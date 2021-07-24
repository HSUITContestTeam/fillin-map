package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentFriendsBinding

class FriendsFragment :Fragment(R.layout.fragment_friends){

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendsAdapter
    private val data_friends = mutableListOf<FriendsItemList>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycler()
    }

    private fun setRecycler(){
        adapter = FriendsAdapter(this)
        binding.friendsRecycler.adapter = adapter
        binding.friendsRecycler.layoutManager = LinearLayoutManager(this.context)
        binding.friendsRecycler.setHasFixedSize(true)

        data_friends.apply{
            add(FriendsItemList(("Friends1")))
            add(FriendsItemList(("Friends2")))

            adapter.datas_friends = data_friends
            adapter.notifyDataSetChanged()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}