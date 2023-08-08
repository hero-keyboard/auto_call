package com.keyboardhero.call.features.home

import com.keyboardhero.call.shared.domain.dto.DeviceInfoResponse

data class HomeViewState(
    val isRunning: Boolean = false,
    val isLoading: Boolean = false,
    val startWithSystem: Boolean = false,
    val deviceInfo: DeviceInfoResponse? = null,
    val callStatus: CallStatus = CallStatus.NORMAL,
    val log: String = ""
)

enum class CallStatus {
    NORMAL,
    CALLING,
    DELAY,
}