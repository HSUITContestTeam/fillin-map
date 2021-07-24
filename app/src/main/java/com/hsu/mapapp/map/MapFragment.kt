package com.hsu.mapapp.map

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentMapBinding

class MapFragment : Fragment(R.layout.fragment_map) {
    private var _binding: FragmentMapBinding? = null

    private lateinit var adapter: MapAdapter
    private val datas = mutableListOf<MapItemList>()

    private var isFabOpen = false // Fab 버튼 default는 닫혀있음
    private var isPageOpen = false

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
        setRecycler()
        setAddMapBtn()
        setFABClickEvent()
        setSlidingAnimation()

    }

    private fun setRecycler() {
        adapter = MapAdapter(this)
        binding.recyclerView.adapter = adapter // RecyclerView와 CustomAdapter 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.setHasFixedSize(true)


        datas.apply {
            add(MapItemList("Map1"))
            add(MapItemList("Map2"))
            add(MapItemList("Map3"))

            adapter.datas = datas
            adapter.notifyDataSetChanged()
        }

    }

    private fun setAddMapBtn() {
        binding.addMapBtn.setOnClickListener {
            val fm = childFragmentManager
            MapNameDialogFragment().show(fm, "dialog")
        }
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

    @SuppressLint("ResourceAsColor")
    private fun setSlidingAnimation() {
        val leftAnimation = AnimationUtils.loadAnimation(this.context, R.anim.map_list_translate_left)
        val rightAnimation = AnimationUtils.loadAnimation(this.context, R.anim.map_list_translate_right)

        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) { }

            override fun onAnimationEnd(animation: Animation?) {
                if(isPageOpen) {
                    binding.slidingList.setVisibility(View.INVISIBLE)
                    binding.slideBtn.text = "open"
                    isPageOpen = false
                }
                else {
                    binding.slideBtn.text = "close"
                    isPageOpen = true
                }
            }

            override fun onAnimationRepeat(animation: Animation?) { }
        }

        leftAnimation.setAnimationListener(animationListener)
        rightAnimation.setAnimationListener(animationListener)

        binding.slideBtn.setOnClickListener {
            if(isPageOpen) { // 슬라이딩 리스트 닫기
                binding.slidingList.startAnimation(rightAnimation)
            }
            else { // 슬라이딩 리스트 열기
                binding.slidingList.setVisibility(View.VISIBLE)
                binding.slidingList.startAnimation(leftAnimation)

            }
        }

    }

    // onDestoryView에서 binding을 null로 만들지 않으면 Fragment가 사라지지 않아서
    // 메모리 누수가 생긴다고 함. 그래서 _binding을 null로 만들어 줘야 한다~..
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




