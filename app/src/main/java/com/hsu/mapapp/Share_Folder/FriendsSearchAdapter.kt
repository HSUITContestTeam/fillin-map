package com.hsu.mapapp.Share_Folder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hsu.mapapp.databinding.FriendsListItemBinding
import com.hsu.mapapp.databinding.SearchFriendsListItemBinding

class FriendsSearchAdapter(private val context: FriendsSearchFragment) :
    RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder>() {
        var datas_friends_search = mutableListOf<FriendsSearchItemList>()
        var isStartBtnSelected = false

    inner class ViewHolder(private val binding: SearchFriendsListItemBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun setFriendsName(item: FriendsSearchItemList){
                    binding.friendsSearchName.text = item.FriendsSearchName
                }
                fun Add_Friends_btn_OnClich(){
                    binding.addFriendsBtn.isSelected = isStartBtnSelected
                    isStartBtnSelected = !isStartBtnSelected
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchFriendsListItemBinding.inflate(layoutInflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setFriendsName(datas_friends_search[position])
        holder.Add_Friends_btn_OnClich()
    }

    override fun getItemCount() = datas_friends_search.size
}