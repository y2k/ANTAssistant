package com.assistant.ant.solidlsnake.antassistant.data

import com.assistant.ant.solidlsnake.antassistant.data.net.Api
import com.assistant.ant.solidlsnake.antassistant.data.pref.Store

suspend fun <T, R : Any> SequenceScope<Eff<*, T>>.yieldEff(eff: Eff<R, T>): R {
    yield(eff)
    return eff.result
}

// Executor

suspend fun <T> Sequence<Eff<*, T>>.await(): T {
    for (r in this) {
        when (r) {
            is Eff.WebRequest ->
                r.consume(Api.execute(r.r))
            is Eff.ReadDb ->
                r.consume(Store.getStore())
            is Eff.WriteDb ->
                Store.setStore(r.db)
            is Eff.Result<T> ->
                return r.x
        }
    }
    error("Illegal state")
}
