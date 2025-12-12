package com.example.pillpalmobile.network

import android.content.Context
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.network.SettingsService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://pillpal.space/"

    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()


        val token = AuthStore.getCachedToken()

        val newRequest = if (!token.isNullOrEmpty()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        chain.proceed(newRequest)
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val medicationService: MedicationService = retrofit.create(MedicationService::class.java)
    val historyService: HistoryService = retrofit.create(HistoryService::class.java)
    val calendarService: CalendarService = retrofit.create(CalendarService::class.java)
    val settingsService: SettingsService = retrofit.create(SettingsService::class.java)
    val deviceApi: DeviceService by lazy { retrofit.create(DeviceService::class.java) }

}
