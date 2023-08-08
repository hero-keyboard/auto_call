package com.keyboardhero.call.shared.domain

import com.keyboardhero.call.core.utils.apiCall
import com.keyboardhero.call.di.IoDispatcher
import com.keyboardhero.call.shared.data.ApiService
import com.keyboardhero.call.shared.data.AppPreference
import com.keyboardhero.call.shared.domain.dto.Location
import com.keyboardhero.call.shared.domain.dto.LocationRequest
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class UpdateLocationDeviceUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val apiService: ApiService,
    private val appPreference: AppPreference
) : UseCase<UpdateLocationDeviceUseCase.Parameters, Boolean>(dispatcher) {

    data class Parameters(
        val longitude: Double,
        val latitude: Double,
    )

    override suspend fun execute(parameters: UpdateLocationDeviceUseCase.Parameters): Boolean {
        val result = apiCall {
            apiService.updateLocation(
                deviceId = appPreference.deviceID,
                request = LocationRequest(
                    location = Location(
                        latitude = parameters.latitude,
                        longitude = parameters.longitude
                    )
                )
            )
        }
        return result.data?.success == true
    }
}