package com.assistant.ant.solidlsnake.antassistant.data.common

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

interface Saga<TA> {
    fun hasNext(): Boolean
    suspend fun next(): TA
    suspend fun <T> consume(hk: Typed<*, T>, x: T)
}

class SageScope : Continuation<Unit> {

    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow()
    }

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    var nextStep: Continuation<*>? = null
    var inputValue: Any? = null
    var effect: Any? = null

    suspend fun <TOut> yield(x: Typed<*, TOut>): TOut =
        suspendCoroutineUninterceptedOrReturn { c ->
            effect = x
            nextStep = c
            COROUTINE_SUSPENDED
        }
}

interface Typed<out TI, TO>

fun <T> saga(block: suspend SageScope.() -> Unit): Saga<T> {
    val scope = SageScope()
    scope.nextStep = block.createCoroutineUnintercepted(receiver = scope, completion = scope)

    return object : Saga<T> {

        private var isFirstCal = true
        private var consumed = false

        @Suppress("UNCHECKED_CAST")
        override fun hasNext(): Boolean {
            if (isFirstCal) {
                isFirstCal = false

                val continuation = scope.nextStep!! as Continuation<Any?>
                scope.nextStep = null
                continuation.resume(null)
            } else if (!consumed) {
                innerConsume(null)
            }
            return scope.nextStep != null
        }

        @Suppress("UNCHECKED_CAST")
        override suspend fun next(): T {
            consumed = false
            return scope.effect as T
        }

        override suspend fun <T> consume(hk: Typed<*, T>, x: T) {
            innerConsume(x)
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T> innerConsume(x: T) {
            consumed = true
            scope.inputValue = x

            val continuation = scope.nextStep!! as Continuation<Any?>
            scope.nextStep = null
            continuation.resume(scope.inputValue)
            scope.inputValue = null
        }
    }
}
