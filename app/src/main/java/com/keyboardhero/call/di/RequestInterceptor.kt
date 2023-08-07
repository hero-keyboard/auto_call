package com.keyboardhero.call.di

import android.content.Context
import com.keyboardhero.call.BaseApplication
import com.keyboardhero.call.core.utils.isConnectedToNetwork
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RequestInterceptor @Inject constructor(@ApplicationContext val context: Context) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val connectionListener = BaseApplication.getInternetConnectionListener()
        if (!context.isConnectedToNetwork()) {
            connectionListener?.onInternetUnavailable()
        }
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)

        val responseCode = response.code
        if (401 == responseCode) {
            connectionListener?.tokenExpired()
        } else if (403 == responseCode) {
            connectionListener?.accessDenied()
        }
        return response
    }
}