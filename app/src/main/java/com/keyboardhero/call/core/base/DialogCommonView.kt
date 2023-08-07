package com.keyboardhero.call.core.base

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

/**
 * The interface define method to work with dialog.
 */
interface DialogCommonView {

    /**
     * Show dialog with one option button, usually for alert confirm purpose.
     * The dialog is persist with back press, and when user click on button, then the dialog will be
     * closed.
     *
     * @param title [String]: Text for title of dialog. It may be null when you don't want to show title.
     * @param message [String]: Text for message of dialog. It must not null, and we don't handle case empty.
     * @param button [String]: Text for button. It must not null, and we don't handle case empty.
     * @param listener [DialogButtonClickListener]: It will be called when use click on button.
     */
    fun showSingleOptionDialog(
        title: String? = null,
        message: String,
        button: String,
        listener: DialogButtonClickListener? = null,
    ): AlertDialog?

    /**
     * Show dialog with one option button, usually for alert confirm purpose.
     * The dialog is persist with back press, and when user click on button, then the dialog will be
     * closed.
     *
     * @param title [Int]: String resource for title of dialog. It may be null when you don't want to show title.
     * @param message [Int]: String resource for message of dialog. It must not null, and we don't handle case empty.
     * @param button [Int]: String resource for button. It must not null, and we don't handle case empty.
     * @param listener [DialogButtonClickListener]: It will be called when use click on button.
     */
    fun showSingleOptionDialog(
        title: Int? = null,
        message: Int,
        button: Int,
        listener: DialogButtonClickListener? = null,
    ): AlertDialog?

    /**
     * Show dialog with two option buttons, usually for alert decide purpose.
     * The dialog is persist with back press, and when user click on one in two buttons, then the dialog will be
     * closed.
     *
     * @param title [String]: Text for title of dialog. It may be null when you don't want to show title.
     * @param message [String]: Text for message of dialog. It must not null, and we don't handle case empty.
     * @param firstButton [String]: Text for positive button. It must not null, and we don't handle case empty.
     * @param secondButton [String]: Text for negative button. It must not null, and we don't handle case empty.
     * @param firstButtonListener [DialogButtonClickListener]: It will be called when use click on positive button.
     * @param secondButtonListener [DialogButtonClickListener]: It will be called when use click on negative button.
     */
    fun showDoubleOptionsDialog(
        title: String? = null,
        message: String,
        firstButton: String,
        secondButton: String,
        firstButtonListener: DialogButtonClickListener? = null,
        secondButtonListener: DialogButtonClickListener? = null,
    ): AlertDialog?

    /**
     * Show dialog with two option buttons, usually for alert decide purpose.
     * The dialog is persist with back press, and when user click on one in two buttons, then the dialog will be
     * closed.
     *
     * @param title [Int]: String resource for title of dialog. It may be null when you don't want to show title.
     * @param message [Int]: String resource for message of dialog. It must not null, and we don't handle case empty.
     * @param firstButton [Int]: String resource for positive button. It must not null, and we don't handle case empty.
     * @param secondButton [Int]: String resource for negative button. It must not null, and we don't handle case empty.
     * @param firstButtonListener [DialogButtonClickListener]: It will be called when use click on positive button.
     * @param secondButtonListener [DialogButtonClickListener]: It will be called when use click on negative button.
     */
    fun showDoubleOptionsDialog(
        title: Int? = null,
        message: Int,
        firstButton: Int,
        secondButton: Int,
        firstButtonListener: DialogButtonClickListener? = null,
        secondButtonListener: DialogButtonClickListener? = null,
    ): AlertDialog?
}

/**
 * Named anonymous function for button of dialog click listener.
 */
typealias DialogButtonClickListener = (dialog: DialogInterface) -> Unit
