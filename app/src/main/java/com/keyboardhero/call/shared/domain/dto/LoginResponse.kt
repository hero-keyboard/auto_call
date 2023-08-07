package com.keyboardhero.call.shared.domain.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("access_token") val accessToken: String? = null
)

data class LoginRequest(
    @SerializedName("otp") val otp: String,
    @SerializedName("type") val type: String = "call",
)

data class RefreshTokenRequest(
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("refresh_token") val refreshToken: String
)