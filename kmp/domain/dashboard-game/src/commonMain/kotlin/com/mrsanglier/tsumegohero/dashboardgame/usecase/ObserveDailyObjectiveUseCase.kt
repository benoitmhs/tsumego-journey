package com.mrsanglier.tsumegohero.dashboardgame.usecase

import com.mrsanglier.tsumegohero.data.model.objective.DailyObjective
import com.mrsanglier.tsumegohero.domain.common.delegate.ObserveDailyObjectiveDelegate
import com.mrsanglier.tsumegohero.domain.common.delegate.ObserveDailyObjectiveDelegateImpl
import kotlinx.coroutines.flow.Flow

class ObserveDailyObjectiveUseCase(
    observeDailyObjectiveDelegateImpl: ObserveDailyObjectiveDelegateImpl,
) : ObserveDailyObjectiveDelegate by observeDailyObjectiveDelegateImpl {

    operator fun invoke(): Flow<DailyObjective> = observeDailyObjective()
}
