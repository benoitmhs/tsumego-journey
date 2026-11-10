package com.mrsanglier.tsumegohero.domain.collection.usecase

import com.mrsanglier.tsumegohero.core.error.THGameError
import com.mrsanglier.tsumegohero.core.error.toError
import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.domain.collection.model.TsumegoDraft
import com.mrsanglier.tsumegohero.domain.collection.utils.UserTsumegoSgf
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlinx.coroutines.flow.first
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SaveUserTsumegoUseCase(
    private val repository: CollectionRepository,
) {
    /**
     * Adds the drafted problem to the collection, or replaces the problem [tsumegoId] when set.
     * @return id of the saved problem
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        collectionId: String,
        tsumegoId: String?,
        draft: TsumegoDraft,
        title: String?,
        rank: Rank?,
        notes: String?,
    ): THResult<String> = THResult.catchResult {
        if (!draft.hasCorrectSolution) {
            throw THGameError.Code.SgfFormatNotSupported.toError("A correct solution is required")
        }

        val existing = tsumegoId?.let { repository.observeTsumego(it).first() }
        val tsumego = UserTsumego(
            id = existing?.id ?: Uuid.random().toString(),
            collectionId = collectionId,
            title = title?.trim()?.takeIf { it.isNotEmpty() },
            rank = rank,
            notes = notes?.trim()?.takeIf { it.isNotEmpty() },
            sgf = UserTsumegoSgf.write(draft, rank),
            createdAt = existing?.createdAt ?: Clock.System.now(),
        )
        repository.upsertTsumego(tsumego)
        tsumego.id
    }
}
