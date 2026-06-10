package com.mrsanglier.tsumegohero.dashboardgame.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.dashboardgame.sgfCollection
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

class PrepopulateTsumegoDBUseCase(
    private val tsumegoRepository: TsumegoRepository,
) {
    suspend operator fun invoke(): THResult<Unit> = THResult.catchResult {
        if (tsumegoRepository.observeAllGames().first().isNotEmpty()) return@catchResult
//        val tsumegos = sgfCollection.mapIndexed { index, data ->
//            RawTsumego(
//                id = data.hashCode().toString(),
//                name = "Tsumego #${index + 1}",
//                data = data,
//                updatedAt = Clock.System.now(),
//            )
//        }
//
//        tsumegoRepository.upsert(tsumegos)
    }
}