package com.example.appmusicmp3

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appmusicmp3.adapter.Adapter_recycleList
import com.example.appmusicmp3.databinding.LayoutListSongBinding
import com.example.appmusicmp3.item.Item_song
import org.jsoup.Jsoup
import kotlin.random.Random

class Fragmnet_list_song : Fragment(), Adapter_recycleList.IOnClickList {
    private lateinit var binding: LayoutListSongBinding
    private var arrDataSongFull = mutableListOf<Item_song>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arrDataSongFull.clear()
        getDataFull("https://chiasenhac.vn/mp3/vietnam.html?tab=album-2020")
        for (i in 2..10) {
            getDataFull("https://chiasenhac.vn/mp3/vietnam.html?tab=album-2020&page=$i")
        }
        binding = LayoutListSongBinding.inflate(inflater, container, false)
        //
        binding.rclList.layoutManager = LinearLayoutManager(binding.root.context)
        val adapter = Adapter_recycleList(this)
        binding.rclList.adapter = adapter
        //
        binding.btnPlayrandom.setOnClickListener {
            val random = Random.nextInt(arrDataSongFull.size)
            (activity as MainActivity).fragment_playmusic(random, arrDataSongFull)
        }
        return binding.root
    }

    @SuppressLint("StaticFieldLeak")
    fun getDataFull(url: String) {
        val arr = mutableListOf<Item_song>()
        val asyn = object : AsyncTask<String, Void, MutableList<Item_song>>() {
            override fun doInBackground(vararg params: String?): MutableList<Item_song> {
                    try {
                        val linkCrawl = params[0]
                        val doc = Jsoup.connect(linkCrawl).get()
                        for (d in doc.select("div.row10px").select("div.col")) {
                            val link =
                                d.select("div.col").select("div.card ").select("div.card-header")
                                    .attr("style")
                            val sizes = link.split("(").size
                            val index = link.split("(").get(sizes - 1).lastIndexOf(')')
                            val linkImage = link.split("(").get(sizes - 1).substring(0, index)
                            var name =
                                d.select("div.col").select("div.card-body").select("h3")
                                    .select("a").text()
                            var singer = ""
                            for (i in d.select("div.col").select("div.card-body").select("p")
                                .select("a")) {
                                singer += i.select("a").text()
                            }
                            val linkMusic ="https://vi.chiasenhac.vn${d.select("div.col").select("div.card ").select("div.card-header")
                                .select("a").attr("href")}"
                            arr.add(Item_song(linkImage, name, singer, linkMusic))
                        }
                        return arr
                    }catch (e : Exception){
                        //
                    }
                return arr
            }

            override fun onPostExecute(result: MutableList<Item_song>) {
                arrDataSongFull.addAll(result)
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
                        arrDataSongFull.clear()
                        for (i in result) {
                            if (i.songName!!.startsWith(binding.edtsearchSong.text.toString())) {
                                arrDataSongFull.add(i)
                            }
                        }
                        binding.rclList.adapter?.notifyDataSetChanged()
                    }

                })
            }
        }
        asyn.execute(url)
    }

    override fun onClickList(position: Int) {
        (activity as MainActivity).fragment_playmusic(position, arrDataSongFull)
    }

    override fun getCount(): Int {
        return arrDataSongFull.size
    }

    override fun getData(position: Int): Item_song {
        return arrDataSongFull[position]
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}