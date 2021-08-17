package com.hsu.mapapp.map

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.PathParser
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.hsu.mapapp.databinding.FragmentMapSeoulBinding
import com.richpath.RichPathView
import java.io.*


class MapSeoulFragment : Fragment() {
    private var _binding: FragmentMapSeoulBinding? = null
    private val binding get() = _binding!!

    private lateinit var richPathView: RichPathView
    private var mapName: String? = null // 선택된 지도 이름

    private var currentImageUri: Uri? = null
    private var colorResult: String? = null // 색 채우기
    private var selectedMap: String? = null

    private lateinit var storage: FirebaseStorage
    private val uid = Firebase.auth.currentUser?.uid
    private val IMGS: HashMap<String, ImageView> = hashMapOf<String, ImageView>()
    private var width: Int? = null
    private var height: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapSeoulBinding.inflate(inflater, container, false)
        // 서버에 저장해놓은 img를 imageview에 적용
        //******* 추후 수정하겠음 - 이미지가 저장된 mapName리스트 코드 추가, 코드 깔끔하게
        Log.d("uid",uid.toString())
        storage.reference.child("mapImageView/$uid/Goseong").downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                Glide.with(this)
                    .asBitmap()
                    .load(it.result)
                    .into(object : BitmapImageViewTarget(binding.Goseong) {});
            }
        }
        onClick()
        return binding.root

    }

    //-----------------------------map ImageView 서버에 저장----------------------------------//
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongThread")
    override fun onResume() {
        super.onResume()
        val keySet = IMGS.keys
        for (mapName in keySet) {
            val imageView = IMGS[mapName]
            if (imageView != null) {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, baos)
                val data = baos.toByteArray()
                // FirebaseStorage
                val storageRef = storage.reference
                val bitmapRef = storageRef.child("mapImageView/$uid/$mapName")
                val uploadTask: UploadTask = bitmapRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Log.d("uploadTask", "Faliure")
                }.addOnSuccessListener {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    Log.d("uploadTask", "Success")
                }

            }

        }
    }

    //-----------------------------지도 클릭 이벤트 ----------------------------------//
    fun onClick() {
        richPathView = binding.icMapOfSouthKorea

        val builder = AlertDialog.Builder(requireContext())
        val inflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // 지도 어떤걸로 채울지 선택하는 다이얼로그.
        // path 클릭했을 때 띄워줘야 함..
        /*builder.setTitle("지역 이름 써줘야됨")
            .setItems(R.array.fillMapItems,
            DialogInterface.OnClickListener { dialog, pos ->
                when (pos) {
                    //0 -> //이미지로 채우기
                    //1 -> // 색칠하기
                }
            })

        builder.show()*/

        // 고성 지역 클릭 이벤트
        richPathView.findRichPathByName("Goseong")?.setOnPathClickListener {
            Log.d("Goseong", "click")
            mapName = "Goseong"
            // hashMap에 추가
            IMGS["Goseong"] = binding.Goseong
            /*  갤러리 불러오기  */
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            filterActivityLauncher.launch(intent)
        }

        // 해남 지역 색 변경하기 이벤트
        richPathView.findRichPathByName("Haenam")?.setOnPathClickListener {
            selectedMap = "Haenam" // 선택한 지역을 해남 지역으로 변경.
            val haenamPathData = ""
            val intent = Intent(this.context, FillMapWithColorActivity::class.java)
            intent.putExtra("pathData", haenamPathData)
            fillColorActivityLancher.launch(intent)
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

                        var srcBitmap = BitmapFactory.decodeFile(path)
                        // bitmap 사이즈 조절
                        //******* 추후 수정하겠음 - 서버에서 꺼내오는걸로
                        width =
                            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalWidth.toInt()
                        height =
                            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalHeight.toInt()
                        srcBitmap = Bitmap.createScaledBitmap(srcBitmap, width!!, height!!, true)

                        // 첫번째 방법 - 이미지뷰 이용
                        binding.Goseong.setImageBitmap(convertToMap(srcBitmap)) // bitmap을 이미지뷰에 붙이기
                        Log.d("width", width.toString())
                        Log.d("height", height.toString())

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
        Log.d("file.getAbsolutePath()", file.absolutePath)
        val path = file.absolutePath
        return path
    }

    //-----------------------------색 변경 activiy lancher----------------------------------//
    private val fillColorActivityLancher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val colorResult = it.data?.getStringExtra("color")
                richPathView.findRichPathByName(selectedMap)?.fillColor =
                    Color.parseColor(colorResult.toString())
            }
        }

}