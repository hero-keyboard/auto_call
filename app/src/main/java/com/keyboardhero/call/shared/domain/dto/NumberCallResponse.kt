package com.keyboardhero.call.shared.domain.dto

import com.google.gson.annotations.SerializedName

data class NumberCallResponse(
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("duration") val duration: Int? = null,
    @SerializedName("delay") val delay: Int? = null
)