package com.hsu.mapapp.Share_Folder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.FriendsListItemBinding


class FriendsAdapter(private val context: FriendsFragment) :
    RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    var datas_friends = mutableListOf<FriendsItemList>()
    var isStartBtnSelected = false

    inner class ViewHolder (private val binding : FriendsListItemBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun setFriendsName(item: FriendsItemList){
                    binding.FriendsName.text = item.FriendsName
                }
                fun Message_btn_OnClick(){
                    binding.messageBtn.isSelected = isStartBtnSelected
                    isStartBtnSelected = !isStartBtnSelected
                }
            }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = FriendsListItemBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setFriendsName(datas_friends[position])
        holder.Message_btn_OnClick()
    }

    override fun getItemCount() = datas_friends.size


}