package com.hsu.mapapp.map

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.MapListItemBinding
import java.io.*


class MapAdapter(private val context: MapFragment) :
    RecyclerView.Adapter<MapAdapter.ViewHolder>() {
    private lateinit var binding: MapListItemBinding
    private var isStartBtnSelected = false
    private var itemListener: OnItemClickListener? = null
    var longPos = -1 // 롱클릭 position
    private var mapItemList = mutableListOf<MapItemList>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListData(newMapItemList: MutableList<MapItemList>) {
        this.mapItemList = newMapItemList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        binding = MapListItemBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val mapItem: MapItemList = mapItemList[position]
        viewHolder.mapTitle.text = mapItem.mapTitle
        viewHolder.previewImage.setImageURI(mapItem.previewImage.toUri())
        viewHolder.setOnclick()
    }

    override fun getItemCount(): Int {
        return mapItemList.size
    }

    inner class ViewHolder(private val binding: MapListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var mapTitle = binding.mapTitle
        val previewImage = binding.previewImage

        fun setOnclick() {
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

            itemView.setOnLongClickListener {
                longPos = layoutPosition
                return@setOnLongClickListener false
            }

            // 지도편집 floating menu
            itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->
                menu.setHeaderTitle("지도 편집")
                menu.add(layoutPosition, 0, 0, "제목 변경하기")
                menu.add(layoutPosition, 1, 1, "지도 삭제하기")
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }

}