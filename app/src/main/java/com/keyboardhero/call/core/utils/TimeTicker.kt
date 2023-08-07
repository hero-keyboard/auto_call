package com.keyboardhero.call.core.utils

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow

fun timeTicker(duration: Int, period: Int = 1) = flow {
    for (time in duration downTo 0 step period) {
        emit(time)
        delay(period * 1000L)
    }
}.cancellable()

fun Context.isConnectedToNetwork(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
}