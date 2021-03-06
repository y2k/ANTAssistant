package com.assistant.ant.solidlsnake.antassistant.data.model.mapper

import com.assistant.ant.solidlsnake.antassistant.data.model.NetUserData
import com.assistant.ant.solidlsnake.antassistant.domain.Mapper
import com.assistant.ant.solidlsnake.antassistant.domain.entity.State
import com.assistant.ant.solidlsnake.antassistant.domain.entity.Tariff
import com.assistant.ant.solidlsnake.antassistant.domain.entity.UserData

object NetUserDataMapper : Mapper<NetUserData, UserData> {
    override fun map(from: NetUserData): UserData {
        val state = State(
                from.state__balance,
                from.state__downloaded,
                from.status,
                from.credit
        )

        val tariff = Tariff(
                from.tariff_name,
                from.tariff_downloadSpeed,
                from.tariff_uploadSpeed,
                from.tariff_price
        )

        return UserData(
                from.accountName,
                from.userId,
                tariff,
                state
        )
    }
}