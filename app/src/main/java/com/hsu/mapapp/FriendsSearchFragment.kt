package com.hsu.mapapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsu.mapapp.databinding.FragmentFriendsBinding
import com.hsu.mapapp.databinding.FragmentSearchFriendsBinding

class FriendsSearchFragment : Fragment(R.layout.fragment_search_friends) {
    private var _binding: FragmentSearchFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        friendsSearchBtnClickEvent()
    }

    private fun friendsSearchBtnClickEvent(){



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}