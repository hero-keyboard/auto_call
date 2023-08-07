package com.keyboardhero.call.core.utils.permission

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class HandleResultFragment : Fragment() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 65535 // Max unsigned integer
        const val TAG = "HandleResultFragment"
    }

    private val requestPermissions: HashMap<String, (Permission) -> Unit> = HashMap()

    private var onFragmentCreated: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        onFragmentCreated?.invoke()
        onFragmentCreated = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun request(permissions: Array<out String>) {
        if (isAdded) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        } else {
            onFragmentCreated = {
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                val permissionRequested = requestPermissions[permission]
                val permissionResult =
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        Permission(permission = permission, granted = true, preventAskAgain = false)
                    } else {
                        Permission(
                            permission = permission,
                            granted = false,
                            preventAskAgain = !shouldShowRequestPermissionRationale(permission),
                        )
                    }
                permissionRequested?.invoke(permissionResult)
                requestPermissions.remove(permission)
            }
        }
    }

    fun addPermissionRequest(permission: String, request: (Permission) -> Unit) {
        requestPermissions[permission] = request
    }

    fun getPermissionRequest(permission: String): ((Permission) -> Unit)? =
        requestPermissions[permission]
}
