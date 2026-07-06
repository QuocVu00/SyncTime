package com.example.synctime.utils

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.synctime.AdminHomeActivity
import com.example.synctime.LoginActivity
import com.example.synctime.ManagerHomeActivity
import com.example.synctime.StaffHomeActivity

object RoleGuard {

    fun requireLogin(activity: Activity): Boolean {
        if (!AuthManager.isLoggedIn(activity)) {
            goLogin(activity)
            return false
        }
        return true
    }

    fun requireRole(
        activity: Activity,
        vararg allowedRoles: String
    ): Boolean {
        if (!requireLogin(activity)) return false

        val currentRole = AuthManager.getRole(activity)
        val allowed = allowedRoles.map { it.uppercase() }

        if (currentRole !in allowed) {
            Toast.makeText(activity, "Bạn không có quyền truy cập màn hình này", Toast.LENGTH_LONG).show()
            routeHome(activity)
            return false
        }

        return true
    }

    fun routeHome(activity: Activity) {
        val role = AuthManager.getRole(activity)

        val target = when (role) {
            "ADMIN" -> AdminHomeActivity::class.java
            "MANAGER" -> ManagerHomeActivity::class.java
            "STAFF" -> StaffHomeActivity::class.java
            else -> LoginActivity::class.java
        }

        val intent = Intent(activity, target)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

    fun goLogin(activity: Activity) {
        AuthManager.logout(activity)

        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }
}