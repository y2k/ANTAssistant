package com.assistant.ant.solidlsnake.antassistant

import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

class Example4Tests {

    sealed class Eff<I, O> : HK<I, O> {
        class Write(val message: String) : Eff<String, Unit>()
        object Read : Eff<Unit, String>()
        class Result(val result: Int) : Eff<Int, Unit>()
    }

    fun example() = launch_<Eff<*, *>> {
        yield_(Eff.Write("Enter something:"))
        val input = yield_(Eff.Read)
        yield_(Eff.Write("You entered: $input"))
        yield_(Eff.Result(-1))
    }

    @Test(timeout = 1000)
    fun test2() = runBlocking {
        println(">> BEGIN <<<")

        val xs = example()
        while (xs.hasNext()) {
            when (val x = xs.next()) {
                is Eff.Write -> println("[PRINT ]: ${x.message}")
                Eff.Read -> xs.consume(x as Eff.Read, "fake input")
                is Eff.Result -> println("[RESULT]: ${x.result}")
            }
        }

        println(">> END <<<")
    }

    @Ignore
    @Test(timeout = 1000)
    fun test() = runBlocking {
        suspend fun handle(xs: EffResult<Eff<*, *>>) =
            when (val x = xs.next()) {
                is Eff.Write -> println(x.message)
                Eff.Read -> xs.consume(x as Eff.Read, "fake input")
                is Eff.Result -> println("[RESULT]: ${x.result}")
            }

        println(">> BEGIN <<<")
        val xs = example()
        handle(xs)
        handle(xs)
        handle(xs)
        println(">> END <<<")
    }

    class Scope : Continuation<Unit> {

        override fun resumeWith(result: Result<Unit>) {
            result.getOrThrow()
        }

        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        var nextStep: Continuation<*>? = null
        var nextValue: Any? = null
        var input: Any? = null

        suspend fun <TOut> yield_(x: HK<*, TOut>): TOut =
            suspendCoroutineUninterceptedOrReturn { c ->
                input = x
                nextStep = c
                COROUTINE_SUSPENDED
            }
    }

    interface HK<TI, TO>

    fun <T> launch_(block: suspend Scope.() -> Unit): EffResult<T> {
        val scope = Scope()
        scope.nextStep = block.createCoroutineUnintercepted(receiver = scope, completion = scope)

        return object : EffResult<T> {

            override fun hasNext(): Boolean {
                return scope.nextStep != null
            }

            @Suppress("UNCHECKED_CAST")
            override suspend fun next(): T {
                val step = scope.nextStep!! as Continuation<Any?>
                scope.nextStep = null
                step.resume(scope.nextValue)
                scope.nextValue = null
                return scope.input as T
            }

            override fun <T> consume(hk: HK<*, T>, x: T) {
                scope.nextValue = x
            }
        }
    }

    interface EffResult<TA> {
        fun hasNext(): Boolean
        suspend fun next(): TA
        fun <T> consume(hk: HK<*, T>, x: T)
    }
}
