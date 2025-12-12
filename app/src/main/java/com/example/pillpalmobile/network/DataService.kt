package com.example.pillpalmobile.network

import retrofit2.http.GET
import retrofit2.http.Query

data class AlertStatusResponse(
    val led: Boolean?,
    val sound: Boolean?,
    val vibration: Boolean?,
    val should_alert: Boolean?
)

interface DeviceService {

    @GET("api/device/alert_status")
    suspend fun getAlertStatus(
        @Query("device_id") deviceId: Int
    ): AlertStatusResponse
}
