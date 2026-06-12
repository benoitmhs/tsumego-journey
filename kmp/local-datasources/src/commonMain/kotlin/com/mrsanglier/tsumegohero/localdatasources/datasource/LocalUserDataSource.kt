package com.mrsanglier.tsumegohero.localdatasources.datasource

import com.mrsanglier.tsumegohero.data.model.user.User
import com.mrsanglier.tsumegohero.localdatasources.room.dao.UserDao
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalUserDataSource(
    private val dao: UserDao,
) {
    suspend fun upsert(user: User) {
        dao.upsert(RoomUser.fromAppModel(user))
    }

    fun observeUser(): Flow<User?> =
        dao.observeUser().map { it?.toAppModel() }

    suspend fun clean() {
        dao.clean()
    }
}
