package com.keyboardhero.call.core.utils

import com.google.gson.Gson
import retrofit2.Response

inline fun <T> apiCall(request: () -> Response<T>): ResponseData<T> {
    return try {
        val result = request.invoke()
        val resultBody = result.body()
        if (result.isSuccessful && resultBody != null) {
            return ResponseData(code = result.code(), data = resultBody)
        } else {
//            if (result.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                return request.invoke()
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

