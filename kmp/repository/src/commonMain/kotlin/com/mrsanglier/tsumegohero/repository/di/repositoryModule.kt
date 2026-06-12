package com.mrsanglier.tsumegohero.repository.di

import com.mrsanglier.tsumegohero.repository.AttemptRepository
import com.mrsanglier.tsumegohero.repository.TsumegoRepository
import com.mrsanglier.tsumegohero.repository.UserRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule: Module = module {
    singleOf(::TsumegoRepository)
    singleOf(::UserRepository)
    singleOf(::AttemptRepository)
}
