package com.assistant.ant.solidlsnake.antassistant.domain.interactor

import com.assistant.ant.solidlsnake.antassistant.data.await
import com.assistant.ant.solidlsnake.antassistant.data.repository.PureRepository

class IsLogged : UseCase {
    suspend fun check(): Boolean {
        return PureRepository.isAuthorized().await()
    }
}
