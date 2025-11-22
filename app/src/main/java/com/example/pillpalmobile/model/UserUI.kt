package com.example.pillpalmobile.model

import androidx.annotation.DrawableRes
import com.example.pillpalmobile.R


data class UserUI(
    val name: String,
    val birthday: String,
    @DrawableRes val avatarRes: Int = R.drawable.pfp
)
