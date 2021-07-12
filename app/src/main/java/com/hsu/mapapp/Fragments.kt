package com.hsu.mapapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hsu.mapapp.databinding.ActivityLoginBinding
import com.hsu.mapapp.databinding.ActivityMapBinding
import com.hsu.mapapp.databinding.ActivityTestBinding

//class ShareFragment : Fragment(R.layout.레이아웃이름)

//class SettingFragment : Fragment(R.layout.레이아웃이름)

class TestFragment : Fragment(R.layout.activity_test) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ActivityTestBinding.bind(view)
        binding.textView.text = "테스트"
    }
}

class MapFragment : Fragment(R.layout.activity_map) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ActivityMapBinding.bind(view)
    }
}

class LoginFragment : Fragment(R.layout.activity_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ActivityLoginBinding.bind(view)
    }
}