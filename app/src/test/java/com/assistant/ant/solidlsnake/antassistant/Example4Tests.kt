package com.assistant.ant.solidlsnake.antassistant

import com.assistant.ant.solidlsnake.antassistant.data.common.Saga
import com.assistant.ant.solidlsnake.antassistant.data.common.Typed
import com.assistant.ant.solidlsnake.antassistant.data.common.saga
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class Example4Tests {

    sealed class Eff<I, O> : Typed<I, O> {
        class Write(val message: String) : Eff<String, Unit>()
        object Read : Eff<Unit, String>()
        class Result(val result: Int) : Eff<Int, Unit>()
    }

    private fun example(): Saga<Eff<*, *>> = saga {
        yield(Eff.Write("Enter something:"))
        val input = yield(Eff.Read)
        yield(Eff.Write("You entered: $input"))
        yield(Eff.Result(-1))
    }

    @Test(timeout = 500)
    fun `async test`() = runBlocking {
        println(">> BEGIN <<<")

        val xs = example()
        while (xs.hasNext()) {
            val x = xs.next()
            when (x) {
                is Eff.Write -> {
                    delay(50)
                    println("[PRINT ]: ${x.message}")
                }
                is Eff.Read -> {
                    delay(50)
                    xs.consume(x, "fake input")
                }
                is Eff.Result -> println("[RESULT]: ${x.result}")
            }
        }

        println(">> END <<<")
    }

    @Test(timeout = 500)
    fun `sync test`() = runBlocking {
        println(">> BEGIN <<<")

        val xs = example()
        while (xs.hasNext()) {
            val x = xs.next()
            when (x) {
                is Eff.Write -> println("[PRINT ]: ${x.message}")
                is Eff.Read -> xs.consume(x, "fake input")
                is Eff.Result -> println("[RESULT]: ${x.result}")
            }
        }

        println(">> END <<<")
    }
}
