package com.hsu.mapapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsu.mapapp.databinding.ActivityShareBinding

class ShareFragment : Fragment(R.layout.activity_share) {


    private var _binding: ActivityShareBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val binding_v = ActivityShareBinding.bind(view)
        //binding_v.
        ShareBtnClickEvent()
    }

    private fun ShareBtnClickEvent(){


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

