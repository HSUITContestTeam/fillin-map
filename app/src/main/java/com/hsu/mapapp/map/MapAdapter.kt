package com.hsu.mapapp.map

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.MapListItemBinding

class MapAdapter(private val context: MapFragment) :
    RecyclerView.Adapter<MapAdapter.ViewHolder>() {
    var datas = mutableListOf<MapItemList>()
    var isStartBtnSelected = false

    inner class ViewHolder(private val binding: MapListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun setContent(item: MapItemList) {
                    binding.mapTitle.text = item.mapTitle
                }
                fun startOnclick() {
                    binding.startBtn.setOnClickListener {
                        binding.startBtn.isSelected = isStartBtnSelected
                        isStartBtnSelected = !isStartBtnSelected
                    }
                }
            }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = MapListItemBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContent(datas[position])
        viewHolder.startOnclick()
    }

    override fun getItemCount() = datas.size

}