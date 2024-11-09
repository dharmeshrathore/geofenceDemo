package com.dharmesh.geofencedemo.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.dharmesh.geofencedemo.R
import com.google.android.material.snackbar.Snackbar

@SuppressLint("RestrictedApi")
fun View.showSnackBar(
    message: String,
    status: SnackBarStatus = SnackBarStatus.Success,
    snackBarTime: Int = Snackbar.LENGTH_LONG,
) {
    val snackBar = Snackbar.make(this, "", snackBarTime)

    val customSnackView: View =
        View.inflate(this.context, R.layout.custom_snack_bar_layout, null)

    val textViewTitle = customSnackView.findViewById<TextView>(R.id.textview_toast)

    customSnackView.background =
        AppCompatResources.getDrawable(this.context, R.drawable.snack_bar_background)

    // set the background of the default snackBar as transparent
    snackBar.view.setBackgroundColor(Color.TRANSPARENT)

    // change color based on statue
    when (status) {
        SnackBarStatus.Failure -> {
            textViewTitle.setBackgroundColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.snack_bar_failure_background_color,
                ),
            )
            textViewTitle.setTextColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.snack_bar_text_failure_color,
                ),
            )
        }

        else -> {
            textViewTitle.setBackgroundColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.snack_bar_success_background_color,
                ),
            )
            textViewTitle.setTextColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.snack_bar_success_text_color,
                ),
            )
        }
    }

    // now change the layout of the snackBar
    val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout

    val typedValue = TypedValue()
    var actionBarHeight = 0
    if (this.context.theme.resolveAttribute(
            androidx.appcompat.R.attr.actionBarSize,
            typedValue,
            true
        )
    ) {
        actionBarHeight =
            TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    }

    val params: FrameLayout.LayoutParams = snackBarLayout.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    params.setMargins(params.leftMargin, params.topMargin + actionBarHeight, params.rightMargin, 0)
    snackBarLayout.layoutParams = params

    snackBarLayout.setPadding(0, 0, 0, 0)
    textViewTitle.text = message
    snackBarLayout.addView(customSnackView, 0)

    snackBar.show()
}

enum class SnackBarStatus {
    Success,
    Failure,
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}

