package com.example.appmusicmp3

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.appmusicmp3.item.Item_song

class MainActivity : AppCompatActivity(){
    private var check : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragment_chosse_opption_music()
        if(intent?.getParcelableArrayListExtra<Item_song>("Arr")  != null){
            fragment_playmusic(  intent.getIntExtra("Pos",0) , intent.getParcelableArrayListExtra<Item_song>("Arr") as MutableList<Item_song> )
        }
    }
    fun fragment_chosse_opption_music(){
        val tran =  supportFragmentManager.beginTransaction()
        val frag  =  Fragment_chosse_option_music()
        tran.add(R.id.fragmentmain ,frag)
        tran.addToBackStack(null).commit()
    }
      fun fragmentmanin(){
        val tran  =  supportFragmentManager.beginTransaction()
        val frag =  FragmentMainOnline()
        tran.replace(R.id.fragmentmain , frag)
        tran.addToBackStack(null).commit()
    }
    fun fragmentMainOffline(){
        val tran  =  supportFragmentManager.beginTransaction()
        val frag =  FragmentMainOffline()
        tran.replace(R.id.fragmentmain , frag)
        tran.addToBackStack(null).commit()
    }
      fun fragmnet_list_song(){
          check = true
        val tran  =  supportFragmentManager.beginTransaction()
        val frag =  Fragmnet_list_song()
         tran.replace(R.id.fragmentmain , frag)
        tran.addToBackStack(null).commit()
    }
    fun fragment_playmusic( position: Int,  arr: MutableList<Item_song>){
           val tran  = supportFragmentManager.beginTransaction()
           val frag =  Fragment_playmusic(arr)
           val bundle =  Bundle()
           bundle.putInt("pos",position)
           frag.arguments = bundle
           tran.replace(R.id.fragmentmain, frag).addToBackStack(null).commit()
    }
    fun fragment_albumlove(url : String?){
        val tran = supportFragmentManager.beginTransaction()
        val frag  =  Fragment_ListAlbumLove()
        val bunble  =  Bundle()
        bunble.putString("url",url)
        frag.arguments = bunble
        tran.replace(R.id.fragmentmain , frag).addToBackStack(null).commit()
    }
    fun fragment_album(url : String?){
        val tran = supportFragmentManager.beginTransaction()
        val frag  =  Fragment_ListAlbum()
        val bunble  =  Bundle()
        bunble.putString("url",url)
        frag.arguments = bunble
        tran.replace(R.id.fragmentmain , frag).addToBackStack(null).commit()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {

        super.onDestroy()
    }
}