package com.keyboardhero.call.shared.domain

import com.keyboardhero.call.core.utils.apiCall
import com.keyboardhero.call.di.IoDispatcher
import com.keyboardhero.call.shared.data.ApiService
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class RestartStatusDeviceUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val apiService: ApiService
) : UseCase<Unit, Boolean>(dispatcher) {
    override suspend fun execute(parameters: Unit): Boolean {
        val result = apiCall { apiService.restartDevice() }
        return result.data?.success == true
    }
}