package com.example.synctime.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    /*
        Nếu chạy app bằng Android Emulator:
        dùng http://10.0.2.2:8080/

        Nếu chạy bằng điện thoại thật:
        đổi thành IP laptop, ví dụ:
        http://192.168.1.5:8080/
    */
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val managerAdminApi: ManagerAdminApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ManagerAdminApi::class.java)
    }
}