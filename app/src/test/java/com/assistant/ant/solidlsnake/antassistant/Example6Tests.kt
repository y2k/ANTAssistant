package com.assistant.ant.solidlsnake.antassistant

import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

class FreeScope : Continuation<Any> {

    private lateinit var next: Continuation<Any>
    private lateinit var program: FaceProgram<*>

    override val context: CoroutineContext = EmptyCoroutineContext
    override fun resumeWith(result: Result<Any>) {
        result.getOrThrow()
    }

    suspend fun bind_(p: FaceProgram<*>) {
        program = p

        suspendCoroutineUninterceptedOrReturn<Any> { c ->
            next = c

            bind<Any, Any>(p as FaceProgram<Any>, {
                next.resume<Any>(it as Any)
            })


            COROUTINE_SUSPENDED
        }
    }
}

fun free(f: suspend FreeScope.() -> Unit) {
    val scope = FreeScope()
    f.createCoroutineUnintercepted(scope, scope)
}

sealed class FaceInstruction<a> {
    class ReadLine<a>(val x: Unit, val next: (String) -> a) : FaceInstruction<a>()
    class WriteLine<a>(val x: String, val next: (Unit) -> a) : FaceInstruction<a>()
}

private fun <a, b> mapI(f: (a) -> b, x: FaceInstruction<a>): FaceInstruction<b> = when (x) {
    is FaceInstruction.ReadLine -> FaceInstruction.ReadLine(x.x) { f(x.next(it)) }
    is FaceInstruction.WriteLine -> FaceInstruction.WriteLine(x.x) { f(x.next(it)) }
}

sealed class FaceProgram<a> {
    class Free<a>(val x: FaceInstruction<FaceProgram<a>>) : FaceProgram<a>()
    class Pure<a>(val x: a) : FaceProgram<a>()
}

private fun <a, b> bind(x: FaceProgram<a>, f: (a) -> FaceProgram<b>): FaceProgram<b> = when (x) {
    is FaceProgram.Free -> {
        val q = { it: FaceProgram<a> -> bind(it, f) }
        val w = mapI(q, x.x)
        FaceProgram.Free(w)
    }
    is FaceProgram.Pure -> f(x.x)
}

private fun getLine(): FaceProgram.Free<String> =
    FaceProgram.Free<String>(FaceInstruction.ReadLine(Unit) { FaceProgram.Pure(it) })

private fun putLine(x: String): FaceProgram.Free<Unit> =
    FaceProgram.Free<Unit>(FaceInstruction.WriteLine(x) { FaceProgram.Pure(Unit) })

private suspend fun <a> interpret(x: FaceProgram<a>): a = when (x) {
    is FaceProgram.Pure -> x.x
    is FaceProgram.Free -> when (val y = x.x) {
        is FaceInstruction.ReadLine -> interpret(y.next("[Mock input #${(Math.random() * 1000).toInt()}]"))
        is FaceInstruction.WriteLine -> interpret(y.next(println(y.x)))
    }
}

class Example6Tests {

    @Test
    fun main() = runBlocking<Unit> {
        val r = interpret(makeGame())
        println("Interpret result = $r")
    }

    private fun makeGame(): FaceProgram<String> {
        return bind(putLine("Enter your name:")) {
            bind(getLine()) { name ->
                bind(putLine("Hello, $name")) {
                    bind(getLine()) { FaceProgram.Pure(it.toUpperCase()) }
                }
            }
        }
    }
}
