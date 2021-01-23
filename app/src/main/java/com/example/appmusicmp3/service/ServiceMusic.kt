package com.example.appmusicmp3.service

import android.app.*
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.appmusicmp3.MainActivity
import com.example.appmusicmp3.broadCast.Mybroadcast
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.R

class ServiceMusic : Service() {
    var mediaPlayer: MediaPlayer? = null
    var pos = 0
    var arr = mutableListOf<Item_song>()
    private lateinit var mybroadcast: Mybroadcast
    lateinit var notificationManager: NotificationManager

    // service dispaly list music view  se rang buoc vs  view  dung boud sẻvice
    override fun onCreate() {
        super.onCreate()
    }

    // service chuyền dữ liệu trả về dạng binder
    class Mybinder(val service: ServiceMusic) : Binder()

    override fun onBind(intent: Intent?): IBinder {
        return Mybinder(this)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mybroadcast = Mybroadcast()
        mybroadcast.sevice = this
        val intentfiler = IntentFilter()
        intentfiler.addAction("BACK")
        intentfiler.addAction("NEXT")
        intentfiler.addAction("PLAY")
        intentfiler.addAction("PAUSE")
        intentfiler.addAction("AUTO_NEXT")
        this.registerReceiver(mybroadcast, intentfiler)
        return START_NOT_STICKY
    }

    fun playMusicMp3(pos: Int, arr: MutableList<Item_song>, linkMp3: String) {
        this.pos = pos
        this.arr = arr
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnErrorListener { mp, what, extra -> true }
        if(linkMp3.startsWith("https")){
            mediaPlayer?.setDataSource(linkMp3)
        }else{
            Log.d("huy","media off")
            mediaPlayer?.setDataSource( this, Uri.parse(linkMp3))
        }
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.prepareAsync()
        (applicationContext as MyApp).datalive.linkMp3.value = linkMp3
        mediaPlayer?.setOnCompletionListener {
            val intent = Intent("AUTO_NEXT")
            sendBroadcast(intent)
        }
    }
    fun createNotification(pos: Int, arr: MutableList<Item_song>, isplaying: Boolean) {
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createChanle()
        // create content of notificaton ; remoteview
        val remoteview = RemoteViews(packageName, R.layout.layout_notification)
        remoteview.setTextViewText(R.id.txtname, arr[pos].songName)
        remoteview.setTextViewText(R.id.txtsinger, arr[pos].singer)
        remoteview.setImageViewBitmap(
            R.id.ivplayNoti,
            BitmapFactory.decodeResource(
                resources,
                if (isplaying) R.drawable.baseline_pause_purple_500_48dp else R.drawable.baseline_play_arrow_purple_500_48dp
            )
        )
        val notification = NotificationCompat.Builder(this, "No")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.iconnotifi)
            .setCustomBigContentView(remoteview)
            .build()

        if (arr[pos].uriImage != null) {
            Glide.with(this).asBitmap().load(
                arr[pos].uriImage
            ).into(
                object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        remoteview.setImageViewBitmap(R.id.ivImageSong, resource)
                        startForeground(1, notification)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                }
            )
        }
        pendingIntent(remoteview, isplaying)
        startForeground(1, notification)
    }

    private fun pendingIntent(rv: RemoteViews, isplaying: Boolean) {
        val intenBack = Intent()
        intenBack.action = "BACK"
        val pendingIntentBack =
            PendingIntent.getBroadcast(this, 0, intenBack, PendingIntent.FLAG_UPDATE_CURRENT)
        rv.setOnClickPendingIntent(R.id.ivbackNoti, pendingIntentBack)
        if (isplaying) {
            val intenPause = Intent()
            intenPause.action = "PAUSE"
            val pendingIntentPause =
                PendingIntent.getBroadcast(this, 0, intenPause, PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setOnClickPendingIntent(R.id.ivplayNoti, pendingIntentPause)
        } else {
            val intenPlay = Intent()
            intenPlay.action = "PLAY"
            val pendingIntentPlay =
                PendingIntent.getBroadcast(this, 0, intenPlay, PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setOnClickPendingIntent(R.id.ivplayNoti, pendingIntentPlay)
        }
        val intenNext = Intent()
        intenNext.action = "NEXT"
        val pendingIntentNext =
            PendingIntent.getBroadcast(this, 0, intenNext, PendingIntent.FLAG_UPDATE_CURRENT)
        rv.setOnClickPendingIntent(R.id.ivnextNoti, pendingIntentNext)
        val intentOpen = Intent(this, MainActivity::class.java)
            intentOpen.putExtra("Pos",pos)
            intentOpen.putExtra("Arr", arr as ArrayList<Item_song>)
        val pendingIntentOpen  =  PendingIntent.getActivity(this,1,intentOpen,PendingIntent.FLAG_UPDATE_CURRENT)
        rv.setOnClickPendingIntent(R.id.layoutNoti , pendingIntentOpen)
    }


    private fun createChanle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("No", "Music", importance)
            mChannel.description = "No"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onDestroy() {
        this.unregisterReceiver(mybroadcast)
        super.onDestroy()
    }
}