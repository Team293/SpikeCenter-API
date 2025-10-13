package center.spike.common

object SafeRunner {
    inline fun <T> runOutcome(
        crossinline errorCode: (Throwable) -> ErrorCode,
        crossinline block: () -> T
    ): Outcome<T> {
        return try {
            Outcome.Success(block())
        } catch (t: Throwable) {
            Outcome.Failure(ServiceError(errorCode(t), t.message) )
        }
    }
}


sealed class Outcome<out T> {
    data class Success<T>(val value: T) : Outcome<T>()
    data class Failure(val error: ServiceError) : Outcome<Nothing>()
}

inline fun <T, R> Outcome<T>.toServiceResponse(
    crossinline onSuccess: (T) -> ServiceResponse<R>,
    crossinline onFailure: (ServiceError) -> ServiceResponse<R>
): ServiceResponse<R> = when (this) {
    is Outcome.Success -> onSuccess(value)
    is Outcome.Failure -> onFailure(error)
}
