package com.example.appmusicmp3.broadCast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.appmusicmp3.interfacemusic.IclickNotification

class MybroadcastFragmentplay : BroadcastReceiver() {
     var inter: IclickNotification? = null
    override fun onReceive(p0: Context?, intent : Intent) {
        when (intent.action) {
            "BACK" -> {
                inter?.onMBack()
            }
            "PLAY" -> {
                inter?.onMPlay()
            }
            "PAUSE" -> {
                inter?.onMPause()
            }
            "NEXT","AUTO_NEXT" -> {
                inter?.onMNext()
            }
        }

    }

}