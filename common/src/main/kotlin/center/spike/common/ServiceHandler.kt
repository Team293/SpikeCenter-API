package center.spike.common

import kotlinx.serialization.serializer
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ServiceResponse<T>(
    val status: ResponseStatus = ResponseStatus.SUCCESS,
    val message: String? = null,
    val data: T? = null,
    val error: ServiceError? = null
)

fun ResponseStatus.toJaxRs(): Response.Status = when (this) {
    ResponseStatus.SUCCESS -> Response.Status.OK
    ResponseStatus.ERROR -> Response.Status.INTERNAL_SERVER_ERROR
    ResponseStatus.VALIDATION_ERROR -> Response.Status.BAD_REQUEST
    ResponseStatus.NOT_FOUND -> Response.Status.NOT_FOUND
}

inline fun <reified T> ServiceResponse<T>.toResponse(
    statusOverride: Response.StatusType? = null
): Response {
    val status = statusOverride ?: this.status.toJaxRs()
    val json = Json.encodeToString(serializer(), this) // uses reified serializer for ServiceResponse<T>
    return Response.status(status.statusCode)
        .entity(json)
        .type(MediaType.APPLICATION_JSON)
        .build()

}

@Serializable
data class ServiceError(
    val code: ErrorCode,
    val detail: String?
)

@Serializable
sealed class ErrorCode {
    @Serializable data object Validation : ErrorCode()
    @Serializable data object NotFound : ErrorCode()
    @Serializable data object Unauthorized : ErrorCode()
    @Serializable data object Database : ErrorCode()
    @Serializable data class Custom(val value: String) : ErrorCode()
}

@Serializable
enum class ResponseStatus {
    SUCCESS,
    ERROR,
    VALIDATION_ERROR,
    NOT_FOUND
}

object ServiceResponses {
    fun <T> ok(data: T, message: String? = null) =
        ServiceResponse(ResponseStatus.SUCCESS, message, data)

    fun <T> error(
        message: String,
        code: ErrorCode,
        detail: String? = null
    ) = ServiceResponse<T>(
        status = ResponseStatus.ERROR,
        message = message,
        error = ServiceError(code, detail ?: message)
    )

    fun <T> validation(error: ServiceError, message: String? = "Validation failed") =
        ServiceResponse<T>(
            status = ResponseStatus.VALIDATION_ERROR,
            message = message,
            error = error
        )
}