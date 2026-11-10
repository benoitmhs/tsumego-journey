package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow

class ObserveUserTsumegoUseCase(
    private val repository: CollectionRepository,
) {
    /** Emits null when the problem does not exist (or was deleted). */
    operator fun invoke(tsumegoId: String): Flow<UserTsumego?> = repository.observeTsumego(tsumegoId)
}
