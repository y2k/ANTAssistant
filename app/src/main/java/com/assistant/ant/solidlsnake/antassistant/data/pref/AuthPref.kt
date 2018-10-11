package com.assistant.ant.solidlsnake.antassistant.data.pref

import com.assistant.ant.solidlsnake.antassistant.domain.entity.UserData
import com.chibatching.kotpref.KotprefModel

@Deprecated("")
object AuthPref : KotprefModel() {
    val isLogged: Boolean
        get() = login.isNotEmpty() && password.isNotEmpty()

    var login by stringPref(default = "")
    var password by stringPref(default = "")
}

data class Store(val login: String, val password: String, val isAuthorized: Boolean, val userData: UserData?) {

    companion object {

        @Volatile
        private var store: Store = Store("", "", false, null)

        suspend fun getStore(): Store = store

        suspend fun dispatch(db: Store): Store = TODO()

        fun setStore(store: Store) {
            this.store = store
        }
    }
}
