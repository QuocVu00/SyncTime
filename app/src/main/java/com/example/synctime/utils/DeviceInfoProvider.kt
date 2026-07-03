package com.example.synctime.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

object DeviceInfoProvider {
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
    }
}
