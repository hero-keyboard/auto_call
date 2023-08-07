package com.keyboardhero.call.core.base

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.keyboardhero.call.core.utils.logging.DebugLog
import com.keyboardhero.call.core.utils.permission.Permission

interface IBaseFragment : DialogCommonView {
    /**
     * Base activity @see [BaseActivity] which this fragment is standing on.
     */
    val baseActivity: BaseActivity<*>?

    /**
     * Receive data from which screen open this screen.
     *
     * @param data [Bundle]: Composite received data.
     */
    fun initData(data: Bundle?)

    /**
     * Init views in screen (e.g., a adapter, layout manager for RecycleView).
     */
    fun initViews()

    /**
     * Declare listener on views (e.g., listen click on button, view)
     */
    fun initActions()

    /**
     * Observe states from ViewModel to update views.
     * Make sure that this method is called after [initViews] because the views are need to ready to
     * display data.
     */
    fun initObservers()

    /**
     * Called when user click system back button
     */
    fun onBackPressed()

    /**
     * Called to show loading view.
     */
    fun showLoading()

    /**
     * Called to hide loading view.
     */
    fun hideLoading()

    /**
     * Request grants permissions.
     * Please aware that to use this method, the activity hold this fragment have to extend from @see [BaseActivity].
     *
     * @param permissions (variable number of arguments): List permission want to request
     * (e.g.,[Manifest.permission.ACCESS_COARSE_LOCATION], [Manifest.permission.READ_EXTERNAL_STORAGE],...)
     *
     * @param callback : Return result of request. There are two parameters:
     * areGrantedAll: True if all permissions are granted. False if at least one of those is declined.
     * deniedPermissions: List [Permission] request which are declined by user.
     */
    fun requestPermissions(
        vararg permissions: String,
        callback: (areGrantedAll: Boolean, deniedPermissions: List<Permission>) -> Unit,
    ) {
        baseActivity?.requestPermissions(*permissions, callback = callback) ?: run {
            callback.invoke(false, emptyList())
            DebugLog.w("To use this #requestPermissions() method, the activity hold this fragment have to extend from BaseActivity (read method docs)")
        }
    }

    override fun showSingleOptionDialog(
        title: String?,
        message: String,
        button: String,
        listener: DialogButtonClickListener?,
    ): AlertDialog? {
        return baseActivity?.showSingleOptionDialog(title, message, button, listener) ?: run {
            DebugLog.w("To use this #showSingleOptionDialog() method, the activity hold this fragment have to extend from BaseActivity.")
            null
        }
    }

    override fun showSingleOptionDialog(
        title: Int?,
        message: Int,
        button: Int,
        listener: DialogButtonClickListener?,
    ): AlertDialog? {
        return baseActivity?.showSingleOptionDialog(title, message, button, listener) ?: run {
            DebugLog.w("To use this #showSingleOptionDialog() method, the activity hold this fragment have to extend from BaseActivity.")
            null
        }
    }

    override fun showDoubleOptionsDialog(
        title: String?,
        message: String,
        firstButton: String,
        secondButton: String,
        firstButtonListener: DialogButtonClickListener?,
        secondButtonListener: DialogButtonClickListener?,
    ): AlertDialog? {
        return baseActivity?.showDoubleOptionsDialog(
            title,
            message,
            firstButton,
            secondButton,
            firstButtonListener,
            secondButtonListener,
        ) ?: run {
            DebugLog.w("To use this #showDoubleOptionsDialog() method, the activity hold this fragment have to extend from BaseActivity.")
            null
        }
    }

    override fun showDoubleOptionsDialog(
        title: Int?,
        message: Int,
        firstButton: Int,
        secondButton: Int,
        firstButtonListener: DialogButtonClickListener?,
        secondButtonListener: DialogButtonClickListener?,
    ): AlertDialog? {
        return baseActivity?.showDoubleOptionsDialog(
            title,
            message,
            firstButton,
            secondButton,
            firstButtonListener,
            secondButtonListener,
        ) ?: run {
            DebugLog.w("To use this #showDoubleOptionsDialog() method, the activity hold this fragment have to extend from BaseActivity.")
            null
        }
    }
}
