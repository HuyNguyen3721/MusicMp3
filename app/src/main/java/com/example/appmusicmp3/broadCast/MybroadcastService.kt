package com.example.appmusicmp3.broadCast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.service.ServiceMusic
import org.jsoup.Jsoup

class MybroadcastService : BroadcastReceiver() {
    var sevice: ServiceMusic? = null
    override fun onReceive(context: Context?, intent: Intent) {
        when (intent.action) {
            "BACK" -> {
                if (sevice != null) {
                    if (sevice!!.pos - 1 < 0) {
                        sevice!!.pos = sevice!!.arr.size - 1
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
                sevice?.mediaPlayer?.start()
                sevice?.createNotification(
                    sevice!!.pos,
                    sevice!!.arr,
                    sevice!!.mediaPlayer!!.isPlaying
                )
            }
            "PAUSE" -> {
                if (sevice != null && sevice!!.mediaPlayer!!.isPlaying) {
                    sevice!!.mediaPlayer!!.pause()
                    sevice!!.createNotification(
                        sevice!!.pos,
                        sevice!!.arr,
                        sevice!!.mediaPlayer!!.isPlaying
                    )
                }
            }
            "NEXT","AUTO_NEXT" -> {
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
                    try {
                        val linkCrawl = params[0]
                        val doc = Jsoup.connect(linkCrawl).get()
                        return doc.select("ul.list-unstyled").select("li")
                            .select("a.download_item")[1].attr("href")
                    }catch (e : java.net.SocketTimeoutException){
                        //
                    }
                    return ""
                }
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onPostExecute(result: String) {
                    sevice?.playMusicMp3(pos, arr, result)
                }
            }
            asyn.execute(arr[pos].link_music)
        } else {
            sevice?.playMusicMp3(pos, arr, arr[pos].link_music!!)
        }
    }
}