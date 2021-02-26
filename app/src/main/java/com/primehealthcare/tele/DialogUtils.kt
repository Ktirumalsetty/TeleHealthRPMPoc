package com.primehealthcare.tele

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar

/**
 * Created by KondalRao Tirumalasetty on 8/27/2019.
 */

/**
 * Purpose of this Class is to display different dialog in application.
 */
object DialogUtils {

    /**
     * Displays alert dialog to show common messages.
     *
     * @param message Message to be shown in the Dialog displayed
     * @param context Context of the Application, where the Dialog needs to be displayed
     */
    fun displayDialog(context: Context, message: String) {

        val alertDialog = AlertDialog.Builder(context)
            .create()
        // alertDialog.setTitle(context.getString(R.string.app_name))
        alertDialog.setCancelable(false)

        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, context.getString(android.R.string.ok)
        ) { dialog, _ -> dialog.dismiss() }

        if (!(context as Activity).isFinishing) {
            alertDialog.show()
        }
    }

    /**
     * Displays alert dialog to show common messages.
     *
     * @param message Message to be shown in the Dialog displayed
     * @param context Context of the Application, where the Dialog needs to be displayed
     */
    fun displayDialogWithTitle(context: Context,title:String, message: String) {

        val alertDialog = AlertDialog.Builder(context)
            .create()
         alertDialog.setTitle(title)
        alertDialog.setCancelable(false)

        alertDialog.setMessage(message)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, context.getString(android.R.string.ok)
        ) { dialog, _ -> dialog.dismiss() }

        if (!(context as Activity).isFinishing) {
            alertDialog.show()
        }
    }


    /**
     * Displays toast message
     *
     * @param mContext requires to create Toast
     */
    fun showToast(
        mContext: Context,
        message: String
    ) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
            .show()
    }

    fun showSnackBarShort(activity: Activity, message: String) {
        Snackbar.make(
            activity.findViewById(android.R.id.content), //                findViewById(com.google.android.material.R.id.snackbar_text),
            message, Snackbar.LENGTH_SHORT
        ).show()//Assume "rootLayout" as the root layout of every activity.?.show()

    }

    fun showSnackBarLong(activity: Activity, message: String) {
        Snackbar.make(
            activity.findViewById(android.R.id.content), //                findViewById(com.google.android.material.R.id.snackbar_text),
            message, Snackbar.LENGTH_LONG
        ).show()//Assume "rootLayout" as the root layout of every activity.?.show()

    }
}