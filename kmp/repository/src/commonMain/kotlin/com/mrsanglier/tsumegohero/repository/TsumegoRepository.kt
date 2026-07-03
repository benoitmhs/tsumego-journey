package com.mrsanglier.tsumegohero.repository

import com.mrsanglier.tsumegohero.localdatasources.datasource.LocalTsumegoDatasource
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.data.model.game.Rank
import kotlinx.coroutines.flow.Flow

class TsumegoRepository(
    private val localTsumegoDatasource: LocalTsumegoDatasource,
) {
    suspend fun upsert(tsumegos: List<RawTsumego>) {
        localTsumegoDatasource.upsert(tsumegos)
    }

    fun observeAllGames(): Flow<List<RawTsumego>> =
        localTsumegoDatasource.observeAllGames()

    fun observeGame(id: String): Flow<RawTsumego> =
        localTsumegoDatasource.observeGame(id)

    suspend fun getNextNeverAttempted(rank: Rank): RawTsumego? =
        localTsumegoDatasource.getNextNeverAttempted(rank)

    suspend fun getNextNeverSucceeded(rank: Rank): RawTsumego? =
        localTsumegoDatasource.getNextNeverSucceeded(rank)

    suspend fun getOldestAttempted(rank: Rank): RawTsumego? =
        localTsumegoDatasource.getOldestAttempted(rank)

    suspend fun countRanks(): Map<String, Int> =
        localTsumegoDatasource.countRanks()

    suspend fun clean() {
        localTsumegoDatasource.clean()
    }
}
