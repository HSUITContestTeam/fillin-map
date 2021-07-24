package com.hsu.mapapp.Share_Folder

import android.graphics.Insets.add
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentGroupListBinding
import com.hsu.mapapp.databinding.GroupListListItemBinding

class GroupListFragment : Fragment(R.layout.group_list_list_item) {
    private var _binding: GroupListListItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GroupListAdapter
    private val data_groups = mutableListOf<GroupListItemList>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupListListItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycler()
    }

    private fun setRecycler(){
        adapter = GroupListAdapter(this)


        data_groups.apply{
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