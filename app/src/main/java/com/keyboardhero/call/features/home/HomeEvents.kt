package com.keyboardhero.call.features.home

sealed interface HomeEvent {
    data class SendSMSEvent(val message: String, val phone: String) : HomeEvent
    data class CallEvent(val phone: String) : HomeEvent
    object EndCallEvent : HomeEvent
    object GetLocation : HomeEvent
    object RestartDevice : HomeEvent
    data class GetHistory(val phone: String) : HomeEvent
    data class GetDeviceInfoError(val message: String) : HomeEvent
}
