package com.mrsanglier.tsumegohero.repository

import com.mrsanglier.tsumegohero.data.model.user.User
import com.mrsanglier.tsumegohero.localdatasources.datasource.LocalUserDataSource
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val localUserDataSource: LocalUserDataSource,
) {
    suspend fun upsert(user: User) {
        localUserDataSource.upsert(user)
    }

    fun observeUser(): Flow<User?> =
        localUserDataSource.observeUser()
}
