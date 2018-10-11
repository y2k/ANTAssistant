package com.assistant.ant.solidlsnake.antassistant.data

import com.assistant.ant.solidlsnake.antassistant.data.pref.Store
import okhttp3.Request

@Suppress("unused")
sealed class Eff<TR : Any, out T> {

    class WebRequest(val r: Request) : Eff<String, Nothing>()

    class ReadDb : Eff<Store, Nothing>()

    class WriteDb(val db: Store) : Eff<Nothing, Nothing>()

    class Result<T>(val x: T) : Eff<Nothing, T>()

    lateinit var result: TR
}
