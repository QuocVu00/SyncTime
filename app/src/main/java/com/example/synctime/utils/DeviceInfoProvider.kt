package com.example.synctime.utils

import android.content.Context
import android.provider.Settings

object DeviceInfoProvider {

    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ).orEmpty()
    }
}