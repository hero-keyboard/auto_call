package com.keyboardhero.call.shared.domain

import com.keyboardhero.call.core.utils.apiCall
import com.keyboardhero.call.di.IoDispatcher
import com.keyboardhero.call.shared.data.ApiService
import com.keyboardhero.call.shared.data.AppPreference
import com.keyboardhero.call.shared.domain.dto.RefreshTokenRequest
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher


class RefreshTokenUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val apiService: ApiService,
    private val appPreference: AppPreference
) : UseCase<Unit, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Unit): Boolean {
        return if (appPreference.refreshToken.isNotEmpty() && appPreference.deviceID.isNotEmpty()) {
            val result = apiCall {
                apiService.refreshToken(
                    RefreshTokenRequest(
                        deviceId = appPreference.deviceID, refreshToken = appPreference.refreshToken
                    )
                )
            }
            val refreshToken = result.data?.refreshToken
            val accessToken = result.data?.accessToken
            if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                appPreference.accessToken = accessToken
                appPreference.refreshToken = refreshToken
                true
            } else {
                false
            }
        } else {
            false
        }

    }
}