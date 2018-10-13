package com.assistant.ant.solidlsnake.antassistant

import com.assistant.ant.solidlsnake.antassistant.data.Eff
import com.assistant.ant.solidlsnake.antassistant.data.parser.Parser
import com.assistant.ant.solidlsnake.antassistant.data.pref.Store
import com.assistant.ant.solidlsnake.antassistant.data.repository.PureRepository
import org.junit.Test

private const val HTML = "<html><head><title>Информация о счете</title></head></html>"

class Tests {

    @Test
    fun `parse test`() {
        assert(Parser.isLogged(HTML))
    }

    @Test
    fun `auth test`() {
        val auth = PureRepository.auth("login", "password")

        for (r in auth) {
            when (r) {
                is Eff.WebRequest -> r.consume("<html><head><title>Информация о счете</title></head></html>")
                is Eff.ReadDb -> r.consume(Store("", "", false, null))
                is Eff.WriteDb -> r.consume(Unit)
                is Eff.Result<*> -> {
                    r.consume(Unit)
                    assert(r.x as Boolean)
                }
            }
        }
    }
}
