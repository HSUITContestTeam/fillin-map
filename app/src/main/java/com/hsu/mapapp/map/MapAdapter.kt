package com.hsu.mapapp.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.MapListItemBinding

class MapAdapter(private var data: LiveData<ArrayList<MapItemList>>) :
    RecyclerView.Adapter<MapAdapter.ViewHolder>() {
    private lateinit var binding: MapListItemBinding
    private var isStartBtnSelected = false
    private var itemListener: OnItemClickListener? = null

    public interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }

    public fun setOnItemClickListener(itemListener: OnItemClickListener) {
        this.itemListener = itemListener
    }

    inner class ViewHolder(private val binding: MapListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun startOnclick() {
            binding.startBtn.setOnClickListener {
                binding.startBtn.isSelected = isStartBtnSelected
                isStartBtnSelected = !isStartBtnSelected
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemListener?.onItemClick(it, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        binding = MapListItemBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        data.value!!.get(position).let { item ->
            binding.mapTitle.text = item.mapTitle
        }
        viewHolder.startOnclick()
    }

    override fun getItemCount(): Int {
        val size = data.value!!.size
        return size
    }

}