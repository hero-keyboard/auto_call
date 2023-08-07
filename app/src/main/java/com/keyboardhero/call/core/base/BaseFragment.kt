package com.keyboardhero.call.core.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.CallLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.addCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.keyboardhero.call.core.utils.views.SafetyClickListener
import com.keyboardhero.call.databinding.FragmentBaseBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment(), IBaseFragment {
    override val baseActivity: BaseActivity<*>?
        get() = activity as? BaseActivity<*>

    val safetyClickListener = SafetyClickListener()

    private lateinit var baseBinding: FragmentBaseBinding

    protected lateinit var locationManager: LocationManager

    lateinit var binding: VB
        private set
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        baseBinding = FragmentBaseBinding.inflate(inflater, container, false)
        binding = bindingInflater.invoke(inflater, baseBinding.contentContainer, true)
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        initViews()
        initActions()

        return baseBinding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(arguments)
        initObservers()

        (requireActivity() as OnBackPressedDispatcherOwner)
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                onBackPressed()
            }
    }

    override fun showLoading() {
        baseBinding.loadingView.root.isVisible = true
    }

    override fun hideLoading() {
        baseBinding.loadingView.root.isVisible = false
    }

    override fun onBackPressed() {
        if (!findNavController().popBackStack()) {
            requireActivity().finish()
        }
    }

    protected fun requestCurrentLocation() {
        if (checkLocationPermission()) {
            showEnableLocationSetting()
        } else {
            requestLocationPermission {
                showEnableLocationSetting()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission(
        onGrantedPermission: (() -> Unit)? = null,
    ) {
        requestPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) { allGranted, _ ->
            if (allGranted) {
                onGrantedPermission?.invoke()
            }
        }
    }

    private fun showEnableLocationSetting(onSuccessListener: (() -> Unit)? = null) {
        activity ?: return

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).setAlwaysShow(true)
        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        client.checkLocationSettings(builder.build()).apply {
            addOnSuccessListener {
                onSuccessListener?.invoke() ?: getLastLocation()
            }
            addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(exception.resolution).build()
                        resolutionForResult.launch(intentSenderRequest)
                    } catch (_: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val resolutionForResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            requireContext(),
        )
        fusedLocationProviderClient
            .lastLocation
            .addOnSuccessListener(requireActivity()) { location ->
                if (location != null) {
                    resultCurrentLocation?.invoke(location.latitude, location.longitude)
                } else {
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            for (result in locationResult.locations) {
                                if (result != null) {
                                    resultCurrentLocation?.invoke(
                                        location?.latitude ?: 0.0,
                                        location?.longitude ?: 0.0,
                                    )
                                    fusedLocationProviderClient.removeLocationUpdates(this)
                                }
                            }
                        }
                    }

                    Looper.myLooper()?.let {
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            it,
                        )
                    }
                }
            }
    }

    protected var resultCurrentLocation: ((latitude: Double, longitude: Double) -> Unit)? = null


    @SuppressLint("Range")
    protected fun readCallLogByPhoneNumber(
        phoneNumber: String,
        onSuccessListener: ((Pair<String, Long>) -> Unit)?
    ) {
        requestPermissions(Manifest.permission.READ_CALL_LOG) { isGranted, _ ->
            if (isGranted) {
                val selection = "${CallLog.Calls.NUMBER} = ?"
                val selectionArgs = arrayOf(phoneNumber)

                val cursor = requireContext().contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    null
                )

                cursor?.let {
                    if (it.moveToLast()) {
                        val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                        val duration = it.getLong(it.getColumnIndex(CallLog.Calls.DURATION))
                        onSuccessListener?.invoke(Pair(number, duration))
                    }
                    it.close()
                }
            }
        }
    }
}
