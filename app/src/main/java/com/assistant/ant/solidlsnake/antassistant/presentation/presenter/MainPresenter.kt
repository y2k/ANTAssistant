package com.assistant.ant.solidlsnake.antassistant.presentation.presenter

import com.assistant.ant.solidlsnake.antassistant.data.repository.RepositoryImpl
import com.assistant.ant.solidlsnake.antassistant.domain.entity.UserData
import com.assistant.ant.solidlsnake.antassistant.presentation.view.MainView

data class ViewModel(val progress: Boolean, val data: UserData?)
sealed class Event {
    class NewData(val data: UserData) : Event()
}

object MainComponent {

    fun init(): Pair<ViewModel, (suspend () -> Event)?> =
        ViewModel(true, null) to
            suspend { RepositoryImpl.getUserData().let(Event::NewData) }

    fun update(state: ViewModel, data: Event): Pair<ViewModel, (suspend () -> Event)?> =
        state.copy(progress = false, data = (data as Event.NewData).data) to null
}

private fun MainView.render(state: ViewModel) {
    setProgress(state.progress)
    state.data?.let(::showUserData)
}

fun MainPresenter(): BasePresenter<MainView> =
    CommonPresenter(MainComponent::init, MainComponent::update, MainView::render)
