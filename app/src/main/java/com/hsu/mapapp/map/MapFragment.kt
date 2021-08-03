package com.hsu.mapapp.map

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentMapBinding
import com.hsu.mapapp.utils.OnSwipeTouchListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MapFragment : Fragment(R.layout.fragment_map) {
    private var _binding: FragmentMapBinding? = null

    private var data = MutableLiveData<ArrayList<MapItemList>>()
    private lateinit var mapAdapter: MapAdapter
    private lateinit var mapViewModel : MapViewModel

    private var isFabOpen = false // Fab 버튼 default는 닫혀있음
    private var isPageOpen = false

    private val binding get() = _binding!!

    private var gangwondoFragment = Map_Gangwondo()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setSlidingAnimation() // 지도목록 스와이핑, 슬라이딩 설정
        setRecycler() // 리사이클러뷰 (지도목록 슬라이딩 화면) 설정
        setAddMapBtn() // 지도추가버튼, Dialog 띄움
        setFABClickEvent() // FAB 버튼 설정
    }

    // ----------------------슬라이딩 Layout-------------------------

    @SuppressLint("ClickableViewAccessibility")
    private fun setSlidingAnimation() {
        val leftAnimation =
            AnimationUtils.loadAnimation(this.context, R.anim.map_list_translate_left)
        val rightAnimation =
            AnimationUtils.loadAnimation(this.context, R.anim.map_list_translate_right)

        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                if (isPageOpen) {
                    binding.slidingList.setVisibility(View.INVISIBLE)
                    isPageOpen = false
                } else {
                    isPageOpen = true
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        }

        leftAnimation.setAnimationListener(animationListener)
        rightAnimation.setAnimationListener(animationListener)

        binding.mapPage.setOnTouchListener(object : OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeLeft() {
                // 슬라이딩 페이지 꺼내기
                if (!isPageOpen) { // 슬라이딩 리스트 닫기
                    binding.slidingList.setVisibility(View.VISIBLE)
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
    }

    // ----------------------지도 목록 recycler-------------------------

    private fun setRecycler() {
        binding.MapListRecyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.MapListRecyclerView.setHasFixedSize(true)

        val dataObserver: Observer<ArrayList<MapItemList>> =
            Observer { liveData ->
                data.value = liveData
                mapAdapter = MapAdapter(data)
                binding.MapListRecyclerView.adapter = mapAdapter // RecyclerView와 CustomAdapter 연결
                mapAdapter.notifyDataSetChanged()
                // 지도 목록에서 map 클릭하면 mapFragment 바뀜
                mapAdapter.setOnItemClickListener(object : MapAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, position: Int) {
                        Log.d("click",position.toString())
                        childFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView2,gangwondoFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    }
                })
                println("지도 추가")
            }

        mapViewModel.mapLiveData.observe(viewLifecycleOwner, dataObserver)

        setRecyclerDeco()
    }

    private fun setRecyclerDeco() {
        // 지도 목록 위아래 margin 설정
        val size = resources.getDimensionPixelSize(R.dimen.map_list_vertical_margin)
        val vertical_margin = MapListDeco(size)
        binding.MapListRecyclerView.addItemDecoration(vertical_margin)
    }

    private fun setAddMapBtn() {
        binding.addMapBtn.setOnClickListener {
            val fm = childFragmentManager
            AddMapDialogFragment().show(fm, "dialog")
        }

    }

    // ----------------------FAB button-------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setFABClickEvent() {

        requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        // 플로팅 버튼 클릭시 애니메이션 동작 기능
        binding.fabMain.setOnClickListener {
            toggleFab()
        }

        // 플로팅 버튼 클릭 이벤트 - 캡처
        binding.fabCapture.setOnClickListener {
            Toast.makeText(this.context, "캡처 버튼 클릭!", Toast.LENGTH_SHORT).show()

            // FAB 버튼 제외하고 화면 캡쳐
            val bitmap = getScreenShotFromView(binding.mapPage)

            if (bitmap != null) {
                saveMediaToStorage(bitmap)
            }
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

    private fun setFABVisiblity(visible: Boolean) {
        binding.fabCapture.isVisible = visible
        binding.fabShare.isVisible = visible
        binding.fabMain.isVisible = visible
    }

    // ----------------------뷰 캡쳐-------------------------

    private fun getScreenShotFromView(v: View) : Bitmap? {
        var screenshot: Bitmap? = null

        setFABVisiblity(false)

        try {
            screenshot = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "캡쳐실패 : " + e.message)
        }

        setFABVisiblity(true)

        return screenshot
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMediaToStorage(bitmap : Bitmap) {
        val date = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val todayDate = date.format(formatter)
        val filename = "${todayDate}_captureMap"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.requireActivity().contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this.context , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
        }

    }

    // ----------------------상단 액션바 hide-------------------------
    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }
    // --------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




