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
import com.bumptech.glide.Glide
import com.example.appmusicmp3.adapter.Adapter_recycle_list_Album
import com.example.appmusicmp3.databinding.LayoutListalbumBinding
import com.example.appmusicmp3.item.Item_detail_album
import com.example.appmusicmp3.item.Item_song
import org.jsoup.Jsoup

class Fragment_ListAlbum  : Fragment() , Adapter_recycle_list_Album.IOnClickItem{
    private  lateinit var  binding : LayoutListalbumBinding
    private  var arrListItemAlbum =  mutableListOf<Item_song>()
    private   var  urlImage : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arrListItemAlbum.clear()
        loadDataAlbum(arguments?.getString("url"))
        loadRclView(arguments?.getString("url"))
        binding  = LayoutListalbumBinding.inflate(inflater,container, false)
        val adapter =  Adapter_recycle_list_Album(this)
        binding.rclListAlbum.layoutManager =  LinearLayoutManager(binding.rclListAlbum.context)
        binding.rclListAlbum.adapter =  adapter
        return binding.root
    }

    private fun loadDataAlbum(url: String?) {
        val arr =  mutableListOf<String>()
        val asyn = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<String, Void, Item_detail_album>() {
            override fun doInBackground(vararg params: String?): Item_detail_album {
                val linkCrawl = params[0]
                val doc = Jsoup.connect(linkCrawl).get()
                val d = doc.select("div.col-md-4").select("div.card-details")
                val linkImage = d.select("div#companion_cover").select("img").attr("src")
                val nameSong = d.select("div.card-body").select("h2.card-title").text()
                Log.d("data","size :: ${ d.select("div.card-body").select("ul.list-unstyled").size}")
                for( i in d.select("div.card-body").select("ul.list-unstyled").select("li")){
                    arr.add(i.select("a").text())
                }
                return Item_detail_album(linkImage,nameSong,arr[0],arr[1])
            }
            override fun onPostExecute(result: Item_detail_album?) {
                Glide.with(binding.imageAlbum.context).load(result?.linkImage).into(binding.imageAlbum)
                urlImage = result?.linkImage
                binding.txtnameSong.text = result?.name
                binding.txtsingerName.text = result?.singerName
                binding.txtCreater.text = result?.creatorName
                binding.notifyChange()
            }
        }
        asyn.execute(url)
    }
    private  fun loadRclView(url : String?){
        @SuppressLint("StaticFieldLeak")
        val asyn = object  : AsyncTask<String,Void,MutableList<Item_song>>(){
            override fun doInBackground(vararg params: String?): MutableList<Item_song> {
                val linkCrawl  =  params[0]
                val doc  =  Jsoup.connect(linkCrawl).get()
                var arr =  mutableListOf<Item_song>()
                for( d in doc.select("div.card-footer")){
                    val nameSong  =  d.select("div.name").select("a").text()
                    val singerName  =  d.select("div.author").select("div.author-ellepsis").select("a").text()
                    val linkMusic =   d.select("div.name").select("a").attr("href")
                    Log.d("huy","link music + $linkMusic")
                    arr.add(Item_song(urlImage,nameSong,singerName,linkMusic))
                }
                return arr
            }
            override fun onPostExecute(result: MutableList<Item_song>) {
                arrListItemAlbum.addAll(result)
                binding.rclListAlbum.adapter?.notifyDataSetChanged()
            }
        }
        asyn.execute(url)
    }

    override fun onclickListAlbum(pos: Int) {
        (activity as MainActivity).fragment_playmusic(pos,arrListItemAlbum)
    }

    override fun getDataListAlbum(pos: Int): Item_song {
        return  arrListItemAlbum[pos]
    }

    override fun getCountListAlbum(): Int {
       return arrListItemAlbum.size
    }

}