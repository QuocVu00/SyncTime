package com.example.synctime.utils

import android.content.Context
import com.example.synctime.api.ApiClient
import com.example.synctime.data.model.AuthUser

object AuthManager {

    private const val PREF_NAME = "synctime_prefs"

    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_FULL_NAME = "full_name"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"
    private const val KEY_BRANCH_ID = "branch_id"

    fun saveLogin(
        context: Context,
        token: String,
        user: AuthUser?
    ) {
        val role = user?.role.orEmpty().uppercase()

        ApiClient.token = token

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, user?.id ?: 0)
            .putString(KEY_FULL_NAME, user?.fullName.orEmpty())
            .putString(KEY_EMAIL, user?.email.orEmpty())
            .putString(KEY_ROLE, role)
            .putInt(KEY_BRANCH_ID, user?.branchId ?: -1)
            .apply()
    }

    fun getToken(context: Context): String {
        val token = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, "")
            .orEmpty()

        ApiClient.token = token
        return token
    }

    fun getRole(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ROLE, "")
            .orEmpty()
            .uppercase()
    }

    fun getFullName(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FULL_NAME, "")
            .orEmpty()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getToken(context).isNotBlank() && getRole(context).isNotBlank()
    }

    fun logout(context: Context) {
        ApiClient.token = null

        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}