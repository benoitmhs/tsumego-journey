package com.mrsanglier.tsumegohero.dashboardgame.usecase

import com.mrsanglier.tsumegohero.dashboardgame.model.TsumegoItem
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class GetTsumegoItemUseCase(
    private val repository: TsumegoRepository,
) {
    operator fun invoke(): Flow<List<TsumegoItem>> =
        repository.observeAllGames()
            .distinctUntilChanged()
            .map { list ->
                list.map { sgf ->
                    TsumegoItem(
                        title = sgf.name,
                        tsumegoId = sgf.id,
                        updatedAt = sgf.updatedAt,
                        rank = sgf.rank,
                    )
                }
                    .sortedByDescending { it.updatedAt }
            }
            .distinctUntilChanged()
}