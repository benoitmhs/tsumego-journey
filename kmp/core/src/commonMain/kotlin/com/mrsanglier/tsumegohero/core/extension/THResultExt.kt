package com.mrsanglier.tsumegohero.core.extension

import com.mrsanglier.tsumegohero.core.error.THError
import com.mrsanglier.tsumegohero.core.result.THResult

inline fun <T> THResult<T>.handleResult(
    onSuccess: (data: T) -> Unit,
    onError: (error: THError?) -> Unit,
) {
    when (this) {
        is THResult.Failure<T> -> onError(error)
        is THResult.Success<T> -> onSuccess(successData)
    }
}

suspend inline fun <T> THResult<T>.suspendHandleResult(
    onSuccess: suspend (data: T) -> Unit,
    onError: (error: THError?) -> Unit,
) {
    when (this) {
        is THResult.Failure<T> -> onError(error)
        is THResult.Success<T> -> onSuccess(successData)
    }
}
