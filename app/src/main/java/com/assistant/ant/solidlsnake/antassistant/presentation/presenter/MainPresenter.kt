package com.assistant.ant.solidlsnake.antassistant.presentation.presenter

import com.assistant.ant.solidlsnake.antassistant.data.await
import com.assistant.ant.solidlsnake.antassistant.data.repository.PureRepository
import com.assistant.ant.solidlsnake.antassistant.domain.entity.UserData
import com.assistant.ant.solidlsnake.antassistant.presentation.presenter.MainComponent.ViewModel
import com.assistant.ant.solidlsnake.antassistant.presentation.view.MainView

object MainComponent {

    fun init(): Upd<ViewModel, Event> =
        ViewModel(true, null) to
            suspend {
                PureRepository.getUserData().await().let(Event::NewData)
            }

    fun update(model: ViewModel, event: Event): Upd<ViewModel, Event> =
        model.copy(progress = false, data = (event as Event.NewData).data) to null

    data class ViewModel(val progress: Boolean, val data: UserData?)

    sealed class Event {
        class NewData(val data: UserData) : Event()
    }
}

private fun MainView.render(model: ViewModel) {
    setProgress(model.progress)
    model.data?.let(::showUserData)
}

fun MainPresenter(): BasePresenter<MainView> =
    ElmPresenter(MainComponent::init, MainComponent::update, MainView::render)
