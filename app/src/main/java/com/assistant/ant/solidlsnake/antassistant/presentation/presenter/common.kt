package com.assistant.ant.solidlsnake.antassistant.presentation.presenter

import com.assistant.ant.solidlsnake.antassistant.presentation.view.BaseView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch

class ElmPresenter<Model, Msg, View : BaseView>(
    private val init: () -> Pair<Model, (suspend () -> Msg)?>,
    private val update: (Model, Msg) -> Pair<Model, (suspend () -> Msg)?>,
    private val render: (View, Model) -> Unit
) : BasePresenter<View>() {

    private var model: Model? = null

    fun update(msg: Msg) {
        GlobalScope.launch(Dispatchers.Main) {
            loop(model!!, suspend { msg })
        }
    }

    override fun doOnAttach() {
        if (model == null) {
            GlobalScope.launch(Dispatchers.Main) {
                val (state, cmd) = init()
                loop(state, cmd)
            }
        } else {
            render(_view!!, model!!)
        }
    }

    private suspend fun loop(model: Model, cmd: (suspend () -> Msg)?) {
        this.model = model
        _view?.let { render(it, model) }

        if (cmd != null) {
            val (state2, cmd2) = update(model, cmd.invoke())
            loop(state2, cmd2)
        }
    }
}

typealias Upd<ViewModel, Event> = Pair<ViewModel, (suspend () -> Event)?>
