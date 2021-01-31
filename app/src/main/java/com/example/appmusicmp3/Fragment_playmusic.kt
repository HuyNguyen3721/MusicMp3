package com.example.appmusicmp3

import android.annotation.SuppressLint
import android.content.*
import android.media.MediaPlayer
import android.os.*

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.appmusicmp3.broadCast.MybroadcastFragmentplay
import com.example.appmusicmp3.broadCast.MybroadcastService
import com.example.appmusicmp3.databinding.LayoutPlaymusicBinding
import com.example.appmusicmp3.interfacemusic.IclickNotification
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.service.MyApp
import com.example.appmusicmp3.service.ServiceMusic
import org.jsoup.Jsoup
import java.text.SimpleDateFormat

class Fragment_playmusic : Fragment, View.OnClickListener,
    IclickNotification {
    private var arr: MutableList<Item_song> = mutableListOf()
    lateinit var binding: LayoutPlaymusicBinding
    var service: ServiceMusic? = null
    private var conection: ServiceConnection? = null
    private var pos = 0
    private var intent: IntentFilter? = null
    private lateinit var mybroadcast: MybroadcastFragmentplay

    constructor(arr: MutableList<Item_song>) {
        this.arr = arr
    }

    constructor()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pos = arguments!!.getInt("pos")
        openServiceUnBound()
        connection()
        rigester()
        binding = LayoutPlaymusicBinding.inflate(inflater, container, false)
        binding.datamusic = arr[pos]
        binding.ivplay.setImageResource(R.drawable.pause)
        binding.circleiv.startAnimation(
            AnimationUtils.loadAnimation(
                binding.circleiv.context,
                R.anim.iv_circle
            )
        )
        return binding.root
    }

    private fun connection() {
        val intent = Intent(context, ServiceMusic::class.java)
        conection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
                val binder = serviceBinder as ServiceMusic.Mybinder
                service = binder.service
                getLinkMp3()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }// gửi yêu càu kết nối
        context?.bindService(intent, conection!!, Context.BIND_AUTO_CREATE)
    }

    private fun openServiceUnBound() {
        val intent = Intent()
        intent.setClass(context!!, ServiceMusic::class.java)
        //bat service unbound
        //moi lan goi startService thi chac chan vao onStartCommand
        context!!.startService(intent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun rigester() {
        (context?.applicationContext as MyApp).datalive.linkMp3.observe(this, Observer {
            val simpledataformat = SimpleDateFormat("mm:ss")
            if(service?.mediaPlayer?.duration != null){
                binding.txtTotalTime.text = simpledataformat.format(service?.mediaPlayer?.duration)
                binding.seekBarMusic.max = service?.mediaPlayer?.duration!!
                binding.ivplay.setImageResource(R.drawable.pause)
                clickplaymusic()
                clickseekbar()
                updateTimeSong()
            }
        })
    }

    private fun clickplaymusic() {
        binding.ivplay.setOnClickListener(this)
        binding.ivnext.setOnClickListener(this)
        binding.ivback.setOnClickListener(this)
    }

    private fun clickseekbar() {
        binding.seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                service?.mediaPlayer?.seekTo(binding.seekBarMusic.progress)
            }

        })
    }

    private fun getLinkMp3() {
        if (arr[pos].link_music!!.startsWith("https")) {
            val asyn = @SuppressLint("StaticFieldLeak")
            object : AsyncTask<String, Void, String>() {
                override fun doInBackground(vararg params: String?): String {
                    try {
                        val linkCrawl = params[0]
                        val doc = Jsoup.connect(linkCrawl).get()
                        return doc.select("ul.list-unstyled").select("li")
                            .select("a.download_item")[1].attr("href")
                    } catch (e: Exception) {
                        //
                    }
                    return ""
                }

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onPostExecute(result: String) {
                    service?.playMusicMp3(pos, arr, result)
                }
            }
            asyn.execute(arr[pos].link_music)
        } else {
            service?.playMusicMp3(pos, arr, arr[pos].link_music!!)
        }
    }

    @SuppressLint("ResourceType", "UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivplay -> {
                if (service?.mediaPlayer?.isPlaying() == true) {
                    binding.ivplay.setImageResource(R.drawable.play)
                    service?.mediaPlayer?.pause()
                } else {
                    binding.ivplay.setImageResource(R.drawable.pause)
                    service?.mediaPlayer?.start()
                }
                service?.createNotification(pos, arr, service?.mediaPlayer!!.isPlaying)
            }
            R.id.ivnext -> {
                if (pos + 1 > arr.size - 1) {
                    pos = 0
                } else {
                    pos += 1
                }
                binding.datamusic = arr[pos]
                getLinkMp3()
            }
            R.id.ivback -> {
                if (pos - 1 < 0) {
                    pos = arr.size - 1
                } else {
                    pos -= 1
                }
                binding.datamusic = arr[pos]
                getLinkMp3()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateTimeSong() {
        val handle = Handler()
        handle.postDelayed(object : Runnable {
            override fun run() {
                val simpledataformat = SimpleDateFormat("mm:ss")
                binding.txtTimeMusic.text =
                    simpledataformat.format(service?.mediaPlayer?.currentPosition)
                binding.seekBarMusic.progress = service?.mediaPlayer?.currentPosition!!
                handle.postDelayed(this, 200)
            }
        }, 100)
    }

    override fun onDestroyView() {
        context!!.unbindService(conection!!)
        super.onDestroyView()
    }

    override fun onMNext() {
        if (pos + 1 > arr.size - 1) {
            pos = 0
        } else {
            pos += 1
        }
        binding.datamusic = arr[pos]
        binding.ivplay.setImageResource(R.drawable.pause)
//        loadTimeMusic()
    }

    override fun onMPause() {
            binding.ivplay.setImageResource(R.drawable.play)
    }

    override fun onMPlay() {
        binding.ivplay.setImageResource(R.drawable.pause)
    }

    override fun onMBack() {
        if (pos - 1 < 0) {
            pos = arr.size - 1
        } else {
            pos -= 1
        }
        binding.datamusic = arr[pos]
        binding.ivplay.setImageResource(R.drawable.pause)
//        loadTimeMusic()
    }


    override fun onStart() {
        rigisterBroadcast()
        super.onStart()
        //
        if (service != null) {
            arr = service!!.arr
            pos = service!!.pos
            binding.datamusic = arr[pos]
            if (service!!.mediaPlayer!!.isPlaying) {
                binding.ivplay.setImageResource(R.drawable.pause)
            } else {
                binding.ivplay.setImageResource(R.drawable.play)
            }
        }
    }

    private fun rigisterBroadcast() {
        mybroadcast = MybroadcastFragmentplay()
        mybroadcast.inter = this
        intent = IntentFilter()
        intent?.addAction("BACK")
        intent?.addAction("NEXT")
        intent?.addAction("PLAY")
        intent?.addAction("PAUSE")
        intent?.addAction("AUTO_NEXT")
        context!!.registerReceiver(mybroadcast, intent)
    }

    override fun onStop() {
        context!!.unregisterReceiver(mybroadcast)
        super.onStop()
    }
}

