package com.example.appmusicmp3

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appmusicmp3.adapter.Adapter_recycleList
import com.example.appmusicmp3.adapter.Adapter_recycle_album
import com.example.appmusicmp3.adapter.Adapter_recycle_love
import com.example.appmusicmp3.adapter.Adpater_recycle_BXH
import com.example.appmusicmp3.databinding.LayoutMainBinding
import com.example.appmusicmp3.item.Item_BXH
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.item.Data_object
import org.jsoup.Jsoup

class FragmentMainOnline : Fragment(), Adapter_recycle_love.IOnClickLove, Adpater_recycle_BXH.IOnClickBXH,
    Adapter_recycle_album.IOnClickAlbum, Adapter_recycleList.IOnClickList {
    private var arrlove = mutableListOf<Item_song>()
    private var arralbum = mutableListOf<Item_song>()
    private var arrlistsong = mutableListOf<Item_song>()
    private var arrbxh = mutableListOf<Item_BXH>()
    private lateinit var binding: LayoutMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadData("https://chiasenhac.vn/")
        binding = LayoutMainBinding.inflate(layoutInflater, container, false)
        binding.rclULOVE.layoutManager =
            LinearLayoutManager(binding.rclULOVE.context, LinearLayoutManager.HORIZONTAL, false)
        val adapterlove = Adapter_recycle_love(this)
        binding.rclULOVE.adapter = adapterlove

        binding.rclAlbum.layoutManager =
            LinearLayoutManager(binding.rclAlbum.context, LinearLayoutManager.HORIZONTAL, false)
        val adapteralbum = Adapter_recycle_album(this)
        binding.rclAlbum.adapter = adapteralbum
        // image bxh
        binding.rclBXH.layoutManager =
            LinearLayoutManager(binding.rclBXH.context, LinearLayoutManager.HORIZONTAL, false)
        val adapterbxh = Adpater_recycle_BXH(this)
        binding.rclBXH.adapter = adapterbxh
        // list music bxh
        binding.rclListSong.layoutManager =
            LinearLayoutManager(binding.rclBXH.context)
        val adapterlistsong = Adapter_recycleList(this)
        binding.rclListSong.adapter = adapterlistsong
        binding.txtMore.setOnClickListener { (activity as MainActivity).fragmnet_list_song() }
        // search song
        return binding.root
    }

    // aysn thay đổi dữ liệu
    fun loadData(url: String) {
        val asyn = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<String, Void, Data_object>() {
            override fun doInBackground(vararg params: String?): Data_object {
                val linkCrawl = params[0]
                val doc = Jsoup.connect(linkCrawl).get()
                val arr = mutableListOf<Item_song>()
                val arrlist = mutableListOf<Item_song>()
                val arrbxh = mutableListOf<Item_BXH>()
                arrbxh.add(Item_BXH("https://data.chiasenhac.com/imgs/bxh/BXHNhacVietNam_245x140.png"))
                arrbxh.add(Item_BXH("https://data.chiasenhac.com/imgs/bxh/BXHVideo_245x140.png"))
                arrbxh.add(Item_BXH("https://data.chiasenhac.com/imgs/bxh/BXHNhacUs-UK_245x140.png"))
                arrbxh.add(Item_BXH("https://data.chiasenhac.com/imgs/bxh/BXHNhacHoa_245x140.png"))
                arrbxh.add(Item_BXH("https://data.chiasenhac.com/imgs/bxh/BXHNhacHan_245x140.png"))
                //
                for (d in doc.select("div.tab-content").select("div.tab-pane")
                    .select("ul.list-unstyled").select("li.media")) {
                    val name =
                        d.select("li.media").select("div.media-left").select("a").attr("title")
                    val linkImg =
                        d.select("li.media").select("div.media-left").select("a").select("img")
                            .attr("src")
                    val linkMusic = "https://vi.chiasenhac.vn${d.select("li.media").select("div.media-left").select("a").attr("href")}"
                    var songer = ""
                    for (i in d.select("li.media").select("div.media-body")
                        .select("div.align-items-center").select("div.author")) {
                        songer += i.select("a").text()
                    }
                    arrlist.add(Item_song(linkImg, name, songer, linkMusic))
                }
                //
                for (d in doc.select("div.col-md-9").select("div.row10px").select("div.col")) {
                    try {
                        val link = d.select("div.col").select("div.card").select("div.card-header")
                            .attr("style")
                        val index = link.indexOf('(')
                        val index2 = link.lastIndexOf(')')
                        val linkImage = link.substring(index + 1, index2)
                        val name = d.select("div.col").select("div.card").select("div.card-body")
                            .select("h3").select("a").text()
                        var singer = ""
                        for (i in d.select("div.col").select("div.card-body").select("p")
                            .select("a")) {
                            singer += i.select("a").text()
                        }
                        val linkMusic ="https://vi.chiasenhac.vn${d.select("div.col").select("div.card ").select("div.card-header").select("a").attr("href")}"
                        arr.add(Item_song(linkImage, name, singer, linkMusic))
                        if (arr.size == 10) {
                            break
                        }
                    } catch (e: Exception) {
                        //
                    }
                }

                return Data_object(arr, arrbxh, arrlist)
            }

            override fun onPostExecute(result: Data_object) {
                arralbum.clear()
                arrlove.clear()
                arrbxh.clear()
                arrlistsong.clear()
                for (i in 0..4) {
                    arralbum.add(result.arr.get(i))
                }
                for (i in 5..9) {
                    arrlove.add(result.arr.get(i))
                }
                arrlistsong.addAll(result.arrlist)
                arrbxh.addAll(result.arrBXH)
                binding.rclULOVE.adapter?.notifyDataSetChanged()
                binding.rclListSong.adapter?.notifyDataSetChanged()
                binding.rclAlbum.adapter?.notifyDataSetChanged()
                binding.rclBXH.adapter?.notifyDataSetChanged()
            }
        }
        asyn.execute(url)
    }

    override fun onClickList(position: Int) {
        (activity as MainActivity).fragment_playmusic(position, arrlistsong)
    }

    override fun getCount(): Int {
            return arrlistsong.size
        }

    override fun getData(position: Int): Item_song {
        return arrlistsong[position]
    }

    override fun onClickLove(position: Int) {
        (activity as MainActivity).fragment_albumlove(arrlove[position].link_music)
    }

    override fun getCountItemLove(): Int {
           return arrlove.size
    }

    override fun getDataLove(position: Int): Item_song {
        return arrlove[position]
    }

    override fun onClickAlbum(position: Int) {
        (activity as MainActivity).fragment_album(arralbum[position].link_music)
    }

    override fun getCountItemAlbum(): Int {
          return  arralbum.size
    }

    override fun getDataAlbum(position: Int): Item_song {
        return arralbum[position]
    }

    override fun onClickBXH(position: Int) {
        TODO("Not yet implemented")
    }

    override fun getCountBXH(): Int {
        return   arrbxh.size
    }

    override fun getDataBXH(position: Int): Item_BXH {
        return arrbxh[position]
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}