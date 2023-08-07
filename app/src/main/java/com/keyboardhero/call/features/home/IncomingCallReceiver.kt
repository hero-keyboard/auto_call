package com.keyboardhero.call.features.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class IncomingCallReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context, p1: Intent) {
        val extras = p1.extras
        val state = extras?.getString("state") ?: ""
        CallStateSubscribe.postCallEvent(state)
    }
}
