package com.hsu.mapapp.friend_map

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentFriendMapBinding
import com.hsu.mapapp.map.MapGangwondoFragment
import com.hsu.mapapp.map.MapIdViewModel
import com.hsu.mapapp.map.MapItemList
import com.hsu.mapapp.map.MapSeoulFragment
import com.hsu.mapapp.utils.OnSwipeTouchListener
import com.richpath.RichPathView
import java.util.*

class ShareMapActivity : AppCompatActivity() {
    private lateinit var binding: FragmentFriendMapBinding

    private var data = MutableLiveData<ArrayList<MapItemList>>()
    private lateinit var mapAdapter: FriendMapAdapter
    private lateinit var mapViewModel: FriendMapViewModel
    private lateinit var mapIdViewModel: MapIdViewModel
    private lateinit var friendMapViewFactory: FriendMapViewFactory
    private var isPageOpen = false

    private lateinit var gangwondoFragment : MapGangwondoFragment
    private lateinit var seoulFragment : MapSeoulFragment

    private var selectedMapId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFriendMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val message: String = intent.getStringExtra("friendUid") ?: ""
        friendMapViewFactory = FriendMapViewFactory(message)
        mapViewModel = ViewModelProvider(this,friendMapViewFactory).get(FriendMapViewModel::class.java)
        Log.d("onCreateView","실행")
        mapViewModel = ViewModelProvider(this).get(FriendMapViewModel::class.java)
        mapIdViewModel = ViewModelProvider(this).get(MapIdViewModel::class.java)

        setSlidingAnimation() // 지도목록 스와이핑, 슬라이딩 설정
        setRecycler() // 리사이클러뷰 (지도목록 슬라이딩 화면) 설정
    }
// ----------------------슬라이딩 Layout 애니메이션-------------------------

    @SuppressLint("ClickableViewAccessibility")
    private fun setSlidingAnimation() {
        val leftAnimation =
            AnimationUtils.loadAnimation(this, R.anim.map_list_translate_left)
        val rightAnimation =
            AnimationUtils.loadAnimation(this, R.anim.map_list_translate_right)

        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (isPageOpen) {
                    binding.slidingList.visibility = View.INVISIBLE
                    isPageOpen = false
                } else {
                    isPageOpen = true
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        }

        leftAnimation.setAnimationListener(animationListener)
        rightAnimation.setAnimationListener(animationListener)

        binding.mapPage.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {
            override fun onSwipeLeft() {
                // 슬라이딩 페이지 꺼내기
                if (!isPageOpen) {
                    binding.slidingList.visibility = View.VISIBLE
                    binding.slidingList.startAnimation(leftAnimation)
                }
            }

            override fun onSwipeRight() {
                // 슬라이딩 페이지 닫기
                if (isPageOpen) { // 슬라이딩 리스트 닫기
                    binding.slidingList.startAnimation(rightAnimation)
                }
            }
        })

        // 일단 버튼으로 열고닫기 하도록.. 해놨음 ㅜ
        binding.button2.setOnClickListener {
            // 슬라이딩 페이지 꺼내기
            if (!isPageOpen) {
                binding.slidingList.visibility = View.VISIBLE
                binding.slidingList.startAnimation(leftAnimation)
                binding.button2.text = "close"
            } else { // 슬라이딩 페이지 닫기
                binding.slidingList.startAnimation(rightAnimation)
                binding.button2.text = "open"
            }
        }
    }

    // ----------------------지도 목록 recycler setting-------------------------

    @SuppressLint("NotifyDataSetChanged")
    private fun setRecycler() {
        Log.d("setRecycler()","실행")

        binding.MapListRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.MapListRecyclerView.setHasFixedSize(true)

        val dataObserver: Observer<ArrayList<MapItemList>> =
            Observer { liveData ->
                data.value = liveData
                mapAdapter = FriendMapAdapter(data)
                binding.MapListRecyclerView.adapter = mapAdapter // RecyclerView와 CustomAdapter 연결
                mapAdapter.notifyDataSetChanged()

                /*// 처음은 첫번째 index의 지도로 교체
                if (mapViewModel.mapLiveData.value?.size != 0) {
                    selectedMapId = mapViewModel.fetch().value?.get(0)?.mapId.toString()
                    mapIdViewModel.setMapId(selectedMapId)
                    fragmentTransaction(mapViewModel.fetch().value?.get(0)?.mapSort.toString())
                }*/

                // 지도 목록에서 map 클릭하면 mapFragment 바뀜
                mapAdapter.setOnItemClickListener(object : FriendMapAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, position: Int) {
                        Log.d("Mapclick", position.toString())

                        // 선택한 mapID 하위 프래그먼트들과 공유
                        selectedMapId = mapViewModel.mapLiveData.value?.get(position)?.mapId.toString()
                        mapIdViewModel.setMapId(selectedMapId)
                        fragmentTransaction(mapViewModel.mapLiveData.value?.get(position)?.mapSort.toString())
                    }
                })
            }
        mapViewModel.mapLiveData.observe(this, dataObserver)
    }

    // 프래그먼트 교체
    fun fragmentTransaction(mapSort: String) {
        seoulFragment = MapSeoulFragment()
        gangwondoFragment = MapGangwondoFragment()

        when(mapSort) {
            // 대한민국지도
            "대한민국" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView4, seoulFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitNow()
            }
            // 강원도지도
            "강원도" -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView4, gangwondoFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitNow()
            }
        }
    }


}