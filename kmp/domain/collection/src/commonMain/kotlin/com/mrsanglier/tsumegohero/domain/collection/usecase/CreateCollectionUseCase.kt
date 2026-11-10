package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollection
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CreateCollectionUseCase(
    private val repository: CollectionRepository,
) {
    /** @return id of the created collection */
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(name: String): THResult<String> = THResult.catchResult {
        val collection = TsumegoCollection(
            id = Uuid.random().toString(),
            name = name.trim(),
            createdAt = Clock.System.now(),
        )
        repository.upsertCollection(collection)
        collection.id
    }
}
