package com.keyboardhero.call.shared.domain

import com.keyboardhero.call.core.utils.apiCall
import com.keyboardhero.call.di.IoDispatcher
import com.keyboardhero.call.shared.data.ApiService
import com.keyboardhero.call.shared.domain.dto.HistoryRequest
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher


class UpdateHistoryCallUseCase @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val apiService: ApiService
) : UseCase<UpdateHistoryCallUseCase.Parameter, Boolean>(dispatcher) {

    data class Parameter(
        val deviceId: String,
        val callNumber: String,
        val answerNumber: String,
        val duration: Int,
    )

    override suspend fun execute(parameters: Parameter): Boolean {
        val result = apiCall {
            apiService.updateCallHistory(
                deviceId = parameters.deviceId,
                request = HistoryRequest(
                    callNumber = parameters.callNumber,
                    answerNumber = parameters.answerNumber,
                    duration = parameters.duration
                )
            )
        }

        return result.data?.success == true
    }
}