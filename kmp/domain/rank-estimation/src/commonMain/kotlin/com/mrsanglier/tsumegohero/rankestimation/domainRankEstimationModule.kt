package com.mrsanglier.tsumegohero.rankestimation

import com.mrsanglier.tsumegohero.domain.common.delegate.GetNextTsumegoFromRankDelegateImpl
import com.mrsanglier.tsumegohero.rankestimation.delegate.ComputeSearchStateDelegateImpl
import com.mrsanglier.tsumegohero.rankestimation.delegate.EstimateLevelDelegateImpl
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetNextRankEstimationTsumegoUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.ObserveRankEstimationProgressUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.SubmitRankEstimationAnswerUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainRankEstimationModule: Module = module {
    // UseCase
    singleOf(::GetNextRankEstimationTsumegoUseCase)
    singleOf(::ObserveRankEstimationProgressUseCase)
    singleOf(::SubmitRankEstimationAnswerUseCase)

    // Delegate
    singleOf(::ComputeSearchStateDelegateImpl)
    singleOf(::EstimateLevelDelegateImpl)
    singleOf(::GetNextTsumegoFromRankDelegateImpl)
}
