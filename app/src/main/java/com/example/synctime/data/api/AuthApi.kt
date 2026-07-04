package com.example.synctime.data.api

import com.example.synctime.data.model.ApiResponse
import com.example.synctime.data.model.LoginRequest
import com.example.synctime.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>
}
