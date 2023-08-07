package com.keyboardhero.call.features.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.telecom.TelecomManager
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.keyboardhero.call.core.base.BaseFragment
import com.keyboardhero.call.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private val callStateSubscribe = CallStateSubscribe.getCallEvents()
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keepScreenOn(requireContext())
        initGPS()
        lifecycleScope.launch {
            callStateSubscribe.collect {
                when (it) {
                    TelephonyManager.EXTRA_STATE_RINGING -> {
                        endCallPhone()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun enableView(enable: Boolean) {
        with(binding) {
            txtDeviceName.isEnabled = enable
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun initActions() {
        with(binding) {
            btnStart.setOnClickListener {
                if (!viewModel.currentState.isRunning) {
                    viewModel.start()
                } else {
                    viewModel.stop()
                }
            }
        }
    }

    override fun initData(data: Bundle?) {
        viewModel.apply {
            getDeviceInfo()
            getLocalData()
        }
    }

    override fun initViews() {

    }

    @SuppressLint("SetTextI18n")
    override fun initObservers() {
        lifecycleScope.launch {
            NetworkStateManager.networkStateFlow.collect {
                if (it) {
                    viewModel.apply {
                        restartDevice()
                        getDeviceInfo()
                    }
                }
            }
        }
        viewModel.observe(
            owner = this,
            selector = { state -> state.isLoading },
            observer = { isLoading ->
                if (isLoading) showLoading() else hideLoading()
            },
        )

        viewModel.observe(
            owner = this,
            selector = { state -> state.isRunning },
            observer = { isRunning ->
                binding.btnStart.text = if (!isRunning) "Chạy" else "Dừng"
                enableView(!isRunning)
            },
        )

        viewModel.observe(
            owner = this,
            selector = { state -> state.startWithSystem },
            observer = { startWithSystem ->
                if (startWithSystem) {
                    viewModel.start()
                }
            },
        )

        viewModel.observe(
            owner = this,
            selector = { state -> state.deviceInfo?.name },
            observer = { deviceName ->
                binding.txtDeviceName.setText(deviceName)
            },
        )

        viewModel.observe(
            owner = this,
            selector = { state -> state.deviceInfo?.status },
            observer = { status ->
                if (status != "offline") {
                    NetworkStateManager.unregisterNetworkChangeReceiver(requireContext())
                }
            },
        )

        viewModel.observeEvent(lifecycleScope, this) { event ->
            when (event) {
                is HomeEvent.SendSMSEvent -> {
                    sendSms(phoneNumber = event.phone, message = event.message)
                }

                is HomeEvent.GetLocation -> {
                    requestCurrentLocation()
                }

                is HomeEvent.EndCallEvent -> {
                    Log.i("AAA", "EndCallEvent: ")
                    endCallPhone()
                }

                is HomeEvent.CallEvent -> {
                    Log.i("AAA", "makeCallPhone: ${event.phone}")
                    makeCallPhone(event.phone)
                }

                is HomeEvent.GetHistory -> {
                    readCallLogByPhoneNumber(phoneNumber = event.phone) {
                        Log.i("AAA", "GetHistory: ${it.first} - ${it.second}")
                        viewModel.updateCallHistory(phoneNumber = it.first, duration = it.second)
                    }
                }

                is HomeEvent.RestartDevice -> {
                    NetworkStateManager.registerNetworkChangeReceiver(requireContext())
                }

                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestCurrentLocation()
        if (viewModel.currentState.callStatus == CallStatus.CALLING) {
            endCallPhone()
            viewModel.stopDelay()
        }
    }

    override fun onDestroy() {
        endCallPhone()
        viewModel.stopDelay()
        super.onDestroy()
    }

    private fun makeCallPhone(phoneNumber: String) {
        requestPermissions(Manifest.permission.CALL_PHONE) { isGranted, _ ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                startActivity(intent)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun endCallPhone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            requestPermissions(Manifest.permission.ANSWER_PHONE_CALLS) { isGranted, _ ->
                if (isGranted) {
                    val telecomManager =
                        requireActivity().getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                    telecomManager?.endCall()
                }
            }
        } else {
            requestPermissions(Manifest.permission.CALL_PHONE) { isGranted, _ ->
                if (isGranted) {
                    val telephonyManager =
                        requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                    telephonyManager?.let {
                        try {
                            val telephonyClass = Class.forName(it.javaClass.name)
                            val methodEndCall = telephonyClass.getDeclaredMethod("endCall")
                            methodEndCall.isAccessible = true
                            methodEndCall.invoke(telephonyManager)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        requestPermissions(Manifest.permission.SEND_SMS) { isGranted, _ ->
            if (isGranted) {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }
        }
    }

    @SuppressLint("WakelockTimeout")
    private fun keepScreenOn(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "Answer:ScreenLock",
        )
        wakeLock.acquire()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun initGPS() {
        resultCurrentLocation = { latitude, longitude ->
            viewModel.updateLocation(latitude, longitude)
        }
    }
}
