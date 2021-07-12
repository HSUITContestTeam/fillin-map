package com.hsu.mapapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.FragmentMapListBinding
import com.hsu.mapapp.databinding.MapListItemBinding

class CustomAdapter(private val context: MapListFragment) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    var datas = mutableListOf<MapItemList>()

    inner class ViewHolder(private val binding: MapListItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun setContent(item: MapItemList) {
                    binding.mapTitle.text = item.mapTitle
                }
            }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = MapListItemBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setContent(datas[position])
    }

    override fun getItemCount() = datas.size

}