package com.example.synctime.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private fun getRetrofit(token: String? = null): Retrofit {
        val clientBuilder = OkHttpClient.Builder()
        
        if (!token.isNullOrBlank()) {
            val authInterceptor = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            clientBuilder.addInterceptor(authInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        getRetrofit().create(AuthApi::class.java)
    }

    fun getStaffApi(token: String): StaffApi {
        return getRetrofit(token).create(StaffApi::class.java)
    }

    fun getManagerAdminApi(token: String): ManagerAdminApi {
        return getRetrofit(token).create(ManagerAdminApi::class.java)
    }

    // Giữ lại hàm cũ để không gây lỗi compile ngay lập tức ở các nơi gọi cũ
    @Deprecated("Dùng getManagerAdminApi(token) thay thế", ReplaceWith("getManagerAdminApi(token)"))
    fun create(token: String): ManagerAdminApi = getManagerAdminApi(token)
}
