package com.keyboardhero.call.di

import com.keyboardhero.call.BuildConfig
import com.keyboardhero.call.NetworkConfig.API_DOMAIN_DEFAULT
import com.keyboardhero.call.NetworkConfig.NETWORK_CONNECT_TIMEOUT
import com.keyboardhero.call.NetworkConfig.NETWORK_READ_TIMEOUT
import com.keyboardhero.call.NetworkConfig.NETWORK_WRITE_TIMEOUT
import com.keyboardhero.call.shared.data.AppPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalSerializationApi
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {


    @Provides
    @Singleton
    fun provideRetrofit(
        appPreference: AppPreference,
        requestInterceptor: RequestInterceptor
    ): Retrofit.Builder {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .hostnameVerifier { _, _ -> true }.apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                        .apply { level = HttpLoggingInterceptor.Level.BODY }
                    addInterceptor(loggingInterceptor)
                }
            }
            .addInterceptor(
                requestInterceptor(
                    appPreference = appPreference
                )
            ).build()

        return Retrofit.Builder()
            .baseUrl(API_DOMAIN_DEFAULT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
    }

    private fun requestInterceptor(appPreference: AppPreference): Interceptor =
        Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder().method(original.method, original.body)
            appPreference.accessToken.let {
                if (it.isNotBlank()) {
                    requestBuilder.addHeader(
                        name = "Authorization",
                        value = "Bearer $it"
                    )
                }
            }
            val request = requestBuilder.build()
            return@Interceptor chain
                .withConnectTimeout(NETWORK_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .withWriteTimeout(NETWORK_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .withReadTimeout(NETWORK_READ_TIMEOUT, TimeUnit.SECONDS)
                .proceed(request)
        }
}
