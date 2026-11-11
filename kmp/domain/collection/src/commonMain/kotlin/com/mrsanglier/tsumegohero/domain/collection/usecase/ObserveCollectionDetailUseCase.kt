package com.mrsanglier.tsumegohero.domain.collection.usecase

import co.touchlab.kermit.Logger
import com.mrsanglier.tsumegohero.data.model.collection.UserTsumego
import com.mrsanglier.tsumegohero.domain.collection.model.CollectionDetail
import com.mrsanglier.tsumegohero.domain.collection.utils.PreviewGeometry
import com.mrsanglier.tsumegohero.domain.collection.utils.UserTsumegoPreviewCache
import com.mrsanglier.tsumegohero.domain.collection.utils.UserTsumegoSgf
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegate
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegateImpl
import com.mrsanglier.tsumegohero.repository.CollectionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Collection and the previews of its problems. Reading every SGF is costly with large collections:
 * it runs on [defaultDispatcher], and a problem is read again only when its SGF changed.
 */
class ObserveCollectionDetailUseCase(
    private val repository: CollectionRepository,
    getCropBoardDelegateImpl: GetCropBoardDelegateImpl,
    private val defaultDispatcher: CoroutineDispatcher,
) : GetCropBoardDelegate by getCropBoardDelegateImpl {

    /** Emits null when the collection does not exist. */
    operator fun invoke(collectionId: String): Flow<CollectionDetail?> = flow {
        // One cache per collection of the flow: only this flow uses it
        val cache = UserTsumegoPreviewCache(::buildGeometry)
        emitAll(
            combine(
                repository.observeCollection(collectionId),
                repository.observeTsumegos(collectionId),
            ) { collection, tsumegos ->
                collection?.let {
                    CollectionDetail(
                        collection = collection,
                        problems = cache.previews(tsumegos),
                    )
                }
            },
        )
    }.flowOn(defaultDispatcher)

    private fun buildGeometry(tsumego: UserTsumego): PreviewGeometry? = try {
        val (board, root) = UserTsumegoSgf.read(tsumego.sgf)
        val hasStones = board.blackStones.isNotEmpty() || board.whiteStones.isNotEmpty()
        PreviewGeometry(
            boardSize = board.boardSize,
            blackStones = board.blackStones,
            whiteStones = board.whiteStones,
            cropBoard = if (hasStones) getCropBoard(initialBoard = board, root = root) else null,
        )
    } catch (e: Exception) {
        Logger.e(e) { "Unreadable user tsumego ${tsumego.id}" }
        null
    }
}
