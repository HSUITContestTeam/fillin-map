package com.hsu.mapapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsu.mapapp.databinding.FragmentMapBinding
import com.hsu.mapapp.databinding.FragmentMapListBinding


// onCreateView나 onViewCreated view binding 쓰려면 맨아래
// MapFragment 클래스 참고!


//class ShareFragment : Fragment(R.layout.레이아웃이름)

//class SettingFragment : Fragment(R.layout.레이아웃이름)

class SettingFragment : Fragment(R.layout.activity_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}

class TestFragment : Fragment(R.layout.activity_test) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}

class MapListFragment : Fragment(R.layout.fragment_map_list) {
    private var _binding: FragmentMapListBinding? = null
    private lateinit var adapter: CustomAdapter
    private val datas = mutableListOf<MapItemList>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycler()
    }

    private fun setRecycler() {
        adapter = CustomAdapter(this)
        binding.recyclerView.adapter = adapter // RecyclerView와 CustomAdapter 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.setHasFixedSize(true)


        datas.apply {
            add(MapItemList("World"))
            add(MapItemList("Korea"))
            add(MapItemList("Hi"))

            adapter.datas = datas
            adapter.notifyDataSetChanged()
        }
    }
}

class MapFragment : Fragment(R.layout.fragment_map) {
    private var _binding: FragmentMapBinding? = null
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
        binding.fabMain.setOnClickListener {
            toggleFab()
        }

        // 플로팅 버튼 클릭 이벤트 - 캡처
        binding.fabCapture.setOnClickListener {
            Toast.makeText(this.context, "캡처 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }

        // 플로팅 버튼 클릭 이벤트 - 공유
        binding.fabShare.setOnClickListener {
            Toast.makeText(this.context, "공유 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFab() {
        Toast.makeText(this.context, "메인 버튼 클릭!", Toast.LENGTH_SHORT).show()
        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.fabShare, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabCapture, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 45f, 0f).apply { start() }
        } else { // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션
            ObjectAnimator.ofFloat(binding.fabShare, "translationY", -360f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabCapture, "translationY", -180f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION, 0f, 45f).apply { start() }
        }

        isFabOpen = !isFabOpen

    }
    
    // onDestoryView에서 binding을 null로 만들지 않으면 Fragment가 사라지지 않아서
    // 메모리 누수가 생긴다고 함. 그래서 _binding을 null로 만들어 줘야 한다~..
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}