package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentFriendsBinding

class FriendsFragment :Fragment(R.layout.fragment_friends){

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        friendsBtnClickEvent()
    }

    private fun friendsBtnClickEvent(){



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}