package com.mrsanglier.tsumegohero.game

import com.mrsanglier.tsumegohero.game.delegate.DeriveTsumegoDelegateImpl
import com.mrsanglier.tsumegohero.game.delegate.GetCropBoardDelegateImpl
import com.mrsanglier.tsumegohero.game.delegate.ParseSgfTsumegoDelegateImpl
import com.mrsanglier.tsumegohero.game.delegate.PlayMoveBackDelegateImpl
import com.mrsanglier.tsumegohero.game.delegate.PlayMoveDelegateImpl
import com.mrsanglier.tsumegohero.game.usecase.GetNextTsumegoIdUseCase
import com.mrsanglier.tsumegohero.game.usecase.ImportTsumegoUseCase
import com.mrsanglier.tsumegohero.game.usecase.NavigateReviewUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayFreeMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayOpponentMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayPlayerMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.PlayReviewMoveUseCase
import com.mrsanglier.tsumegohero.game.usecase.RestartGameUseCase
import com.mrsanglier.tsumegohero.game.usecase.SendGameResultUseCase
import com.mrsanglier.tsumegohero.game.usecase.StartGameUseCase
import com.mrsanglier.tsumegohero.game.usecase.StartReviewUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainGameModule: Module = module {
    // UseCase
    singleOf(::GetNextTsumegoIdUseCase)
    singleOf(::ImportTsumegoUseCase)
    singleOf(::NavigateReviewUseCase)
    singleOf(::PlayFreeMoveUseCase)
    singleOf(::PlayPlayerMoveUseCase)
    singleOf(::PlayReviewMoveUseCase)
    singleOf(::PlayOpponentMoveUseCase)
    singleOf(::RestartGameUseCase)
    singleOf(::SendGameResultUseCase)
    singleOf(::StartGameUseCase)
    singleOf(::StartReviewUseCase)

    // Delegate
    singleOf(::DeriveTsumegoDelegateImpl)
    singleOf(::GetCropBoardDelegateImpl)
    singleOf(::ParseSgfTsumegoDelegateImpl)
    singleOf(::PlayMoveDelegateImpl)
    singleOf(::PlayMoveBackDelegateImpl)
}
