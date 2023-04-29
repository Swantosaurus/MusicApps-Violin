package com.kobera.music.common.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Finds the activity from a context.
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
