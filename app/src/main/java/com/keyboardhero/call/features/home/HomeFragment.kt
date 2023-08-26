package com.keyboardhero.call.features.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.keyboardhero.call.core.base.BaseFragment
import com.keyboardhero.call.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.log

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private val callStateSubscribe = CallStateSubscribe.getCallEvents()
    private val viewModel: HomeViewModel by viewModels()
    private var isCallOf = false;

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

                    TelephonyManager.EXTRA_STATE_IDLE ->{
                        isCallOf = true
                    }

                    TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                        isCallOf = true
                    }
                    else -> {}
                }
            }
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
                if (it && viewModel.currentState.deviceInfo != null) {
                    viewModel.apply {
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
                binding.tvDeviceName.text = "Tên máy: $deviceName"
            },
        )

        viewModel.observe(
            owner = this,
            selector = { state -> state.deviceInfo?.station?.name },
            observer = { stationName ->
                binding.tvStation.text = "Tên trạm: $stationName"
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

        viewModel.observe(
            owner = this,
            selector = { state -> state.log },
            observer = { log ->
                binding.tvLog.text = log
            },
        )

        viewModel.observeEvent(lifecycleScope, this) { event ->
            when (event) {
                is HomeEvent.SendSMSEvent -> {
                    sendSms(phone = event.phone, message = event.message)
                }

                is HomeEvent.GetLocation -> {
                    requestCurrentLocation()
                }

                is HomeEvent.GetDeviceInfoError -> {
                    showSingleOptionDialog(
                        title = "Lỗi",
                        message = event.message,
                        button = "Ok"
                    )
                }

                is HomeEvent.EndCallEvent -> {
                    Log.i("AAA", "EndCallEvent: ")
                    endCallPhone()
                }

                is HomeEvent.CallEvent -> {
                    Log.i("AAA", "makeCallPhone: ${event.phone}")
                    isCallOf = false
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
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestCurrentLocation()
        if (viewModel.currentState.callStatus == CallStatus.CALLING && isCallOf) {
            endCallPhone()
            viewModel.stopDelay()
        }
    }

    override fun onDestroy() {
        endCallPhone()
        viewModel.stopDelay()
        super.onDestroy()
    }

    private fun initGPS() {
        resultCurrentLocation = { latitude, longitude ->
            viewModel.updateLocation(latitude, longitude)
        }
    }
}
