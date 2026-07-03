package com.mrsanglier.tsumegohero.rankestimation

import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeFinalLevelDelegate
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetNextRankEstimationTsumegoUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetRankEstimationResultUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.SubmitRankEstimationAnswerUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainRankEstimationModule: Module = module {
    // UseCase
    singleOf(::GetNextRankEstimationTsumegoUseCase)
    singleOf(::SubmitRankEstimationAnswerUseCase)
    singleOf(::GetRankEstimationResultUseCase)

    // Delegate
    singleOf(::ComputeFinalLevelDelegate)
}
