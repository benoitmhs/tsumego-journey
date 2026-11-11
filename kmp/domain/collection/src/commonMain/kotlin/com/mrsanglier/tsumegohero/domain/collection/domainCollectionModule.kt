package com.mrsanglier.tsumegohero.domain.collection

import com.mrsanglier.tsumegohero.domain.collection.usecase.CreateCollectionUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.GetEditionCropBoardUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.ImportUserTsumegoUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.ObserveCollectionDetailUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.ObserveCollectionsUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.PlayEditionMoveUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.SaveUserTsumegoUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.DeleteUserTsumegoUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.GetEditableUserTsumegoUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.ObserveUserTsumegoUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.StartUserTsumegoGameUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.DeleteCollectionUseCase
import com.mrsanglier.tsumegohero.domain.collection.usecase.ImportCollectionUseCase
import com.mrsanglier.tsumegohero.domain.collection.utils.SgfZipReader
import com.mrsanglier.tsumegohero.domain.collection.usecase.RenameCollectionUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainCollectionModule: Module = module {
    singleOf(::CreateCollectionUseCase)
    singleOf(::RenameCollectionUseCase)
    singleOf(::ImportCollectionUseCase)
    single { SgfZipReader() }
    singleOf(::DeleteCollectionUseCase)
    singleOf(::GetEditionCropBoardUseCase)
    singleOf(::ImportUserTsumegoUseCase)
    singleOf(::ObserveCollectionDetailUseCase)
    singleOf(::ObserveCollectionsUseCase)
    singleOf(::PlayEditionMoveUseCase)
    singleOf(::SaveUserTsumegoUseCase)
    singleOf(::StartUserTsumegoGameUseCase)
    singleOf(::ObserveUserTsumegoUseCase)
    singleOf(::GetEditableUserTsumegoUseCase)
    singleOf(::DeleteUserTsumegoUseCase)
}
