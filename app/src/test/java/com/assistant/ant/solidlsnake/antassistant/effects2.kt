package com.assistant.ant.solidlsnake.antassistant

import org.junit.Test

sealed class Algebra<T> {
    class Write(val message: String) : Algebra<Unit>()
    object Read : Algebra<String>()
}

class F<out T>(val a: T) {
    lateinit var value: Any
}

fun test_(): Sequence<F<Algebra<*>>> = sequence {
//    yield_(Algebra.Write("Hello, write something:"))

    val input: String = yield_(Algebra.Read)

    yield_(Algebra.Write("You entered: $input"))
}

suspend fun <T, R> SequenceScope<F<T>>.yield_(y: T): R {
    val x = F(y)
    yield(x)
    @Suppress("UNCHECKED_CAST")
    return x.value as R
}

class Effects2Tests {

    @Test
    fun test2() {
        println(">>> START <<<")

        val xs: Sequence<F<Algebra<*>>> = test_()

        for (it in xs) {
            when (it.a) {
                is Algebra.Write -> println(it.a.message)
                is Algebra.Read -> it.value = "fake input"
            }
        }

        println(">>> END <<<")
    }

//    @Test
//    fun test1() {
//        println(">>> START <<<")
//
//        test().forEach {
//            when (it) {
//                is Algebra.Write -> println(it.message)
//                is Algebra.Read -> it.f("'fake input'")
//            }
//        }
//
//        println(">>> END <<<")
//    }
}

//

fun <T> ignore(x: T) = Unit