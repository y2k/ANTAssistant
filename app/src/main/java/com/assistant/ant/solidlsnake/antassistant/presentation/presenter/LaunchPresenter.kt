package com.assistant.ant.solidlsnake.antassistant.presentation.presenter

import com.assistant.ant.solidlsnake.antassistant.domain.interactor.IsLogged
import com.assistant.ant.solidlsnake.antassistant.presentation.view.LaunchView
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main

class LaunchPresenter : BasePresenter<LaunchView>() {
    private val isLoggedUseCase = IsLogged()

    override fun doOnAttach() {
        checkAuth()
    }

    private fun checkAuth() = GlobalScope.launch(Dispatchers.Main) {
        val isLogged = withContext(Dispatchers.IO) { isLoggedUseCase.check() }

        if (isLogged) {
            _view?.openMainScreen()
        } else {
            _view?.openAuthScreen()
        }
    }
}