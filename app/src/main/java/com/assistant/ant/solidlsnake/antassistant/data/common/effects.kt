package com.assistant.ant.solidlsnake.antassistant.data.common

import com.assistant.ant.solidlsnake.antassistant.data.net.Api
import com.assistant.ant.solidlsnake.antassistant.data.pref.Store
import okhttp3.Request

sealed class Eff<out T> {

    class WebRequest(val r: Request) : Eff<Nothing>(), Typed<Nothing, String>

    object ReadDb : Eff<Nothing>(), Typed<Nothing, Store>

    class WriteDb(val db: Store) : Eff<Nothing>(), Typed<Nothing, Unit>

    class Result<T>(val x: T) : Eff<T>(), Typed<T, Unit>

}

suspend fun <T> Saga<Eff<T>>.await(): T {
    while (hasNext()) {
        when (val x = next()) {
            is Eff.WebRequest -> consume(x, Api.execute(x.r))
            is Eff.ReadDb -> consume(x, Store.getStore())
            is Eff.WriteDb -> Store.setStore(x.db)
            is Eff.Result -> return x.x
        }
    }
    error("Illegal state")
}
