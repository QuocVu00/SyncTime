package com.example.synctime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ManagerAdminViewModelFactory(
    private val token: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManagerAdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ManagerAdminViewModel(token) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}