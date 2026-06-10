package com.mrsanglier.tsumegohero.localdatasources.datasource

import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.localdatasources.room.dao.TsumegoDao
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.localdatasources.room.model.RankCount
import com.mrsanglier.tsumegohero.localdatasources.room.model.RoomTsumego
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTsumegoDatasource(
    private val dao: TsumegoDao,
) {
    suspend fun upsert(tsumegos: List<RawTsumego>) {
        dao.upsert(tsumegos.map(RoomTsumego::fromAppModel))
    }

    fun observeAllGames(): Flow<List<RawTsumego>> =
        dao.observeAllGames().map { roomModels ->
            roomModels.map { it.toAppModel() }
        }

    fun observeGame(id: String): Flow<RawTsumego> =
        dao.observeGame(id).map { roomModel -> roomModel.toAppModel() }

    suspend fun countRanks(): Map<String, Int> =
        dao.countRanks()
            .sortedBy { Rank.safeValueOf(it.rank) }
            .associate { it.rank to it.total }

    suspend fun clean() {
        dao.clean()
    }
}