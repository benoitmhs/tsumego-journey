package com.mrsanglier.tsumegohero.domain.common.delegate

import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.repository.TsumegoRepository

interface GetNextTsumegoFromRankDelegate {
    suspend fun getNextTsumegoFromRank(rank: Rank): RawTsumego?
}

class GetNextTsumegoFromRankDelegateImpl(
    private val tsumegoRepository: TsumegoRepository,
) : GetNextTsumegoFromRankDelegate {

    override suspend fun getNextTsumegoFromRank(rank: Rank): RawTsumego? =
        tsumegoRepository.getNextNeverAttempted(rank)
            ?: tsumegoRepository.getNextNeverSucceeded(rank)
            ?: tsumegoRepository.getOldestAttempted(rank)
}
