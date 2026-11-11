package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.domain.collection.utils.UserTsumegoSgf
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Adds a problem from an .sgf file to a collection. The file content is kept as is.
 * Title: the SGF game name (`GN`), else [fileName]. Difficulty: the rank comment of the root node, if valid.
 */
class ImportUserTsumegoUseCase(
    private val repository: CollectionRepository,
) {
    /** @return id of the imported problem */
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        collectionId: String,
        fileName: String,
        sgf: String,
        createdAt: Instant = Clock.System.now(),
    ): THResult<String> = THResult.catchResult {
        val content = sgf.trim()
        val board = try {
            UserTsumegoSgf.read(content).first
        } catch (e: Exception) {
            throw THGameError.Code.SgfFormatNotSupported.toError("Unreadable sgf", fileName, e)
        }
        if (board.blackStones.isEmpty() && board.whiteStones.isEmpty()) {
            throw THGameError.Code.SgfFormatNotSupported.toError("No initial stones", fileName)
        }

        val tsumego = UserTsumego(
            id = Uuid.random().toString(),
            collectionId = collectionId,
            title = UserTsumegoSgf.readGameName(content) ?: fileName.trim().takeIf { it.isNotEmpty() },
            rank = UserTsumegoSgf.readRank(content),
            notes = null,
            sgf = content,
            createdAt = createdAt,
        )
        repository.upsertTsumego(tsumego)
        tsumego.id
    }
}
