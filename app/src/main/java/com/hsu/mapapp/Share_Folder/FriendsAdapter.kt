package com.hsu.mapapp.Share_Folder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FriendsListItemBinding


class FriendsAdapter(private val context:ShareFragment) :
    RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private var datas_friends = mutableListOf<FriendsItemList>()
    var isStartBtnSelected = false

    private var itemListener: FriendsAdapter.OnItemClickListener? = null

    fun setData(newData: MutableList<FriendsItemList>) {
        datas_friends.clear()
        datas_friends.addAll(newData)
        println("friendsListCount : ${datas_friends.size} ")

    }

    inner class ViewHolder (private val binding : FriendsListItemBinding) :
            RecyclerView.ViewHolder(binding.root){
                fun setFriendsName(item: FriendsItemList){
                    binding.FriendsName.text = item.FriendsName
                }
                fun SetFriendsImage(item:FriendsItemList){
                    Glide.with(context)
                    .load(item.photoUrl).circleCrop()
                    .error(R.drawable.login_id)
                    .into(binding.FriendsIMG)
                }
                fun SetFriendsMessage(item:FriendsItemList){
                    binding.FirendsMessage.text = item.Message
                }
                fun Message_btn_OnClick(){
                    binding.messageBtn.isSelected = isStartBtnSelected
                    isStartBtnSelected = !isStartBtnSelected
                }
                fun setOnClick(){
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
        val binding = FriendsListItemBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setFriendsName(datas_friends[position])
        holder.Message_btn_OnClick()
        holder.SetFriendsMessage(datas_friends[position])
        holder.SetFriendsImage(datas_friends[position])
        holder.setOnClick()
    }

    override fun getItemCount() = datas_friends.size

    interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }
    fun setOnItemClickListener(itemListener: FriendsAdapter.OnItemClickListener) {
        this.itemListener = itemListener
    }
}