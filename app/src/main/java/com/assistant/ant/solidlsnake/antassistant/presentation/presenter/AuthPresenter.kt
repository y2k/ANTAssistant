package com.assistant.ant.solidlsnake.antassistant.presentation.presenter

import com.assistant.ant.solidlsnake.antassistant.data.await
import com.assistant.ant.solidlsnake.antassistant.data.repository.PureRepository
import com.assistant.ant.solidlsnake.antassistant.presentation.presenter.AuthComponent.ViewModel
import com.assistant.ant.solidlsnake.antassistant.presentation.view.AuthView

/*

type ViewModel = { isProgress : bool, result : bool option }

type Event =
    | Login of string * string
    | LoginResult of bool

let init = { isProgress = false, result = None }, Cmd.none

let update model = function
    | Login (handleAuthorization, password) ->
        { model with isProgress = true },
        Cmd.ofAsync (RepositoryImpl.auth handleAuthorization password) LoginResult
    | LoginResult result ->
        { model with isProgress = false, result = Some result }, Cmd.none

 */

object AuthComponent {

    data class ViewModel(val isProgress: Boolean, val result: Boolean?)

    sealed class Event {
        class Login(val login: String, val password: String) : Event()
        class LoginResult(val result: Boolean) : Event()
    }

    fun init(): Upd<ViewModel, Event> =
        ViewModel(false, null) to null

    fun update(model: ViewModel, event: Event): Upd<ViewModel, Event> = when (event) {
        is Event.Login ->
            model.copy(isProgress = true) to
                suspend {
                    PureRepository.auth(event.login, event.password).await().let(Event::LoginResult)
                }
        is Event.LoginResult ->
            model.copy(isProgress = false, result = event.result) to
                null
    }
}

private fun AuthView.render(model: ViewModel) {
    setProgress(model.isProgress)
    when (model.result) {
        true -> success()
        false -> error()
    }
}

fun AuthPresenter() =
    ElmPresenter(AuthComponent::init, AuthComponent::update, AuthView::render)
