package com.kobera.music.common.resource

import android.content.Context

class ResourceProvider(applicationContext: Context) {
    private val resources = applicationContext.resources

    fun getString(resourceId: Int): String {
        return resources.getString(resourceId)
    }
}