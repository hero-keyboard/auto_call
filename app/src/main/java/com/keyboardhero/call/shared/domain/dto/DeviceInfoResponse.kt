package com.keyboardhero.call.shared.domain.dto

import com.google.gson.annotations.SerializedName

data class DeviceInfoResponse (
    @SerializedName("_id"          ) val Id          : String?  = null,
    @SerializedName("name"         ) val name        : String?  = null,
    @SerializedName("phone_number" ) val phoneNumber : String?  = null,
    @SerializedName("phone_report" ) val phoneReport : String?  = null,
    @SerializedName("network_id"   ) val networkId   : String?  = null,
    @SerializedName("station_id"   ) val stationId   : String?  = null,
    @SerializedName("type"         ) val type        : String?  = null,
    @SerializedName("is_active"    ) val isActive    : Boolean? = null,
    @SerializedName("status"       ) val status      : String?  = null,
    @SerializedName("created_at"   ) val createdAt   : Long?     = null,
    @SerializedName("network"      ) val network     : Network? = Network(),
    @SerializedName("station"      ) val station     : Station? = Station()
)




