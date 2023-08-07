package com.keyboardhero.call.core.utils.views

import android.view.View
import com.keyboardhero.call.core.utils.views.SafetyClickListener.Companion.MIN_DELAY_BETWEEN_CLICK

/**
 * Util help us control deliver click event of views which delegated to it. It's prevent
 * the click event is delivered to fast (may cause app error) when user click on elements two fast.
 *
 * @property delayBetweenClick: Define the min time between the delivery of two click events.
 * It's is optional, the default is [MIN_DELAY_BETWEEN_CLICK] milliseconds.
 */
class SafetyClickListener(
  private val delayBetweenClick: Long = MIN_DELAY_BETWEEN_CLICK
) : View.OnClickListener {
  private val listeners = mutableMapOf<Int, View.OnClickListener>()

  companion object {
    private const val MIN_DELAY_BETWEEN_CLICK = 300L

    private var lastClickTime = 0L
  }

  /**
   * Delegate click listener of view to [SafetyClickListener] manage.
   *
   * @param view [View]: The view which delegate listen click event.
   * @param listener: Is called when user clicked on view.
   */
  fun setViewClickSafetyListener(view: View, listener: View.OnClickListener) {
    listeners[view.id] = listener
    view.setOnClickListener(this)
  }

  override fun onClick(view: View?) {
    val currentTimeMillis = System.currentTimeMillis()
    if (view != null && currentTimeMillis - lastClickTime >= delayBetweenClick) {
      lastClickTime = currentTimeMillis

      listeners[view.id]?.onClick(view)
    }
  }

  /**
   * This method have to called when the screen use this class is destroyed. It will remove all
   * view references.
   */
  fun cleanListeners() {
    listeners.clear()
  }
}
