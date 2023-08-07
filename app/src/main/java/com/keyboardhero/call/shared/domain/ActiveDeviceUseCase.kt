package com.keyboardhero.call.shared.domain

import com.keyboardhero.call.core.utils.ResponseData
import com.keyboardhero.call.core.utils.apiCall
import com.keyboardhero.call.di.IoDispatcher
import com.keyboardhero.call.shared.data.ApiService
import com.keyboardhero.call.shared.domain.dto.LoginRequest
import com.keyboardhero.call.shared.domain.dto.LoginResponse
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class ActiveDeviceUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val apiService: ApiService
) : UseCase<ActiveDeviceUseCase.Parameter, ResponseData<LoginResponse>>(dispatcher) {

    data class Parameter(
        val code: String,
    )

    override suspend fun execute(parameters: Parameter): ResponseData<LoginResponse> {
        return apiCall {
            apiService.activeDevice(LoginRequest(otp = parameters.code))
        }
    }
}