package com.keyboardhero.call.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.keyboardhero.call.SharedPreferenceConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PreferenceStorageModule {
    @Provides
    @Singleton
    @SecurePreference
    fun providerSecurePreference(@ApplicationContext context: Context): SharedPreferences {
        val masterKey: MasterKey =
            MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        return EncryptedSharedPreferences.create(
            context,
            SharedPreferenceConfig.SECURE_SHARED_PREFERENCES_FILE_NAME,
            masterKey, // masterKey created above
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    @NormalPreference
    fun providerPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            SharedPreferenceConfig.SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }
}