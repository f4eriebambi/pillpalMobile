package com.example.pillpalmobile.data

import com.example.pillpalmobile.model.*

class UserRepository(
    private val api: ApiService
) {
    suspend fun register(request: RegisterRequest) =
        api.register(request)

    suspend fun login(request: LoginRequest) =
        api.login(request)

    suspend fun getMedications(userId: Int) =
        api.getMedications(userId)

    suspend fun addMedication(
        user_id: Int,
        name: String,
        notes: String?,
        times: List<String>,
        repeat_type: String,
        day_mask: String
    ) = api.addMedication(
        AddMedicationRequest(
            user_id = user_id,
            name = name,
            notes = notes,
            times = times,
            repeat_type = repeat_type,
            day_mask = day_mask
        )
    )
}
