package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.repository.CollectionRepository

class DeleteCollectionUseCase(
    private val repository: CollectionRepository,
) {
    /** Deletes the collection with all its problems. */
    suspend operator fun invoke(collectionId: String): THResult<Unit> = THResult.catchResult {
        repository.deleteCollection(collectionId)
    }
}
