package com.example.synctime.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    /*
        Nếu test bằng Android Emulator:
        Dùng http://10.0.2.2:8080/

        Nếu test bằng điện thoại thật:
        Đổi thành IP máy tính, ví dụ:
        http://192.168.1.10:8080/
    */
    private const val BASE_URL = "http://10.0.2.2:8080/"

    fun create(token: String): ManagerAdminApi {
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            if (token.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ManagerAdminApi::class.java)
    }
}