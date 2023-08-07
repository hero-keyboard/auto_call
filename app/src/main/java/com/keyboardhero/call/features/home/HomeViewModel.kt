package com.keyboardhero.call.features.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.keyboardhero.call.core.base.BaseViewModel
import com.keyboardhero.call.core.utils.timeTicker
import com.keyboardhero.call.shared.data.AppPreference
import com.keyboardhero.call.shared.domain.GetDeviceInfoUseCase
import com.keyboardhero.call.shared.domain.GetPhoneNumberUseCase
import com.keyboardhero.call.shared.domain.RestartStatusDeviceUseCase
import com.keyboardhero.call.shared.domain.UpdateHistoryCallUseCase
import com.keyboardhero.call.shared.domain.data
import com.keyboardhero.call.shared.domain.dto.NumberCallResponse
import com.keyboardhero.call.shared.domain.succeeded
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase,
    private val updateHistoryCallUseCase: UpdateHistoryCallUseCase,
    private val getPhoneNumberUseCase: GetPhoneNumberUseCase,
    private val restartStatusDeviceUseCase: RestartStatusDeviceUseCase,
) : BaseViewModel<HomeViewState, HomeEvent>() {

    override fun initState(): HomeViewState = HomeViewState()
    private var job: Job? = null
    private var jobTime: Job? = null
    private suspend fun getPhoneNumber(): NumberCallResponse? {
        return getPhoneNumberUseCase(Unit).data
    }

    fun stopDelay() {
        jobTime?.cancel()
    }

    fun start() {
        job?.cancel()
        job = viewModelScope.launch {
            dispatchState(currentState.copy(isRunning = true))
            resetStartWithSystem()
            while (currentState.isRunning && currentState.callStatus == CallStatus.NORMAL) {
                val phoneNumberData = getPhoneNumber()
                if (phoneNumberData?.phoneNumber != null && phoneNumberData.delay != null && phoneNumberData.duration != null) {
                    dispatchEvent(HomeEvent.CallEvent(phone = phoneNumberData.phoneNumber))
                    dispatchState(currentState.copy(callStatus = CallStatus.CALLING))
                    jobTime = timeTicker(
                        duration = 10 + phoneNumberData.duration + phoneNumberData.delay,
                        period = 1
                    ).onEach {
                        if (it == phoneNumberData.delay) {
                            dispatchState(currentState.copy(callStatus = CallStatus.DELAY))
                            dispatchEvent(HomeEvent.EndCallEvent)
                        }

                        if (it == phoneNumberData.delay - 5) {
                            dispatchEvent(HomeEvent.GetLocation)
                            dispatchEvent(HomeEvent.GetHistory(phoneNumberData.phoneNumber))
                        }
                    }.onCompletion {
                        dispatchState(currentState.copy(callStatus = CallStatus.NORMAL))
                    }.launchIn(this)
                }
            }
        }
    }

    fun getDeviceInfo() {
        viewModelScope.launch {
            dispatchState(currentState.copy(isLoading = true))
            val result = getDeviceInfoUseCase.invoke(Unit)
            val data = result.data?.data
            if (result.succeeded && data != null) {
                appPreference.deviceID = data.Id ?: ""
                if (data.status == "offline") {
                    restartDevice()
                    getDeviceInfo()
                } else {
                    dispatchState(currentState.copy(deviceInfo = data))
                }
            } else {
                dispatchEvent(HomeEvent.GetDeviceInfoError("Get device infor error."))
            }
            dispatchState(currentState.copy(isLoading = false))
        }
    }

    fun updateCallHistory(phoneNumber: String, duration: Long) {
        viewModelScope.launch {
           val result = updateHistoryCallUseCase(
                UpdateHistoryCallUseCase.Parameter(
                    deviceId = currentState.deviceInfo?.Id ?: "",
                    callNumber = currentState.deviceInfo?.phoneNumber ?: "",
                    answerNumber = phoneNumber,
                    duration = duration.toInt()
                )
            )

            if (result.data == false){
                dispatchEvent(HomeEvent.RestartDevice)
            }
        }
    }

    fun restartDevice() {
        viewModelScope.launch {
            val result = restartStatusDeviceUseCase.invoke(Unit)
            if (result.data == false) {
                dispatchEvent(HomeEvent.GetDeviceInfoError("Đã xảy ra lỗi. Vui lòng thử lại"))
            }
        }
    }

    fun getLocalData() {
        viewModelScope.launch {
            dispatchState(
                currentState.copy(startWithSystem = appPreference.keyStartApp)
            )
        }
    }

    private fun resetStartWithSystem() {
        viewModelScope.launch {
            dispatchState(currentState.copy(startWithSystem = false))
            appPreference.keyStartApp = false
        }
    }

    fun stop() {
        viewModelScope.launch {
            job?.cancel()
            jobTime?.cancel()
            dispatchState(currentState.copy(isRunning = false))
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            Log.i("AAA", "updateLocation: $latitude  $longitude")
        }
    }
}
