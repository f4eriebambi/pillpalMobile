package com.example.pillpalmobile.data

import com.example.pillpalmobile.R
import com.example.pillpalmobile.model.Medication
import com.example.pillpalmobile.model.User

object DataSource {
    // mock user details
    val user = User(
        name = "name",  // this will be user input but rn mock
//        nickname = "namechan",  // this will be user input but rn mock
        birthday = "##/##/####", // this will be user input but rn mock
//        dateJoined = "29/10/2025", // this will be from database input but rn mock
        avatarRes = R.drawable.pfp
    )
    val medications = listOf(
        Medication(1, "medication 1"),
        Medication(2, "medication 2"),
        Medication(3, "medication 3"),
        Medication(4, "medication 4"),
        Medication(5, "medication 5"),
        Medication(6, "medication 6")
    )
}