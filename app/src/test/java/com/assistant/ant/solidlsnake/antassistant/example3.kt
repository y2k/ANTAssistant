package com.assistant.ant.solidlsnake.antassistant

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.experimental.ExperimentalTypeInference

class Example3 {

    fun example() = iterator_ {
        yield(Algebra.Write("Hello, write something:"))
        val input: String = yield_(Algebra.Read)

        val input_: Int = yield_(Algebra.Read)

        yield(Algebra.Write("TODO"))
    }

    fun test() {

        val xs = example()

//        xs.hasNext()

//        for (x in xs) {
//            when (x) {
//                is Algebra.Write -> println(x.message)
//                Algebra.Read -> xs.consume("input type")
//            }
//        }

    }

}

//
// Library
//

//inline fun <T> Sequence_(crossinline iterator: () -> Iterator<T>): Sequence_<T> =
//    object : Sequence_<T> {
//        override fun iterator(): Iterator<T> = iterator()
//    }

@UseExperimental(ExperimentalTypeInference::class)
@SinceKotlin("1.3")
fun <T> iterator_(@BuilderInference block: suspend SequenceScope_<T>.() -> Unit): SequenceBuilderIterator_<T> {
    val iterator = SequenceBuilderIterator_<T>()
    iterator.nextStep = block.createCoroutineUnintercepted(receiver = iterator, completion = iterator)
    return iterator
}

interface Iterator_<out T> {

    operator fun next(): T

    suspend fun consume(x: Any)

//    operator fun hasNext(): Boolean
}

private typealias State = Int

private const val State_NotReady: State = 0
private const val State_ManyNotReady: State = 1
private const val State_ManyReady: State = 2
private const val State_Ready: State = 3
private const val State_Done: State = 4
private const val State_Failed: State = 5

class SequenceBuilderIterator_<T> : SequenceScope_<T>(), Iterator_<T>, Continuation<Unit> {

    override suspend fun consume(x: Any) {
        TODO()
    }

    private var state = State_NotReady
    private var nextValue: T? = null
    private var nextIterator: Iterator<T>? = null
    var nextStep: Continuation<Unit>? = null

//    override fun hasNext(): Boolean {
//        while (true) {
//            when (state) {
//                State_NotReady -> {
//                }
//                State_ManyNotReady ->
//                    if (nextIterator!!.hasNext()) {
//                        state = State_ManyReady
//                        return true
//                    } else {
//                        nextIterator = null
//                    }
//                State_Done -> return false
//                State_Ready, State_ManyReady -> return true
//                else -> throw exceptionalState()
//            }
//
//            state = State_Failed
//            val step = nextStep!!
//            nextStep = null
//            step.resume(Unit)
//        }
//    }

    override fun next(): T {
        return when (state) {
            State_NotReady, State_ManyNotReady -> nextNotReady()
            State_ManyReady -> {
                state = State_ManyNotReady
                nextIterator!!.next()
            }
            State_Ready -> {
                state = State_NotReady
                @Suppress("UNCHECKED_CAST")
                val result = nextValue as T
                nextValue = null
                result
            }
            else -> throw exceptionalState()
        }
    }

    private fun nextNotReady(): T {
//        if (!hasNext()) throw NoSuchElementException() else return next()
        TODO()
    }

    private fun exceptionalState(): Throwable = when (state) {
        State_Done -> NoSuchElementException()
        State_Failed -> IllegalStateException("Iterator has failed.")
        else -> IllegalStateException("Unexpected state of the iterator: $state")
    }

    override suspend fun yield(value: T) {
        nextValue = value
        state = State_Ready
        return suspendCoroutineUninterceptedOrReturn { c ->
            nextStep = c
            COROUTINE_SUSPENDED
        }
    }

    override suspend fun <R> yield_(value: T): R {
        yield(value)

        TODO()
    }

//    override suspend fun yieldAll(iterator: Iterator<T>) {
//        if (!iterator.hasNext()) return
//        nextIterator = iterator
//        state = State_ManyReady
//        return suspendCoroutineUninterceptedOrReturn { c ->
//            nextStep = c
//            COROUTINE_SUSPENDED
//        }
//    }

    // Completion continuation implementation
    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow() // just rethrow exception if it is there
        state = State_Done
    }

    override val context: CoroutineContext
        get() = EmptyCoroutineContext
}

@RestrictsSuspension
@SinceKotlin("1.3")
abstract class SequenceScope_<in T> internal constructor() {
    abstract suspend fun yield(value: T)
    abstract suspend fun <R> yield_(value: T): R

//    abstract suspend fun yieldAll(iterator: Iterator<T>)
//
//    suspend fun yieldAll(elements: Iterable<T>) {
//        if (elements is Collection && elements.isEmpty()) return
//        return yieldAll(elements.iterator())
//    }
//
//    suspend fun yieldAll(sequence: Sequence<T>) = yieldAll(sequence.iterator())
}
