package com.keyboardhero.call.shared.domain

import com.keyboardhero.call.core.utils.ResponseData
import com.keyboardhero.call.core.utils.apiCall
import com.keyboardhero.call.di.IoDispatcher
import com.keyboardhero.call.shared.data.ApiService
import com.keyboardhero.call.shared.domain.dto.DeviceInfoResponse
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class GetDeviceInfoUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val apiService: ApiService
) : UseCase<Unit, ResponseData<DeviceInfoResponse>>(dispatcher) {
    override suspend fun execute(parameters: Unit): ResponseData<DeviceInfoResponse> {
        return apiCall {
            apiService.getDeviceInfo()
        }
    }
}