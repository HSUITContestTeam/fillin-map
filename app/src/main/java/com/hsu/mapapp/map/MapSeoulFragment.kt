package com.hsu.mapapp.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsu.mapapp.databinding.FragmentMapSeoulBinding
import com.richpath.RichPathView


class MapSeoulFragment : Fragment() {
    private var _binding: FragmentMapSeoulBinding? = null

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
        _binding = FragmentMapSeoulBinding.inflate(inflater, container, false)
        onClick()
        return binding.root
    }

    fun onClick() {
        val view: RichPathView = binding.icMapOfSouthKorea
        view.findRichPathByName("Goseong")?.setOnPathClickListener {
            Log.d("Goseong", "click")
        }

    }
}