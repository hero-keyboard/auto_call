package com.keyboardhero.answer.shared.data

import com.keyboardhero.call.shared.data.ApiService
import com.keyboardhero.call.shared.data.AppPreference
import com.keyboardhero.call.shared.data.AppPreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun provideAuthPreference(
        preference: AppPreferenceImpl,
    ): AppPreference

    companion object {
        @Provides
        @Singleton
        fun provideAuthApiService(retrofitBuilder: Retrofit.Builder): ApiService {
            return retrofitBuilder
                .build()
                .create(ApiService::class.java)
        }
    }
}
