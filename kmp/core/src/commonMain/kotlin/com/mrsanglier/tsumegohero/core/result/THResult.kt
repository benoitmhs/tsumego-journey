package com.mrsanglier.tsumegohero.core.result

import co.touchlab.kermit.Logger
import com.mrsanglier.tsumegohero.core.error.THError

/**
 * Generic result wrapper to wrap data with a status.
 *
 * @param T Type of the wrapped data. Might be [Unit] if the expected result does not contain any data.
 */
sealed class THResult<out T> {

    /**
     * Success implementation of [THResult]
     *
     * @param T @inheritDoc
     * @property successData The final non-null data
     */
    data class Success<out T>(val successData: T) : THResult<T>()

    /**
     * Success implementation of [THResult]
     *
     * @param T @inheritDoc
     * @property throwable The throwable that caused the failure or null if non applicable
     * @property failureData The final data or null if non applicable
     */
    data class Failure<out T>(val error: THError? = null, val failureData: T? = null) : THResult<T>()

    /**
     * Common getter for the data of any result states.
     */
    val data: T?
        get() {
            return when (this) {
                is Success -> successData
                is Failure -> failureData
            }
        }

    /**
     * Cast [THResult] to [THFlowResult]
     */
    fun asFlowResult(): THFlowResult<T> {
        return when (this) {
            is Success -> THFlowResult.Success(successData)
            is Failure -> THFlowResult.Failure(error, failureData)
        }
    }

    companion object Companion {
        inline fun <reified T> catchResult(
            mapError: (THError) -> THError = { it },
            block: () -> T,
        ): THResult<T> =
            try {
                Success(block())
            } catch (e: THError) {
                val piError = mapError(e)
                Logger.e { e.message.toString() }
                Logger.e { piError.stackTraceToString() }
                Failure(piError)
            }
    }
}
