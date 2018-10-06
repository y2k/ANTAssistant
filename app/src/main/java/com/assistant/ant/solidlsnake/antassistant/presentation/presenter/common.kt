package com.assistant.ant.solidlsnake.antassistant.presentation.presenter

import com.assistant.ant.solidlsnake.antassistant.presentation.view.BaseView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch

class CommonPresenter<Model, Msg, View : BaseView>(
    private val init: () -> Pair<Model, (suspend () -> Msg)?>,
    private val update: (Model, Msg) -> Pair<Model, (suspend () -> Msg)?>,
    private val render: (View, Model) -> Unit
) : BasePresenter<View>() {

    override fun doOnAttach() {
        GlobalScope.launch(Dispatchers.Main) {
            val (state, cmd) = init()
            loop(state, cmd)
        }
    }

    private suspend fun loop(state: Model, cmd: (suspend () -> Msg)?) {
        _view?.let { render(it, state) }

        if (cmd != null) {
            val (state2, cmd2) = update(state, cmd.invoke())
            loop(state2, cmd2)
        }
    }
}
