package com.assistant.ant.solidlsnake.antassistant.data

import com.assistant.ant.solidlsnake.antassistant.data.net.Api
import com.assistant.ant.solidlsnake.antassistant.data.pref.Store
import okhttp3.Request

// Helper functions

suspend fun SequenceScope<Eff<Nothing, Nothing>>.writeDb(db: Store) =
    yield(Eff.WriteDb(db))

suspend fun <T> SequenceScope<Eff<Nothing, T>>.result(result: T) =
    yield(Eff.Result(result))

suspend fun SequenceScope<Eff<Store, Nothing>>.readDb(): Store =
    Eff.ReadDb().also { dbe -> yield(dbe) }.result

suspend fun SequenceScope<Eff<String, Nothing>>.webRequest(request: Request): String =
    Eff.WebRequest(request).also { dbe -> yield(dbe) }.result

// Executor

suspend fun <T> Sequence<Eff<*, T>>.await(): T {
    for (r in this) {
        when (r) {
            is Eff.WebRequest ->
                r.result = Api.execute(r.r)
            is Eff.ReadDb ->
                r.result = Store.getStore()
            is Eff.WriteDb ->
                Store.setStore(r.db)
            is Eff.Result<T> ->
                return r.x
        }
    }
    error("Illegal state")
}
