package com.example.appmusicmp3

import android.annotation.SuppressLint
import android.content.*
import android.os.*
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.appmusicmp3.broadCast.Mybroadcast
import com.example.appmusicmp3.databinding.LayoutPlaymusicBinding
import com.example.appmusicmp3.interfacemusic.IAutoNext
import com.example.appmusicmp3.interfacemusic.IclickNotification
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.service.MyApp
import com.example.appmusicmp3.service.ServiceMusic
import org.jsoup.Jsoup
import java.text.SimpleDateFormat

class Fragment_playmusic : Fragment, View.OnClickListener,
    IclickNotification , IAutoNext {
    private var arr: MutableList<Item_song> = mutableListOf()
    lateinit var binding: LayoutPlaymusicBinding
    var service: ServiceMusic? = null
    private var conection: ServiceConnection? = null
    private var pos = 0
    private var mybroadcast: Mybroadcast? = null
    private var intent: IntentFilter? = null
    private var linkMp3: String? = null
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
        getLinkMp3()
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
                if(!arr[pos].link_music!!.startsWith("https")){
                    service!!.playMusicMp3(pos, arr, arr[pos].link_music!!)
                    linkMp3 =  arr[pos].link_music!!
                }
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
            service?.mediaPlayer?.setOnCompletionListener {
                binding.ivplay.setImageResource(R.drawable.play)
            }
            service?.mediaPlayer?.setOnPreparedListener {
                binding.txtTotalTime.text = simpledataformat.format(service!!.mediaPlayer?.duration)
                binding.seekBarMusic.max = service!!.mediaPlayer?.duration!!
                service!!.mediaPlayer?.start()
                binding.ivplay.setImageResource(R.drawable.pause)
                service?.createNotification(pos, arr, service?.mediaPlayer!!.isPlaying)
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
        if(arr[pos].link_music!!.startsWith("https")) {
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
                    Log.d("huy" , "link https")
                    service?.playMusicMp3(pos, arr, result)
                    linkMp3 = result
                }
            }
            asyn.execute(arr[pos].link_music)
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
                    service?.createNotification(pos, arr, service?.mediaPlayer!!.isPlaying)
                } else {
                    binding.ivplay.setImageResource(R.drawable.pause)
                    service?.mediaPlayer?.start()
                    service?.createNotification(pos, arr, service?.mediaPlayer!!.isPlaying)
                }
            }
            R.id.ivnext -> {
                if (pos + 1 > arr.size - 1) {
                    pos = 0
                    binding.datamusic = arr[pos]
                    getLinkMp3()
                } else {
                    pos += 1
                    binding.datamusic = arr[pos]
                    getLinkMp3()
                }
            }
            R.id.ivback -> {
                if (pos - 1 < 0) {
                    pos = arr.size - 1
                    binding.datamusic = arr[pos]
                    getLinkMp3()
                } else {
                    pos -= 1
                    binding.datamusic = arr[pos]
                    getLinkMp3()
                }

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
            binding.datamusic = arr[pos]
            binding.ivplay.setImageResource(R.drawable.pause)
        } else {
            pos += 1
            binding.datamusic = arr[pos]
            binding.ivplay.setImageResource(R.drawable.pause)
        }
    }

    override fun onMPause() {
        if(service?.mediaPlayer!!.isPlaying){
            binding.ivplay.setImageResource(R.drawable.play)
        }else {
            Toast.makeText(context, "Đang tải nhạc..." , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMPlay() {
        binding.ivplay.setImageResource(R.drawable.pause)
    }

    override fun onMBack() {
        if (pos - 1 < 0) {
            pos = arr.size - 1
            binding.datamusic = arr[pos]
            binding.ivplay.setImageResource(R.drawable.pause)
        } else {
            pos -= 1
            binding.datamusic = arr[pos]
            binding.ivplay.setImageResource(R.drawable.pause)
        }
    }

    override fun onStart() {
        super.onStart()
        mybroadcast = Mybroadcast()
        mybroadcast!!.setInter(this)
        mybroadcast!!.setIAutoNext(this)
        intent = IntentFilter()
        intent?.addAction("BACK")
        intent?.addAction("NEXT")
        intent?.addAction("PLAY")
        intent?.addAction("PAUSE")
        intent?.addAction("AUTO_NEXT")
        context!!.registerReceiver(mybroadcast, intent)
        //
        if(service != null){
            arr = service!!.arr
            pos = service!!.pos
            binding.datamusic = arr[pos]
            if(service!!.mediaPlayer!!.isPlaying){
                binding.ivplay.setImageResource(R.drawable.pause)
            }else{
                binding.ivplay.setImageResource(R.drawable.play)
            }
        }
    }

    override fun onStop() {
        context!!.unregisterReceiver(mybroadcast)
        super.onStop()
    }
    override fun autonext() {
        if (pos + 1 > arr.size - 1) {
            pos = 0
            binding.datamusic = arr[pos]
            getLinkMp3()
        } else {
            pos += 1
            binding.datamusic = arr[pos]
            getLinkMp3()
        }
    }
}

