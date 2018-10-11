package com.assistant.ant.solidlsnake.antassistant.data.net

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object Api {
    const val BASE_URL = "http://cabinet.a-n-t.ru/cabinet.php"

    const val PARAM_ACTION = "action"
    const val ACTION_INFO = "info"
    const val PARAM_USERNAME = "user_name"
    const val PARAM_PASSWORD = "user_pass"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    suspend fun execute(request: Request): String {
        return suspendCancellableCoroutine { coroutine ->
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.body()?.use {
                        val body = it.string()
                        if (body == null) {
                            coroutine.resumeWithException(NullPointerException("There's no body, buddy"))
                        } else {
                            coroutine.resume(body)
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    if (coroutine.isCancelled) return
                    coroutine.resumeWithException(e)
                }
            })

            coroutine.invokeOnCancellation {
                if (coroutine.isCancelled)
                    try {
                        call.cancel()
                    } catch (ex: Throwable) {
                        // Ignore cancel exception
                    }
            }
        }
    }
}
