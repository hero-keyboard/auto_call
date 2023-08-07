package com.keyboardhero.call.core.base

interface ConnectionListener {
    fun onInternetUnavailable()
    fun tokenExpired()
    fun accessDenied()
}