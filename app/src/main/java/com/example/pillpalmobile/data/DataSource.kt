package com.example.pillpalmobile.data

import com.example.pillpalmobile.R
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.User

object DataSource {
    // mock user details
    val user = User(
        name = "name",  // this will be user input but rn mock
        nickname = "namechan",  // this will be user input but rn mock
        birthday = "##/##/####", // this will be user input but rn mock
        dateJoined = "29/10/2025", // this will be from database input but rn mock
        avatarRes = R.drawable.pfp
    )

    // MutableList with detailed medication data using default values for missing fields
    val medications = mutableListOf(
        Medication(
            id = 1,
            name = "medication 1",
            reminderTimes = listOf("08:00", "20:00"),
            medicationDate = "Fri, Nov 7, 2025",
            repeatEnabled = false,
            notes = "Take with water"
        ),
        Medication(
            id = 2,
            name = "medication 2"
            // Uses all default values: reminderTimes=["10:00"], medicationDate="Fri, Nov 7, 2025", etc.
        ),
        Medication(
            id = 3,
            name = "medication 3",
            reminderTimes = listOf("09:00", "14:00", "21:00"),
            repeatEnabled = true,
            repeatFrequency = "Weekly",
            repeatDays = listOf("Mon", "Wed", "Fri"),
            notes = "Don't take with dairy"
        ),
        Medication(
            id = 4,
            name = "medication 4"
            // Uses all default values
        ),
        Medication(
            id = 5,
            name = "medication 5"
            // Uses all default values
        ),
        Medication(
            id = 6,
            name = "medication 6"
            // Uses all default values
        )
    )
}