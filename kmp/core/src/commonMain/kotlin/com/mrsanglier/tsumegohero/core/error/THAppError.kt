package com.mrsanglier.tsumegohero.core.error

class THAppError(
    val title: String? = null,
    override val message: String? = null,
    override val cause: Throwable? = null,
    override val code: THAppError.Code,
) : THError("${title.orEmpty()} ${message.orEmpty()}", cause, code) {
    enum class Code : THErrorCode {
        ObjectNotFound,
        InvalidEmailFormat,
        SilentError,
    }
}

fun THAppError.Code.toError(
    title: String? = null,
    message: String? = null,
    cause: Throwable? = null,
): THAppError = THAppError(
    title = title,
    message = message,
    cause = cause,
    code = this,
)
