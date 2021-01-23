package com.example.appmusicmp3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusicmp3.item.Item_song
import com.example.appmusicmp3.databinding.LayoutItemSongBinding

class Adapter_recycleList(val inter : IOnClickList) : RecyclerView.Adapter<Adapter_recycleList.ViewHolder>() {

    class  ViewHolder(var binding : LayoutItemSongBinding, inter: IOnClickList) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false),inter)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.data =  inter.getData(position)
        holder.itemView.setOnClickListener {
            inter.onClickList(holder.adapterPosition)
        }
    }
    override fun getItemCount() =  inter.getCount()
    interface IOnClickList {
        fun onClickList(position: Int)
        fun getCount() : Int
        fun getData(position: Int) : Item_song
    }
}