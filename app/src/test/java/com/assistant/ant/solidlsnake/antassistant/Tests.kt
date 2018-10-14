package com.assistant.ant.solidlsnake.antassistant

import com.assistant.ant.solidlsnake.antassistant.data.common.Eff
import com.assistant.ant.solidlsnake.antassistant.data.parser.Parser
import com.assistant.ant.solidlsnake.antassistant.data.pref.Store
import com.assistant.ant.solidlsnake.antassistant.data.repository.PureRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

class Tests {

    @Test
    fun `parse test`() {
        assert(Parser.isLogged(HTML))
    }

    @Test
    fun `auth test`() = runBlocking {
        val auth = PureRepository.auth("login", "password")

        while (auth.hasNext()) {
            val r = auth.next()
            when (r) {
                is Eff.WebRequest -> auth.consume(r, "<html><head><title>Информация о счете</title></head></html>")
                is Eff.ReadDb -> auth.consume(r, Store("", "", false, null))
                is Eff.WriteDb -> Unit
                is Eff.Result -> assert(r.x)
            }
        }
    }

    companion object {
        private const val HTML = "<html><head><title>Информация о счете</title></head></html>"
    }
}
