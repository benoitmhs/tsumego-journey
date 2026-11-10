package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.repository.CollectionRepository

class DeleteUserTsumegoUseCase(
    private val repository: CollectionRepository,
) {
    suspend operator fun invoke(tsumegoId: String): THResult<Unit> = THResult.catchResult {
        repository.deleteTsumego(tsumegoId)
    }
}
