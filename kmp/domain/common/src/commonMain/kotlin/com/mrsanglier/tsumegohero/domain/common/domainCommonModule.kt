package com.mrsanglier.tsumegohero.domain.common

import com.mrsanglier.tsumegohero.domain.common.delegate.GetNextTsumegoFromRankDelegateImpl
import com.mrsanglier.tsumegohero.domain.common.delegate.GetProgressDataDelegateImpl
import com.mrsanglier.tsumegohero.domain.common.delegate.ObserveDailyObjectiveDelegateImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainCommonModule: Module = module {
    //Delegate
    singleOf(::GetProgressDataDelegateImpl)
    singleOf(::GetNextTsumegoFromRankDelegateImpl)
    singleOf(::ObserveDailyObjectiveDelegateImpl)
}
