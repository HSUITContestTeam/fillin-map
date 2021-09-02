package com.hsu.mapapp.map

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.MapListItemBinding
import android.provider.MediaStore

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import android.os.ParcelFileDescriptor
import android.util.Base64
import com.bumptech.glide.Glide
import java.io.*


class MapAdapter(private val context: MapFragment) :
    RecyclerView.Adapter<MapAdapter.ViewHolder>() {
    private lateinit var binding: MapListItemBinding
    private var isStartBtnSelected = false
    private var itemListener: OnItemClickListener? = null
    var longPos = -1
    private var mapItemList = mutableListOf<MapItemList>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListData(data: MutableList<MapItemList>) {
        mapItemList.clear()
        mapItemList.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        binding = MapListItemBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val mapItem: MapItemList = mapItemList[position]
        viewHolder.mapTitle.text = mapItem.mapTitle
        Log.d("viewHolder", "mapTitle: ${mapItem.mapTitle}")
        viewHolder.previewImage.setImageURI(mapItem.previewImage.toUri())
        viewHolder.startOnclick()
    }

    override fun getItemCount(): Int {
        return mapItemList.size
    }

    inner class ViewHolder(private val binding: MapListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var mapTitle = binding.mapTitle
        val previewImage = binding.previewImage

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