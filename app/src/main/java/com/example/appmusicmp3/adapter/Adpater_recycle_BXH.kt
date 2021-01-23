package com.example.appmusicmp3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusicmp3.item.Item_BXH
import com.example.appmusicmp3.databinding.LayoutItemBxhBinding

class Adpater_recycle_BXH(val inter: IOnClickBXH) :
    RecyclerView.Adapter<Adpater_recycle_BXH.ViewHolder>() {
    class ViewHolder(var binding: LayoutItemBxhBinding, inter: IOnClickBXH) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemBxhBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), inter
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.data = inter.getDataBXH(position)
    }

    override fun getItemCount() = inter.getCountBXH()
    interface IOnClickBXH {
        fun onClickBXH(position: Int)
        fun getCountBXH(): Int
        fun getDataBXH(position: Int): Item_BXH
    }
}