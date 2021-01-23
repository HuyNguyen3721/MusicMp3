package com.example.appmusicmp3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.databinding.LayoutItemAlbumBinding

class Adapter_recycle_love(val inter: IOnClickLove) :
    RecyclerView.Adapter<Adapter_recycle_love.ViewHolder>() {
    class ViewHolder(var binding: LayoutItemAlbumBinding, inter: IOnClickLove) :
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
        holder.binding.data = inter.getDataLove(position)
        holder.itemView.setOnClickListener{ inter.onClickLove(holder.adapterPosition)}
    }
    override fun getItemCount() = inter.getCountItemLove()
    interface IOnClickLove {
        fun onClickLove(position: Int)
        fun getCountItemLove(): Int
        fun getDataLove(position: Int): Item_song
    }
}