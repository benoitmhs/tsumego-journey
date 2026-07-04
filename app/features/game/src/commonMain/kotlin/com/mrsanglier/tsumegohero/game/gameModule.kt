package com.mrsanglier.tsumegohero.game

import com.mrsanglier.tsumegohero.game.game.delegate.BoardViewModelDelegateImpl
import com.mrsanglier.tsumegohero.game.game.delegate.GameViewModelDelegateImpl
import com.mrsanglier.tsumegohero.game.rankestimation.RankEstimationViewModel
import com.mrsanglier.tsumegohero.game.review.ReviewViewModel
import com.mrsanglier.tsumegohero.game.training.TrainingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val gameModule = module {
    singleOf(::BoardViewModelDelegateImpl)
    singleOf(::GameViewModelDelegateImpl)

    viewModelOf(::TrainingViewModel)
    viewModelOf(::RankEstimationViewModel)
    viewModelOf(::ReviewViewModel)
}
