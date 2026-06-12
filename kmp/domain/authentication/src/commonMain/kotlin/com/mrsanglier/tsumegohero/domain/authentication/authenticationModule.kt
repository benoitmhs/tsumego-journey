package com.mrsanglier.tsumegohero.domain.authentication

import com.mrsanglier.tsumegohero.domain.authentication.delegate.LogoutCleanDelegateImpl
import com.mrsanglier.tsumegohero.domain.authentication.usecase.InitUserUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainAuthenticationModule: Module = module {
    singleOf(::InitUserUseCase)
    singleOf(::LogoutCleanDelegateImpl)
}
