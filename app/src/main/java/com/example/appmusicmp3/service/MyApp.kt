package com.example.appmusicmp3.service

import android.app.Application

class MyApp : Application() {
    //class tung gian chuyen den class chứa giữa liệu livedata
    lateinit var datalive : LiveData
    override fun onCreate() {
            datalive =  LiveData()
        super.onCreate()
    }
}