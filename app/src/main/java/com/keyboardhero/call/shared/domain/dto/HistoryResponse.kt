package com.keyboardhero.call.shared.domain.dto

import com.google.gson.annotations.SerializedName

data class HistoryRequest(
    @SerializedName("type") val type: String = "call",
    @SerializedName("call_number") val callNumber: String,
    @SerializedName("answer_number") val answerNumber: String,
    @SerializedName("duration") val duration: Int,
)

data class HistoryResponse(
    @SerializedName("success") val success: Boolean,
)

data class LocationRequest(
    @SerializedName("location") val location: Location,
)

data class Location(
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("latitude") val latitude: Double,
)