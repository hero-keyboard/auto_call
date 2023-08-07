package com.keyboardhero.call.shared.domain

import com.keyboardhero.call.core.utils.apiCall
import com.keyboardhero.call.di.IoDispatcher
import com.keyboardhero.call.shared.data.ApiService
import com.keyboardhero.call.shared.data.AppPreference
import com.keyboardhero.call.shared.domain.dto.NumberCallResponse
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class GetPhoneNumberUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val apiService: ApiService,
    private val appPreference: AppPreference
) : UseCase<Unit, NumberCallResponse?>(dispatcher) {

    override suspend fun execute(parameters: Unit): NumberCallResponse? {
        val result = apiCall { apiService.getPhoneNumber(deviceId = appPreference.deviceID) }
        return result.data
    }
}