package com.example.appmusicmp3.broadCast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.appmusicmp3.interfacemusic.IAutoNext
import com.example.appmusicmp3.interfacemusic.IclickNotification
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.service.ServiceMusic
import org.jsoup.Jsoup

class Mybroadcast : BroadcastReceiver {
    private var inter: IclickNotification? = null
    private var iautonext: IAutoNext? = null
    var sevice: ServiceMusic? = null
    fun setInter(i: IclickNotification) {
        this.inter = i
    }

    fun setIAutoNext(a: IAutoNext) {
        this.iautonext = a
    }

    constructor()

    override fun onReceive(context: Context?, intent: Intent) {
        when (intent.action) {
            "BACK" -> {
                inter?.onMBack()
                if (sevice != null) {
                    if (sevice!!.pos - 1 < 0) {
                        sevice!!.pos = sevice!!.arr.size
                    } else {
                        sevice!!.pos -= 1
                    }
                    getLinkMp3(sevice!!.pos, sevice!!.arr)
                    sevice!!.createNotification(
                        sevice!!.pos,
                        sevice!!.arr,
                        true
                    )
                }
            }
            "PLAY" -> {
                inter?.onMPlay()
                    sevice?.mediaPlayer?.start()
                    sevice?.createNotification(
                        sevice!!.pos,
                        sevice!!.arr,
                        sevice!!.mediaPlayer!!.isPlaying
                    )
            }
            "PAUSE" -> {
                inter?.onMPause()
                if (sevice != null && sevice!!.mediaPlayer!!.isPlaying) {
                    sevice!!.mediaPlayer!!.pause()
                    sevice!!.createNotification(
                        sevice!!.pos,
                        sevice!!.arr,
                        sevice!!.mediaPlayer!!.isPlaying
                    )
                }
            }
            "NEXT" -> {
                inter?.onMNext()
                if (sevice != null) {
                    nextMusic()
                }
            }
            "AUTO_NEXT" -> {
                iautonext?.autonext()
                if (sevice != null) {
                    nextMusic()
                }
            }
        }

    }

    private fun nextMusic() {
        if (sevice!!.pos + 1 > sevice!!.arr.size - 1) {
            sevice!!.pos = 0
        } else {
            sevice!!.pos += 1
        }
        getLinkMp3(sevice!!.pos, sevice!!.arr)
        sevice!!.createNotification(
            sevice!!.pos,
            sevice!!.arr,
            true
        )
    }

    private fun getLinkMp3(pos: Int, arr: MutableList<Item_song>) {
        if (arr[pos].link_music!!.startsWith("https")) {
            val asyn = @SuppressLint("StaticFieldLeak")
            object : AsyncTask<String, Void, String>() {
                override fun doInBackground(vararg params: String?): String {
                    val linkCrawl = params[0]
                    val doc = Jsoup.connect(linkCrawl).get()
                    return doc.select("ul.list-unstyled").select("li")
                        .select("a.download_item")[1].attr("href")
                }

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onPostExecute(result: String) {
                    sevice?.playMusicMp3(pos, arr, result)
                    sevice?.mediaPlayer?.setOnPreparedListener {
                        sevice?.mediaPlayer?.start()
                    }
                }
            }
            asyn.execute(arr[pos].link_music)
        } else {
            sevice?.playMusicMp3(pos, arr, arr[pos].link_music!!)
            sevice?.mediaPlayer?.setOnPreparedListener {
                sevice?.mediaPlayer?.start()
            }
        }
    }
}