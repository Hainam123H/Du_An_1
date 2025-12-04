// ============================================
// FILE: RetrofitClient.kt (Android Studio)
// Cấu hình Retrofit để kết nối API
// ============================================

package com.example.bangiay.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object RetrofitClient {

    // Đổi địa chỉ IP của máy bạn
    private const val BASE_URL = "http://192.168.0.100:3000/api/"
    // Hoặc dùng localhost nếu chạy emulator
    // private const val BASE_URL = "http://10.0.2.2:3000/api/"

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
