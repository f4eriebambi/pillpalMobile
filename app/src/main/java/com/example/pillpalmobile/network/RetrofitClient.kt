package com.example.pillpalmobile.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.14:5000/" // THIS IS JUST FOR MY PHONE
//    private const val BASE_URL = "http://10.0.2.2:5000" FOR YOU GUYS, THE EMULATOR
    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}
