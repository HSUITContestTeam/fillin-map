package com.hsu.mapapp.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsu.mapapp.databinding.FragmentMapGangwondoBinding


class MapGangwondoFragment : Fragment() {
    private var _binding: FragmentMapGangwondoBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapGangwondoBinding.inflate(inflater, container, false)
        return binding.root
    }
}