package com.example.appmusicmp3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusicmp3.databinding.LayoutItemListAlbumBinding
import com.example.appmusicmp3.item.Item_song

class Adapter_recycle_list_Album(var inter: IOnClickItem) :
    RecyclerView.Adapter<Adapter_recycle_list_Album.ViewHolder>() {

    class ViewHolder(var binding: LayoutItemListAlbumBinding, inter: IOnClickItem) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutItemListAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), inter
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.data = inter.getDataListAlbum(position)
        holder.itemView.setOnClickListener{inter.onclickListAlbum(holder.adapterPosition)}
    }

    override fun getItemCount() = inter.getCountListAlbum()
    interface IOnClickItem {
        fun onclickListAlbum(pos: Int)
        fun getDataListAlbum(pos: Int): Item_song
        fun getCountListAlbum(): Int
    }
}