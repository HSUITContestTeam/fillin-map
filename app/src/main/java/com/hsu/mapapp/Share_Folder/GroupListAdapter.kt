package com.hsu.mapapp.Share_Folder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.FriendsListItemBinding
import com.hsu.mapapp.databinding.GroupListListItemBinding


class GroupListAdapter(private val context: GroupListFragment) :
    RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    var datas_groups = mutableListOf<GroupListItemList>()
    var isStartBtnSelected = false

    inner class ViewHolder(private val binding: GroupListListItemBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun setGroupName(item: GroupListItemList){
                    binding.GroupName.text = item.GroupName
                }
                fun Message_group_btn_OnClick(){
                    binding.MessageBtnGroup.isSelected = isStartBtnSelected
                    isStartBtnSelected = !isStartBtnSelected
                }
            }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupListAdapter.ViewHolder {
       val layoutInflater = LayoutInflater.from(parent.context)
        val binding =GroupListListItemBinding.inflate(layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupListAdapter.ViewHolder, position: Int) {
        holder.setGroupName(datas_groups[position])
        holder.Message_group_btn_OnClick()
    }

    override fun getItemCount() = datas_groups.size


}