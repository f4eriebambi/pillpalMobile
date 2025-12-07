package com.example.pillpalmobile.model

import com.google.gson.annotations.SerializedName

data class CalendarDose(
    @SerializedName("instance_id")
    val instanceId: Int,

    val name: String,
    val time: String,
    val status: String
)
