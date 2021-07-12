package com.hsu.mapapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hsu.mapapp.databinding.ActivityLoginBinding
import com.hsu.mapapp.databinding.ActivityMapBinding
import com.hsu.mapapp.databinding.ActivityTestBinding
import com.hsu.mapapp.databinding.FragmentMapBinding


// onCreateView나 onViewCreated view binding 쓰려면 맨아래
// MapFragment 클래스 참고!


//class ShareFragment : Fragment(R.layout.레이아웃이름)

//class SettingFragment : Fragment(R.layout.레이아웃이름)

class TestFragment : Fragment(R.layout.activity_test) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}

class MapFragment : Fragment(R.layout.fragment_map) {
    private lateinit var _binding: FragmentMapBinding
    private var isFabOpen = false // Fab 버튼 default는 닫혀있음

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setFABClickEvent()
    }

    private fun setFABClickEvent() {
        // 플로팅 버튼 클릭시 애니메이션 동작 기능
        _binding.fabMain.setOnClickListener {
            toggleFab()
        }

        // 플로팅 버튼 클릭 이벤트 - 캡처
        _binding.fabCapture.setOnClickListener {
            Toast.makeText(this.context, "캡처 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }

        // 플로팅 버튼 클릭 이벤트 - 공유
        _binding.fabShare.setOnClickListener {
            Toast.makeText(this.context, "공유 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFab() {
        Toast.makeText(this.context, "메인 버튼 클릭!", Toast.LENGTH_SHORT).show()
        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            ObjectAnimator.ofFloat(_binding.fabShare, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(_binding.fabCapture, "translationY", 0f).apply { start() }
            _binding.fabMain.setImageResource(R.drawable.fab_dots)
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(_binding.fabShare, "translationY", -400f).apply { start() }
            ObjectAnimator.ofFloat(_binding.fabCapture, "translationY", -200f).apply { start() }
            _binding.fabMain.setImageResource(R.drawable.fab_up)
        }

        isFabOpen = !isFabOpen

    }
}

class LoginFragment : Fragment(R.layout.activity_login) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ActivityLoginBinding.bind(view)
    }
}