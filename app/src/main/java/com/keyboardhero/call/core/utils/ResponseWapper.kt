package com.keyboardhero.call.core.utils

import com.google.gson.Gson
import java.net.HttpURLConnection
import retrofit2.Response

inline fun <T> apiCall(request: () -> Response<T>): ResponseData<T> {
    return try {
        val result = request.invoke()
        val resultBody = result.body()
        if (result.isSuccessful && resultBody != null) {
            return ResponseData(code = result.code(), data = resultBody)
        } else {
//            if (result.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                return apiCall(request)
//            }
//
//            if (result.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
//                return ResponseData(
//                    code = HttpURLConnection.HTTP_GATEWAY_TIMEOUT,
//                    error = ErrorMessage(message = "Internal Server Error. We had a problem with our server. Try again later."),
//                )
//            }
//
//            if (result.code() == HttpURLConnection.HTTP_FORBIDDEN) {
//                return ResponseData(
//                    code = HttpURLConnection.HTTP_FORBIDDEN,
//                    error = ErrorMessage(message = "You have reached your request limit for today, the next reset will be tomorrow at midnight UTC."),
//                )
//            }

            try {
                val responseBody = result.errorBody()
                val error = Gson().fromJson(responseBody?.string(), ResponseError::class.java)

                ResponseData(
                    code = result.code(),
                    error = ErrorMessage(message = error.message),
                )
            } catch (e: Exception) {
                ResponseData(
                    code = result.code(),
                    error = ErrorMessage(exception = e),
                )
            }
        }
    } catch (exception: Exception) {
        ResponseData(
            error = ErrorMessage(exception = exception),
        )
    }
}

