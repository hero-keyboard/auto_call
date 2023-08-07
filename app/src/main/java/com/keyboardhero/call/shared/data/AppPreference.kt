package com.keyboardhero.call.shared.data

import android.content.SharedPreferences
import com.keyboardhero.call.core.utils.sharedpreference.BooleanPreferenceDelegate
import com.keyboardhero.call.core.utils.sharedpreference.LongPreferenceDelegate
import com.keyboardhero.call.core.utils.sharedpreference.StringPreferenceDelegate
import com.keyboardhero.call.di.SecurePreference
import com.keyboardhero.call.features.Constant
import javax.inject.Inject

interface AppPreference {
    var keyStartApp: Boolean
    var deviceID: String
    var refreshToken: String
    var accessToken: String
}

class AppPreferenceImpl @Inject constructor(
    @SecurePreference val encryptedPrefs: SharedPreferences,
) : AppPreference {
    companion object {
        private const val KEY_START = "KEY_START"
        private const val KEY_DEVICE_ID = "KEY_DEVICE_ID"
        private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
        private const val KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN"
    }
    override var keyStartApp: Boolean by BooleanPreferenceDelegate(encryptedPrefs, KEY_START)
    override var deviceID: String by StringPreferenceDelegate(encryptedPrefs, KEY_DEVICE_ID)
    override var refreshToken: String by StringPreferenceDelegate(encryptedPrefs, KEY_REFRESH_TOKEN)
    override var accessToken: String by StringPreferenceDelegate(encryptedPrefs, KEY_ACCESS_TOKEN)
}
