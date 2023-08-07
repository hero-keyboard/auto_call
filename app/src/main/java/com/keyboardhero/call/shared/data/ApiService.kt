package com.keyboardhero.call.shared.data

import com.keyboardhero.call.shared.domain.dto.DeviceInfoResponse
import com.keyboardhero.call.shared.domain.dto.HistoryRequest
import com.keyboardhero.call.shared.domain.dto.HistoryResponse
import com.keyboardhero.call.shared.domain.dto.LoginRequest
import com.keyboardhero.call.shared.domain.dto.LoginResponse
import com.keyboardhero.call.shared.domain.dto.NumberCallResponse
import com.keyboardhero.call.shared.domain.dto.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val PATH_ACTIVES = "auth-device/active"
private const val PATH_GET_DEVICE_INFO = "auth-device/get-info"
private const val PATH_REFRESH_TOKEN = "auth-device/refresh-token"
private const val PATH_UPDATE_CALL_HISTORY = "phone-histories/{deviceId}"
private const val PATH_UPDATE_LOCATION = "phone-devices/{deviceId}"
private const val PATH_GET_PHONE = "phone-devices/{deviceId}/number-to-call"
private const val PATH_RESTART = "auth-device/restart"

interface ApiService {
    @POST(PATH_ACTIVES)
    suspend fun activeDevice(@Body request: LoginRequest): Response<LoginResponse>

    @POST(PATH_REFRESH_TOKEN)
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponse>

    @GET(PATH_GET_DEVICE_INFO)
    suspend fun getDeviceInfo(): Response<DeviceInfoResponse>

    @POST(PATH_UPDATE_CALL_HISTORY)
    suspend fun updateCallHistory(
        @Path("deviceId") deviceId: String,
        @Body request: HistoryRequest
    ): Response<HistoryResponse>


    @GET(PATH_GET_PHONE)
    suspend fun getPhoneNumber(@Path("deviceId") deviceId: String): Response<NumberCallResponse>

    @POST(PATH_RESTART)
    suspend fun restartDevice(): Response<HistoryResponse>
}