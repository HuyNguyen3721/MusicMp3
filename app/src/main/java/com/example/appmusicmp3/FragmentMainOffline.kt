package com.example.appmusicmp3

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.appmusicmp3.adapter.Adapter_recycleList
import com.example.appmusicmp3.databinding.LayoutMainOfflineBinding
import com.example.appmusicmp3.item.Item_song
import kotlin.random.Random

class FragmentMainOffline : Fragment(), Adapter_recycleList.IOnClickList {
    private lateinit var binding: LayoutMainOfflineBinding
    private var arr = mutableListOf<Item_song>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.requestPermissions(
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        binding = LayoutMainOfflineBinding.inflate(inflater, container, false)
        binding.rclList.layoutManager = LinearLayoutManager(binding.rclList.context)
        binding.rclList.adapter = Adapter_recycleList(this)
        binding.btnPlayrandom.setOnClickListener{
            val random =  Random.nextInt(arr.size)
            (activity as MainActivity).fragment_playmusic(random, arr)
        }
        return binding.root
    }

    private fun getDataMusicOffline() {
        arr.clear()
        val result =  mutableListOf<Item_song>()
//        uri dia chi cua bang
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = context?.contentResolver
            ?.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        while (!cursor.isAfterLast) {
            val path = cursor.getString(
                cursor.getColumnIndex("_data")
            )
            val Arrname = path.split("/")
            val name = Arrname[(Arrname.size - 1)]
            result.add(
                Item_song(
                    "https://media.idownloadblog.com/wp-content/uploads/2018/03/Apple-Music-icon-003.jpg",
                    name,
                    "",
                    path
                )
            )
            cursor.moveToNext()
        }
        cursor.close()
        arr.addAll(result)
        binding.txtTotalSongs.text =  arr.size.toString()
        binding.rclList.adapter?.notifyDataSetChanged()
        binding.edtsearchSong.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {
                arr.clear()
                for (i in result) {
                    if (i.songName!!.startsWith(binding.edtsearchSong.text.toString())) {
                        arr.add(i)
                    }
                }
                binding.rclList.adapter?.notifyDataSetChanged()
            }

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDataMusicOffline()
        } else {
            Toast.makeText(context, "Bạn không cấp quyền truy cập", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onClickList(position: Int) {
        (activity as MainActivity).fragment_playmusic(position,arr)
    }

    override fun getCount(): Int {
        return arr.size
    }

    override fun getData(position: Int): Item_song {
        return arr[position]
    }
}