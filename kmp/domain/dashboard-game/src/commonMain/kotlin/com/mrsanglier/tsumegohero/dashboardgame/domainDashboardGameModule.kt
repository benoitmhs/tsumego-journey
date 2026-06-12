package com.mrsanglier.tsumegohero.dashboardgame

import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveDailyStreakUseCase
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveProgressDataUseCase
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveUserUseCase
import com.mrsanglier.tsumegohero.dashboardgame.usecase.UpdateUserRankUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainDashboardGameModule: Module = module {
    singleOf(::ObserveDailyStreakUseCase)
    singleOf(::ObserveProgressDataUseCase)
    singleOf(::ObserveUserUseCase)
    singleOf(::UpdateUserRankUseCase)
}
