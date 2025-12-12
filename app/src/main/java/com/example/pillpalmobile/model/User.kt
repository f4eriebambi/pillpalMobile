package com.example.pillpalmobile.model

import androidx.annotation.DrawableRes

class User (
    val name: String, // this will be user input but rn mock
//    val nickname: String?, // this will be user input but rn mock
    val birthday: String, // this will be user input but rn mock
//    val dateJoined: String,  // this will be user input but rn mock
    @DrawableRes val avatarRes: Int
)