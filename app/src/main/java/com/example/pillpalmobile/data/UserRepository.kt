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
}
