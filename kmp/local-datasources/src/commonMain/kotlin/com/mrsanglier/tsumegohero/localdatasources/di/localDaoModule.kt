package com.mrsanglier.tsumegohero.localdatasources.di

import com.mrsanglier.tsumegohero.localdatasources.room.AppDatabase
import com.mrsanglier.tsumegohero.localdatasources.room.dao.AttemptDao
import com.mrsanglier.tsumegohero.localdatasources.room.dao.TsumegoDao
import com.mrsanglier.tsumegohero.localdatasources.room.dao.UserDao
import org.koin.core.module.Module
import org.koin.dsl.module

val localDaoModule: Module = module {
    single<TsumegoDao> { get<AppDatabase>().tsumegoDao() }
    single<UserDao> { get<AppDatabase>().userDao() }
    single<AttemptDao> { get<AppDatabase>().attemptDao() }
}
