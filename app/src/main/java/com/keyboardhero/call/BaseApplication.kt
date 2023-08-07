package com.keyboardhero.call

import android.app.Application
import com.keyboardhero.call.core.base.ConnectionListener
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onTerminate() {
        super.onTerminate()
        removeInternetConnectionListener()
    }

    companion object {
        private val sRequestConnectionListener: MutableList<ConnectionListener> = ArrayList()


        fun setInternetConnectionListener(mInternetConnectionListener: ConnectionListener?) {
            mInternetConnectionListener?.let { sRequestConnectionListener.add(0, it) }
        }

        private fun removeInternetConnectionListener() {
            sRequestConnectionListener.clear()
        }

        fun getInternetConnectionListener(): ConnectionListener? {
            return sRequestConnectionListener[0]
        }
    }
}