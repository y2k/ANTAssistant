package com.assistant.ant.solidlsnake.antassistant.domain.repository

import com.assistant.ant.solidlsnake.antassistant.domain.entity.UserData

interface IRepository {
    /**
     * Проверка залогинен ли пользователь в приложение.
     *
     * @return  true если залогинен
     *          false если нет
     */
    suspend fun isLogged(): Boolean

    /**
     * Авторизация пользователя.
     *
     * @param login имя пользователя
     * @param password пароль пользователя
     *
     * @return  true если вход успешен
     *          false иначе
     */
    suspend fun auth(login: String, password: String): Boolean

    /**
     * Получение информации о пользователе
     */
    suspend fun getUserData(): UserData
}