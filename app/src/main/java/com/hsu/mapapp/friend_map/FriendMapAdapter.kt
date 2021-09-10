package com.hsu.mapapp.friend_map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.MapListItemBinding
import com.hsu.mapapp.map.MapItemList
import java.io.*


class FriendMapAdapter(private var data: LiveData<ArrayList<MapItemList>>) :
    RecyclerView.Adapter<FriendMapAdapter.ViewHolder>() {
    private lateinit var binding: MapListItemBinding
    private var isStartBtnSelected = false
    private var itemListener: OnItemClickListener? = null
    var longPos = -1 // 롱클릭 position

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        binding = MapListItemBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        data.value!![position].let { item ->
            binding.mapTitle.text = item.mapTitle
            binding.previewImage.setImageURI(item.previewImage.toUri())
        }
        viewHolder.setOnclick()
    }

    override fun getItemCount(): Int {
        return data.value!!.size
    }

    inner class ViewHolder(private val binding: MapListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setOnclick() {

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

    fun setOnItemClickListener(itemListener: OnItemClickListener) {
        this.itemListener = itemListener
    }

}