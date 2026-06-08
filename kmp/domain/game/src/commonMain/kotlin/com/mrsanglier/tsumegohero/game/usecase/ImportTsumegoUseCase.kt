package com.mrsanglier.tsumegohero.game.usecase

import com.mrsanglier.tsumegohero.core.result.THResult
import com.mrsanglier.tsumegohero.data.model.game.RawTsumego
import com.mrsanglier.tsumegohero.game.delegate.ParseSgfTsumegoDelegate
import com.mrsanglier.tsumegohero.game.delegate.ParseSgfTsumegoDelegateImpl
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import kotlin.time.Clock

class ImportTsumegoUseCase(
    private val repository: TsumegoRepository,
    parseSgfTsumegoDelegateImpl: ParseSgfTsumegoDelegateImpl,
) : ParseSgfTsumegoDelegate by parseSgfTsumegoDelegateImpl {
    suspend operator fun invoke(
        fileName: String,
        sgfData: String,
    ): THResult<String> = THResult.catchResult {

        // test parsing
        val tsumego = parseSgfTsumego(sgfData)

        val rawTsumego = RawTsumego(
            id = sgfData.hashCode().toString(),
            name = fileName,
            data = sgfData,
            updatedAt = Clock.System.now(),
            rank = tsumego.rank,
        )

        repository.upsert(listOf(rawTsumego))

        return@catchResult rawTsumego.id
    }
}
