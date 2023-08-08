package com.keyboardhero.call.features.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.keyboardhero.call.core.utils.isConnectedToNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkStateManager {
    private var isRunning = false

    private val _networkStateFlow = MutableStateFlow(true)

    val networkStateFlow: StateFlow<Boolean>
        get() = _networkStateFlow

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            _networkStateFlow.tryEmit(isNetworkAvailable(context))
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        return context.isConnectedToNetwork()
    }

    fun registerNetworkChangeReceiver(context: Context) {
        isRunning = true
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkChangeReceiver, filter)
    }

    fun unregisterNetworkChangeReceiver(context: Context) {
        if (isRunning){
            context.unregisterReceiver(networkChangeReceiver)
            isRunning = false
        }
    }
}