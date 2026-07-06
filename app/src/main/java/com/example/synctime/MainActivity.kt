package com.example.synctime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.synctime.utils.RoleGuard

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RoleGuard.routeHome(this)
    }
}