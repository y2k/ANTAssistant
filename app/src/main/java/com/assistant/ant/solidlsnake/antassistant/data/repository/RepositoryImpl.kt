package com.assistant.ant.solidlsnake.antassistant.data.repository

import com.assistant.ant.solidlsnake.antassistant.data.Eff
import com.assistant.ant.solidlsnake.antassistant.data.model.mapper.NetUserDataMapper
import com.assistant.ant.solidlsnake.antassistant.data.net.Api.ACTION_INFO
import com.assistant.ant.solidlsnake.antassistant.data.net.Api.BASE_URL
import com.assistant.ant.solidlsnake.antassistant.data.net.Api.PARAM_ACTION
import com.assistant.ant.solidlsnake.antassistant.data.net.Api.PARAM_PASSWORD
import com.assistant.ant.solidlsnake.antassistant.data.net.Api.PARAM_USERNAME
import com.assistant.ant.solidlsnake.antassistant.data.parser.Parser
import com.assistant.ant.solidlsnake.antassistant.data.pref.Store
import com.assistant.ant.solidlsnake.antassistant.data.yieldEff
import com.assistant.ant.solidlsnake.antassistant.domain.entity.UserData
import okhttp3.FormBody
import okhttp3.Request

object PureRepository {

    fun isAuthorized(): Sequence<Eff<*, Boolean>> = sequence {
        val db = yieldEff(Eff.ReadDb())
        yieldEff(Eff.Result(db.isAuthorized))
    }

    fun auth(login: String, password: String): Sequence<Eff<*, Boolean>> = sequence {
        val body = yieldEff(Eff.WebRequest(mkLoginRequest(login, password)))
        val inputDb = yieldEff(Eff.ReadDb())

        val db = saveAuthResult(inputDb, body, login, password)

        yieldEff(Eff.WriteDb(db))
        yieldEff(Eff.Result(db.isAuthorized))
    }

    fun getUserData(): Sequence<Eff<*, UserData>> = sequence {
        val db = yieldEff(Eff.ReadDb())
        val body = yieldEff(Eff.WebRequest(mkLoginRequest(db.login, db.password)))
        val userData = Parser.userData(body).let(NetUserDataMapper::map)
        yieldEff(Eff.Result(userData))
    }

    private fun saveAuthResult(db: Store, body: String, login: String, password: String): Store {
        val result = Parser.isLogged(body)
        val db =
            if (result) {
                db.copy(login = login, password = password)
            } else {
                db
            }
        return db.copy(isAuthorized = result)
    }

    private fun mkLoginRequest(login: String, password: String): Request =
        Request.Builder()
            .url(BASE_URL)
            .post(
                FormBody.Builder()
                    .add(PARAM_ACTION, ACTION_INFO)
                    .add(PARAM_USERNAME, login)
                    .add(PARAM_PASSWORD, password)
                    .build()
            )
            .build()
}
