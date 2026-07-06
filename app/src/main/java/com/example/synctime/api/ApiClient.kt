package com.example.synctime.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    /*
        Emulator Android dùng:
        http://10.0.2.2:8080/

        Điện thoại thật dùng IP máy tính, ví dụ:
        http://192.168.1.10:8080/
    */
    private const val BASE_URL = "http://10.0.2.2:8080/"

    var token: String? = null

    fun create(customToken: String? = token): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()

                val finalToken = customToken ?: token

                if (!finalToken.isNullOrBlank()) {
                    builder.addHeader("Authorization", "Bearer $finalToken")
                }

                chain.proceed(builder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}