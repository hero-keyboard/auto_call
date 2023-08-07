package com.keyboardhero.call.core.utils.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.FragmentActivity

class PermissionUtil constructor(private val activity: FragmentActivity) {

    private val requestFragment: HandleResultFragment

    init {
        val fragment = activity.supportFragmentManager.findFragmentByTag(HandleResultFragment.TAG)
        if (fragment == null) {
            requestFragment = HandleResultFragment()
            activity
                .supportFragmentManager
                .beginTransaction()
                .add(requestFragment, HandleResultFragment.TAG)
                .commitAllowingStateLoss()
        } else {
            requestFragment = fragment as HandleResultFragment
        }
    }

    fun request(
        vararg permissions: String,
        callback: (areGrantedAll: Boolean, deniedPermissions: List<Permission>) -> Unit,
    ) {
        val permissionSize = permissions.size
        val permissionsResult = arrayListOf<Permission>()

        val requestPermissions = arrayListOf<String>()
        permissions.forEach { permission ->
            if (isGranted(permission)) {
                permissionsResult.add(
                    Permission(
                        permission,
                        granted = true,
                        preventAskAgain = false,
                    ),
                )
            } else {
                var subject = requestFragment.getPermissionRequest(permission)
                if (subject == null) {
                    subject = fun(p: Permission) {
                        permissionsResult.add(p)
                        if (permissionsResult.size == permissionSize) {
                            val grantedAll = permissionsResult.all { it.granted }
                            if (grantedAll) {
                                callback.invoke(grantedAll, emptyList())
                            } else {
                                val permissionsNotGranted = permissionsResult.filter { !it.granted }
                                callback.invoke(grantedAll, permissionsNotGranted)
                            }
                        }
                    }
                }
                requestFragment.addPermissionRequest(permission, subject)
                requestPermissions.add(permission)
            }
        }

        if (requestPermissions.isNotEmpty() && !beforeAndroid6()) {
            requestFragment.request(requestPermissions.toTypedArray())
        } else {
            callback.invoke(true, emptyList())
        }
    }

    private fun isGranted(permission: String): Boolean {
        return activity.isPermissionsGranted(permission)
    }
}

fun Context.isPermissionsGranted(vararg permissions: String): Boolean {
    if (beforeAndroid6()) return true

    permissions.forEach { permission ->
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

private fun beforeAndroid6(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
