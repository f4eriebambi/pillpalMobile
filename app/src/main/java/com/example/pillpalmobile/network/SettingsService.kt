package com.example.pillpalmobile.network

import retrofit2.http.Body
import retrofit2.http.POST

data class NotificationSettingsRequest(
    val sound: Boolean,
    val vibration: Boolean,
    val device_notifications: Boolean
)

data class SettingsResponse(
    val status: String
)

interface SettingsService {
    @POST("api/settings/update")
    suspend fun updateSettings(
        @Body body: NotificationSettingsRequest
    ): SettingsResponse
}
