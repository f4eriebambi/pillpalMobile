package com.example.pillpalmobile.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

object RetrofitClient {

//    private const val BASE_URL = "http://192.168.1.14:5000/"
     private const val BASE_URL = "http://10.0.2.2:5000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val medicationService: MedicationService by lazy {
        retrofit.create(MedicationService::class.java)
    }

    val historyService: HistoryService by lazy {
        retrofit.create(HistoryService::class.java)
    }



}
