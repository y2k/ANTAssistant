@file:Suppress("ClassName", "MemberVisibilityCanBePrivate")

package com.assistant.ant.solidlsnake.antassistant

import com.assistant.ant.solidlsnake.antassistant.data.pref.Store
import com.assistant.ant.solidlsnake.antassistant.data.repository.PureRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import org.junit.Test
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

class Example5Test {

    fun auth____(login: String, password: String): Foo__<Boolean> {
        val r = PureRepository.mkLoginRequest(login, password)
        return Foo__.Foo1(r) { body ->
            Foo__.Foo2 { inputDb ->
                val db = PureRepository.saveAuthResult(inputDb, body, login, password)
                Foo__.Foo3(db) {
                    Foo__.Foo4(db.isAuthorized)
                }
            }
        }
    }

    fun auth(login: String, password: String) = run {
        val r = PureRepository.mkLoginRequest(login, password)
        r to { body: String ->
            Unit to { inputDb: Store ->
                val db = PureRepository.saveAuthResult(inputDb, body, login, password)
                db to { _: Unit ->
                    db.isAuthorized
                }
            }
        }
    }

    suspend fun auth_(login: String, password: String) {

        val r = PureRepository.mkLoginRequest(login, password)

        val body: String = foo(r)
        val inputDb: Store = foo_(Unit)

        val db = PureRepository.saveAuthResult(inputDb, body, login, password)

        foo__(db)

    }

    class FooScope : Continuation<Unit> {

        override fun resumeWith(result: Result<Unit>) = result.getOrThrow()
        override val context = EmptyCoroutineContext

        var continuations_: Foo_? = null

        suspend fun <T> foo____(f: (Continuation<T>) -> Foo_): T {
            return suspendCoroutineUninterceptedOrReturn<T> {
                continuations_ = f(it)
                COROUTINE_SUSPENDED
            }
        }
    }

    fun fooScope(block: suspend FooScope.() -> Unit): FooScope {
        val scope = FooScope()
        block.createCoroutineUnintercepted(receiver = scope, completion = scope)
        return scope
    }

    fun auth__(login: String, password: String): FooScope = fooScope {

        val r = PureRepository.mkLoginRequest(login, password)

        println("1.1") // FIXME:
        val body: String = foo____ {
            println("1.3") // FIXME:
            Foo_.Foo1(r, it)
        }
        println("1.2") // FIXME:

        val inputDb: Store = foo____ { Foo_.Foo2(it) }

        val db = PureRepository.saveAuthResult(inputDb, body, login, password)

        foo____<Unit> { Foo_.Foo3(db, it) }

    }

    suspend fun test() {
        auth_("", "")

        val html = suspend {
            println("[REQUEST] ${continuation_.first}")
            "<html><head><title>Информация о счете</title></head></html>"
        }.invoke()

        continuation_.second.resume(html)

        //

        val db = suspend {
            println("[LOAD DB]")
            Store("", "", false, null)
        }.invoke()

        continuation__.second.resume(db)

        //

        suspend {
            println("[SAVE DB] ${continuation___.first}")
        }.invoke()

        continuation___.second.resume(Unit)
    }

    suspend fun test_() {
        auth__("", "")

        while (true) {
            val c = continuations
            continuations = null
            when (c) {
                is Foo.Foo1 -> {
                    val html = suspend {
                        println("[REQUEST] ${c.a}")
                        "<html><head><title>Информация о счете</title></head></html>"
                    }.invoke()

                    c.b.resume(html)
                }
                is Foo.Foo2 -> {
                    val db = suspend {
                        println("[LOAD DB]")
                        Store("", "", false, null)
                    }.invoke()

                    c.b.resume(db)
                }
                is Foo.Foo3 -> {
                    suspend {
                        println("[SAVE DB] ${continuation___.first}")
                    }.invoke()

                    c.b.resume(Unit)
                }
                null -> {
                    println("[END]")
                    return
                }
            }
        }
    }

    @Test(timeout = 1000)
    fun test__() = runBlocking<Unit> {
        println("1") // FIXME:
        val scope = auth__("", "")
        println("2") // FIXME:

        var continue_ = true
        while (continue_) {
            val c = scope.continuations_
            scope.continuations_ = null
            when (c) {
                is Foo_.Foo1 -> {
                    val html = suspend {
                        println("[REQUEST] ${c.a}")
                        "<html><head><title>Информация о счете</title></head></html>"
                    }.invoke()

                    c.b.resume(html)
                }
                is Foo_.Foo2 -> {
                    val db = suspend {
                        println("[LOAD DB]")
                        Store("", "", false, null)
                    }.invoke()

                    c.b.resume(db)
                }
                is Foo_.Foo3 -> {
                    suspend {
                        println("[SAVE DB] ${continuation___.first}")
                    }.invoke()

                    c.b.resume(Unit)
                }
                null -> {
                    println("[END]")
                    continue_ = false
                }
            }
        }
    }

    lateinit var continuation_: Pair<Request, Continuation<String>>
    lateinit var continuation__: Pair<Unit, Continuation<Store>>
    lateinit var continuation___: Pair<Store, Continuation<Unit>>
    var continuations: Foo? = null
//    var continuations_: Foo_? = null

    sealed class Foo__<T> {
        class Foo1<T>(val a: Request, val b: (String) -> Foo__<T>) : Foo__<T>()
        class Foo2<T>(val b: (Store) -> Foo__<T>) : Foo__<T>()
        class Foo3<T>(val a: Store, val b: () -> Foo__<T>) : Foo__<T>()
        class Foo4<T>(val a: T) : Foo__<T>()
    }

    sealed class Foo {
        class Foo1(val a: Request, val b: Continuation<String>) : Foo()
        class Foo2(val a: Unit, val b: Continuation<Store>) : Foo()
        class Foo3(val a: Store, val b: Continuation<Unit>) : Foo()
    }

    sealed class Foo_ {
        class Foo1(val a: Request, val b: Continuation<String>) : Foo_()
        class Foo2(val b: Continuation<Store>) : Foo_()
        class Foo3(val a: Store, val b: Continuation<Unit>) : Foo_()
    }

    suspend fun <T> foo___(f: (Continuation<T>) -> Foo): T {
        return suspendCoroutineUninterceptedOrReturn<T> {
            continuations = f(it)
            COROUTINE_SUSPENDED
        }
    }

    suspend fun foo(r: Request): String {
        return suspendCoroutineUninterceptedOrReturn<String> {
            continuations = Foo.Foo1(r, it)
            continuation_ = r to it
            COROUTINE_SUSPENDED
        }
    }

    suspend fun foo_(unit: Unit): Store {
        return suspendCoroutineUninterceptedOrReturn<Store> {
            continuations = Foo.Foo2(unit, it)
            continuation__ = unit to it
            COROUTINE_SUSPENDED
        }
    }

    suspend fun foo__(db: Store) {
        suspendCoroutineUninterceptedOrReturn<Unit> {
            continuations = Foo.Foo3(db, it)
            continuation___ = db to it
            COROUTINE_SUSPENDED
        }
    }

}
