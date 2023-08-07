package com.keyboardhero.call.shared.domain.dto

import com.google.gson.annotations.SerializedName

data class Station (
    @SerializedName("_id"  ) val Id   : String? = null,
    @SerializedName("name" ) val name : String? = null,
    @SerializedName("code" ) val code : String? = null,
)