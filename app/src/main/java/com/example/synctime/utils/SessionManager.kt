package com.example.synctime.utils

import android.content.Context

object SessionManager {

    private const val PREF_NAME = "synctime_session"

    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_FULL_NAME = "full_name"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"
    private const val KEY_BRANCH_ID = "branch_id"

    fun saveSession(
        context: Context,
        token: String,
        userId: Int,
        fullName: String,
        email: String,
        role: String,
        branchId: Int?
    ) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        pref.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROLE, role)
            .putInt(KEY_BRANCH_ID, branchId ?: -1)
            .apply()
    }

    fun getToken(context: Context): String? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_TOKEN, null)
    }

    fun getUserId(context: Context): Int {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getInt(KEY_USER_ID, -1)
    }

    fun getFullName(context: Context): String {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_FULL_NAME, "") ?: ""
    }

    fun getEmail(context: Context): String {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_EMAIL, "") ?: ""
    }

    fun getRole(context: Context): String {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getString(KEY_ROLE, "") ?: ""
    }

    fun getBranchId(context: Context): Int? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val value = pref.getInt(KEY_BRANCH_ID, -1)
        return if (value == -1) null else value
    }

    fun isLoggedIn(context: Context): Boolean {
        return !getToken(context).isNullOrBlank()
    }

    fun clearSession(context: Context) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().clear().apply()
    }
}