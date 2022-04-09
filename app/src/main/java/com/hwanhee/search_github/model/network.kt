package com.hwanhee.search_github.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException


class ErrorResponse (
    @SerializedName("title")
    var title: String = "",
    @SerializedName("message")
    var message: String = "예기치 못한 에러가 발생했습니다.",
) {
    companion object {
        fun fromString(body: String): ErrorResponse {
            return try {
                val gson = Gson()
                gson.fromJson(body, ErrorResponse::class.java)
            } catch (ex: IOException) {
                empty()
            }
        }

        fun empty() : ErrorResponse {
            return ErrorResponse(
                "",
                "예기치 못한 에러가 발생했습니다."
            )
        }

        fun networkError(title: String = "", body: String = "네트워크 연결상태를 확인해주세요..") : ErrorResponse {
            return ErrorResponse(
                title,
                body
            )
        }
    }
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class Error(val code: Int? = null, val error: ErrorResponse): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
}

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ResultWrapper.Error(code, errorResponse)
                }
                else -> {
                    ResultWrapper.Error(null, ErrorResponse.empty())
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse {
    return try {
        throwable.response()?.errorBody()?.string()?.let {
            return ErrorResponse.fromString(it)
        }

        return ErrorResponse.empty()
    } catch (exception: Exception) {
        ErrorResponse.empty()
    }
}
