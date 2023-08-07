package com.keyboardhero.call.features

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import com.keyboardhero.call.core.base.BaseActivity
import com.keyboardhero.call.databinding.ActivityMainBinding
import com.keyboardhero.call.shared.data.AppPreference
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    @Inject
    lateinit var appReference: AppPreference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val autoRun = intent.getBooleanExtra("NAM", false)
        appReference.keyStartApp = autoRun

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestPermissions(
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.ANSWER_PHONE_CALLS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) { _, _ -> }
        } else {

            requestPermissions(
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) { _, _ -> }
        }
    }
}
