package com.keyboardhero.call.core.utils

import com.google.gson.annotations.SerializedName

data class ResponseData<T>(
    val data: T? = null,
    val code: Int? = null,
    val error: ErrorMessage? = null,
)

data class ErrorMessage(
    val message: String? = null,
    val exception: Exception? = null,
)

data class ResponseError(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("stack") val stack: String,
)
