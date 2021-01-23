package com.example.appmusicmp3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.databinding.LayoutItemAlbumBinding

class Adapter_recycle_album(val inter: IOnClickAlbum) :
    RecyclerView.Adapter<Adapter_recycle_album.ViewHolder>() {
    class ViewHolder(var binding: LayoutItemAlbumBinding, inter: IOnClickAlbum) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), inter
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.data = inter.getDataAlbum(position)
        holder.itemView.setOnClickListener{inter.onClickAlbum(holder.adapterPosition)}
    }

    override fun getItemCount() = inter.getCountItemAlbum()
    interface IOnClickAlbum {
        fun onClickAlbum(position: Int)
        fun getCountItemAlbum(): Int
        fun getDataAlbum(position: Int): Item_song
    }

}