package com.hsu.mapapp.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hsu.mapapp.databinding.FragmentMapSeoulBinding
import com.richpath.RichPathView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class MapSeoulFragment : Fragment() {
    private var _binding: FragmentMapSeoulBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    //-----------------------------지도 클릭 이벤트 ----------------------------------//
    fun onClick() {
        val richPathView: RichPathView = binding.icMapOfSouthKorea
        // 고성 지역 클릭 이벤트
        richPathView.findRichPathByName("Goseong")?.setOnPathClickListener {
            Log.d("Goseong", "click")
            /*  갤러리 불러오기  */
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            filterActivityLauncher.launch(intent)
        }
    }

    //-----------------------------갤러리 이벤트 ----------------------------------//
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK && it.data != null) {
                /*  currentImageUri = 갤러리에서 고른 사진 Uri   */
                currentImageUri = it.data?.data // it.data == intent

                /*  사진을 bitmap으로 변환 */
                if (currentImageUri != null) {
                    try {
                        // 이미지 uri를 절대 경로로 바꾸기
                        var path = ""
                        currentImageUri?.let { it1 ->
                            path = createCopyAndReturnRealPath(it1).toString()
                        }
                        Log.d("path!", path)

//                              // canvas 그려질 customView
                        val customView = CustomView(activity?.applicationContext, path)
                        binding.root.addView(customView)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d("ActivityResult", "something wrong")
                }

            }
        }

    // 이미지 uri를 절대 경로로 바꾸기
    private fun createCopyAndReturnRealPath(uri: Uri): String? {
        val context = requireActivity().applicationContext
        val contentResolver = context.contentResolver ?: return null

        // Create file path inside app's data dir
        val filePath = (context.applicationInfo.dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d("file.getAbsolutePath()", file.getAbsolutePath())
        val path = file.getAbsolutePath()
        return path
    }
}