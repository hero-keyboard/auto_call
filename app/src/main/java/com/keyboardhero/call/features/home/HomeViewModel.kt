package com.keyboardhero.call.features.home

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import com.keyboardhero.call.core.base.BaseViewModel
import com.keyboardhero.call.core.utils.timeTicker
import com.keyboardhero.call.shared.data.AppPreference
import com.keyboardhero.call.shared.domain.*
import com.keyboardhero.call.shared.domain.dto.NumberCallResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase,
    private val updateHistoryCallUseCase: UpdateHistoryCallUseCase,
    private val getPhoneNumberUseCase: GetPhoneNumberUseCase,
    private val restartStatusDeviceUseCase: RestartStatusDeviceUseCase,
    private val updateLocationDeviceUseCase: UpdateLocationDeviceUseCase,
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


    private fun updateLog(log: String) {
        dispatchState(currentState.copy(log = createLog(log)))
    }

    fun start() {
        job?.cancel()
        job = viewModelScope.launch {
            dispatchState(currentState.copy(isRunning = true))
            updateLog("Bắt Đầu")
            resetStartWithSystem()
            while (currentState.isRunning) {
                if (currentState.callStatus == CallStatus.NORMAL) {
                    val phoneNumberData = getPhoneNumber()
                    if (phoneNumberData?.phoneNumber != null && phoneNumberData.delay != null && phoneNumberData.duration != null) {
                        updateLog("Gọi: ${phoneNumberData.phoneNumber} thời lượng ${phoneNumberData.duration}")
                        dispatchEvent(HomeEvent.CallEvent(phone = phoneNumberData.phoneNumber))
                        dispatchState(currentState.copy(callStatus = CallStatus.CALLING))
                        jobTime = timeTicker(
                            duration = 10 + phoneNumberData.duration + phoneNumberData.delay,
                            period = 1
                        ).onEach {
                            if (it == phoneNumberData.delay) {
                                dispatchState(currentState.copy(callStatus = CallStatus.DELAY))
                                dispatchEvent(HomeEvent.EndCallEvent)
                                updateLog("Gọi xong, đang delay")
                            }

                            if (it == phoneNumberData.delay - 5) {
                                dispatchEvent(HomeEvent.GetLocation)
                                dispatchEvent(HomeEvent.GetHistory(phoneNumberData.phoneNumber))
                            }
                        }.onCompletion {
                            if (it != null) {
                                updateLog("Cuộc gọi bị kết thúc sớm.")
                                dispatchState(currentState.copy(callStatus = CallStatus.NORMAL))
                                dispatchEvent(HomeEvent.GetHistory(phoneNumberData.phoneNumber))
                            } else {
                                updateLog("Xong")
                                dispatchState(currentState.copy(callStatus = CallStatus.NORMAL))
                            }
                        }.launchIn(this)
                    } else {
                        updateLog("Lấy số điện thoại thất bại.")
                    }
                }
                delay(3000)
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
                if (data.status != "running") {
                    if (restartDevice()) {
                        delay(2000)
                        getDeviceInfo()
                    }
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

            if (result.data == false) {
                dispatchEvent(HomeEvent.RestartDevice)
            }
        }
    }

    private suspend fun restartDevice(): Boolean {
        val result = restartStatusDeviceUseCase.invoke(Unit)
        return result.data == true
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
            updateLog("Dừng")
            dispatchState(currentState.copy(isRunning = false))
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            updateLocationDeviceUseCase.invoke(
                UpdateLocationDeviceUseCase.Parameters(
                    longitude = longitude,
                    latitude = latitude
                )
            )
        }
    }

    private fun createLog(log: String): String {
        return if (currentState.log.isNotEmpty()) {
            currentState.log + "\n" + getCurrentDateTimeUsingDate() + ": " + log
        } else getCurrentDateTimeUsingDate() + ": " + log
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateTimeUsingDate(): String {
        val currentDateTime = Date()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return sdf.format(currentDateTime)
    }
}
