package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollectionSummary
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectionsUseCase(
    private val repository: CollectionRepository,
) {
    operator fun invoke(): Flow<List<TsumegoCollectionSummary>> =
        repository.observeCollections()
}
