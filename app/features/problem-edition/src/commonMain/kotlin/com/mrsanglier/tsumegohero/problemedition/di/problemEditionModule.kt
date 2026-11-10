package com.mrsanglier.tsumegohero.problemedition.di

import com.mrsanglier.tsumegohero.problemedition.create.CreateProblemViewModel
import com.mrsanglier.tsumegohero.problemedition.edit.EditProblemViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val problemEditionModule = module {
    viewModelOf(::CreateProblemViewModel)
    viewModelOf(::EditProblemViewModel)
}
