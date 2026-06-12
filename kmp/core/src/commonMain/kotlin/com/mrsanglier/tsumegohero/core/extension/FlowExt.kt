package com.mrsanglier.tsumegohero.core.extension

import com.mrsanglier.tsumegohero.core.error.THError
import com.mrsanglier.tsumegohero.core.result.THResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

fun <T> Flow<T>.handleTHError(): Flow<THResult<T>> = transform { value ->
    try {
        emit(THResult.Success(value))
    } catch (e: THError) {
        emit(THResult.Failure(e))
    }
}
