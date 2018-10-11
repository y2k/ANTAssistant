package com.assistant.ant.solidlsnake.antassistant

import com.assistant.ant.solidlsnake.antassistant.data.Eff
import com.assistant.ant.solidlsnake.antassistant.data.parser.Parser
import com.assistant.ant.solidlsnake.antassistant.data.pref.Store
import com.assistant.ant.solidlsnake.antassistant.data.repository.PureRepository
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `parse test`() {
        assert(Parser.isLogged("<html><head><title>Информация о счете</title></head></html>"))
    }

    @Test
    fun `auth test`() {
        val auth = PureRepository.auth("login", "password")

        for (r in auth) {
            when (r) {
                is Eff.WebRequest ->
                    r.result = "<html><head><title>Информация о счете</title></head></html>"
                is Eff.ReadDb ->
                    r.result = Store("", "", false, null)
                is Eff.WriteDb -> Unit
                is Eff.Result<*> -> assert(r.x as Boolean)
            }
        }
    }
}
