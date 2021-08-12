package com.hsu.mapapp.map

import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.PathParser
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

                        // bitmap을 이미지뷰에 붙이기
                        // https://github.com/tarek360/Bitmap-Cropping 참고
                        val srcBitmap = BitmapFactory.decodeFile(path)
                        binding.mapGoseong.setImageBitmap(convertToMap(srcBitmap))

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d("ActivityResult", "something wrong")
                }

            }
        }
    // https://github.com/tarek360/Bitmap-Cropping 참고
    private fun convertToMap(src: Bitmap): Bitmap {
        return BitmapUtils.getCroppedBitmap(src, getMapPath(src))
    }

    private fun getMapPath(src: Bitmap): Path {
        val goseongPathData =
            "M340.083,66.939l13.334,-7l-8.334,-17.5l-1.333,-4.333l-7.167,-10l1.334,-1.833l-8,-14.167V8.939l-6.5,-8.333l-5.334,2.167L317.75,16.94l-1.167,7.833l-10.166,12.833l2.833,2l2.5,0.833l0.833,0.167l-1,1.833l0.667,4.333l5.5,-1.5l1,3.667l0.833,5.5l4.167,0.333l1,3.833l3.667,1l3.833,-1.833l2.167,2.5v5.5L340.083,66.939z"
        // pathData를 이용해 path 생성
        val goseongPath = PathParser.createPathFromPathData(goseongPathData)
        return resizePath(
            goseongPath,
            src.width.toFloat(), src.height.toFloat()
        )
    }

    fun resizePath(path: Path?, width: Float, height: Float): Path {
        val bounds = RectF(0F, 0F, width, height)
        val resizedPath = Path(path)
        val src = RectF()
        resizedPath.computeBounds(src, true)
        val resizeMatrix = Matrix()
        resizeMatrix.setRectToRect(src, bounds, Matrix.ScaleToFit.CENTER)
        resizedPath.transform(resizeMatrix)
        return resizedPath
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