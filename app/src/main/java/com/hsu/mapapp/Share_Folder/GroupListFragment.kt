package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentGroupListBinding

class GroupListFragment : Fragment(R.layout.fragment_group_list_item) {
    private var _binding: FragmentGroupListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        GroupListBtnClickEvent()
    }

    private fun GroupListBtnClickEvent(){



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}