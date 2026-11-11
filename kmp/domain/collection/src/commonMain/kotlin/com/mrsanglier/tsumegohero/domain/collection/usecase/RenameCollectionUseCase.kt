package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlinx.coroutines.flow.first

class RenameCollectionUseCase(
    private val repository: CollectionRepository,
) {
    /** A blank name is ignored: the collection keeps its name. */
    suspend operator fun invoke(collectionId: String, name: String): THResult<Unit> = THResult.catchResult {
        val newName = name.trim()
        if (newName.isEmpty()) return@catchResult
        val collection = repository.observeCollection(collectionId).first()
            ?: throw THGameError.Code.SgfFormatNotSupported.toError("Collection $collectionId not found")
        if (collection.name != newName) repository.upsertCollection(collection.copy(name = newName))
    }
}
