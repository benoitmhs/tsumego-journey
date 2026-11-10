package com.mrsanglier.tsumegohero.domain.collection.usecase

import co.touchlab.kermit.Logger
import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.domain.collection.model.CollectionDetail
import com.mrsanglier.tsumegohero.domain.collection.model.UserTsumegoPreview
import com.mrsanglier.tsumegohero.domain.collection.utils.UserTsumegoSgf
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegate
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegateImpl
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveCollectionDetailUseCase(
    private val repository: CollectionRepository,
    getCropBoardDelegateImpl: GetCropBoardDelegateImpl,
) : GetCropBoardDelegate by getCropBoardDelegateImpl {

    /** Emits null when the collection does not exist. */
    operator fun invoke(collectionId: String): Flow<CollectionDetail?> =
        combine(
            repository.observeCollection(collectionId),
            repository.observeTsumegos(collectionId),
        ) { collection, tsumegos ->
            collection?.let {
                CollectionDetail(
                    collection = collection,
                    problems = tsumegos.mapNotNull { it.toPreview() },
                )
            }
        }

    private fun UserTsumego.toPreview(): UserTsumegoPreview? = try {
        val (board, root) = UserTsumegoSgf.read(sgf)
        val hasStones = board.blackStones.isNotEmpty() || board.whiteStones.isNotEmpty()
        UserTsumegoPreview(
            id = id,
            title = title,
            rank = rank,
            boardSize = board.boardSize,
            blackStones = board.blackStones,
            whiteStones = board.whiteStones,
            cropBoard = if (hasStones) getCropBoard(initialBoard = board, root = root) else null,
        )
    } catch (e: Exception) {
        Logger.e(e) { "Unreadable user tsumego $id" }
        null
    }
}
