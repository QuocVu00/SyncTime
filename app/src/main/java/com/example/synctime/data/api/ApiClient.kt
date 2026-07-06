package com.example.synctime.data.api

object ApiClient {

    fun create(token: String? = null): com.example.synctime.api.ApiService {
        return com.example.synctime.api.ApiClient.create(token)
    }
}