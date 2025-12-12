package com.example.pillpalmobile

import android.app.Application
import com.example.pillpalmobile.data.AuthStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.pillpalmobile.network.RetrofitClient

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        RetrofitClient.initialize(this)


        CoroutineScope(Dispatchers.IO).launch {
            AuthStore.loadToken(this@MyApplication)
        }
    }
}


