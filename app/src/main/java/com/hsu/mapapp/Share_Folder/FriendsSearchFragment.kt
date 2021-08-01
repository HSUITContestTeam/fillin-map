package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentSearchFriendsBinding

class FriendsSearchFragment : Fragment(R.layout.search_friends_list_item) {
    private var _binding: FragmentSearchFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ShareViewModel

    private lateinit var adapter: FriendsSearchAdapter
    private val datas_friends_search = mutableListOf<FriendsSearchItemList>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchFriendsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycler()
    }

   private fun setRecycler(){
       adapter = FriendsSearchAdapter(this)
       binding.FriendsSearchRecycler.adapter = adapter
       binding.FriendsSearchRecycler.layoutManager = LinearLayoutManager(this.context)
       binding.FriendsSearchRecycler.setHasFixedSize(true)

       datas_friends_search.apply{
           add(FriendsSearchItemList("Friends1"))
           add(FriendsSearchItemList("Firends2"))

           adapter.datas_friends_search = datas_friends_search
           adapter.notifyDataSetChanged()
       }
   }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}