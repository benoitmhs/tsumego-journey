package com.mrsanglier.tsumegohero.domain.collection.usecase

import co.touchlab.kermit.Logger
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.collection.TsumegoCollection
import com.mrsanglier.tsumegohero.domain.collection.utils.SgfZipReader
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * @property collectionId null when no problem could be imported (no collection is kept)
 * @property failedFiles names of the .sgf files that could not be read
 */
data class CollectionImportResult(
    val collectionId: String?,
    val importedCount: Int,
    val failedFiles: List<String>,
)

/** Creates a collection named [name] from a zip archive of .sgf files, one problem per readable file. */
class ImportCollectionUseCase(
    private val importUserTsumegoUseCase: ImportUserTsumegoUseCase,
    private val repository: CollectionRepository,
    private val sgfZipReader: SgfZipReader,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(name: String, zipBytes: ByteArray): THResult<CollectionImportResult> =
        THResult.catchResult {
            val files = sgfZipReader.read(zipBytes)
            if (files.isEmpty()) return@catchResult CollectionImportResult(null, 0, emptyList())

            val collectionId = Uuid.random().toString()
            repository.upsertCollection(
                TsumegoCollection(id = collectionId, name = name.trim(), createdAt = Clock.System.now()),
            )

            val failedFiles = mutableListOf<String>()
            // Problems are displayed by date of addition: one millisecond apart, they keep the order of the archive
            // (dates are stored to the millisecond, a fast import could give several problems the same date)
            val importStart = Clock.System.now()
            files.forEachIndexed { index, file ->
                val result = importUserTsumegoUseCase(
                    collectionId = collectionId,
                    fileName = file.name,
                    sgf = file.content,
                    createdAt = importStart + index.milliseconds,
                )
                if (result is THResult.Failure) {
                    Logger.w { "Cannot import ${file.name}: ${result.error}" }
                    failedFiles += file.name
                }
            }

            val importedCount = files.size - failedFiles.size
            if (importedCount == 0) repository.deleteCollection(collectionId)
            CollectionImportResult(
                collectionId = collectionId.takeIf { importedCount > 0 },
                importedCount = importedCount,
                failedFiles = failedFiles,
            )
        }
}
