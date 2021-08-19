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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
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

    // 클릭된 이미지뷰 저장하는 해시맵
    private val ClickedIMGS: HashMap<String, ImageView> = hashMapOf<String, ImageView>()

    // 모든 이미지뷰 저장하는 해시맵
    private val AllIMGS: HashMap<String, ImageView> = hashMapOf<String, ImageView>()
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
        initialImageViewHashMap()
        uploadImageFromStorage()
        onClick()
        return binding.root
    }

    //-----------------------------AllIMGS 해시맵 초기화----------------------------------//
    private fun initialImageViewHashMap() {
        AllIMGS["Goseong"] = binding.Goseong
        AllIMGS["Haenam"] = binding.Haenam
        // 다른 지도도 추가
    }

    //-----------------------------map ImageView 서버에서 불러오기----------------------------------//
    private fun uploadImageFromStorage() {
        val uidRef = storage.reference.child("mapImageView/$uid")
        uidRef.listAll()
            .addOnSuccessListener(OnSuccessListener<ListResult> { result ->
                for (fileRef in result.items) {
                    uidRef.child("${fileRef.name}").downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Glide.with(this).asBitmap().load(it.result)
                                .into(object :
                                    BitmapImageViewTarget(AllIMGS["${fileRef.name}"]) {});
                        }
                    }
                }
            })
            .addOnFailureListener(OnFailureListener {})
    }

    //-----------------------------map ImageView 서버에 저장----------------------------------//
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongThread")
    override fun onResume() {
        super.onResume()
        val keySet = ClickedIMGS.keys
        for (name in keySet) {
            val imageView = ClickedIMGS[name]
            if (imageView != null) {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, baos)
                val data = baos.toByteArray()
                // FirebaseStorage
                val storageRef = storage.reference
                val bitmapRef = storageRef.child("mapImageView/$uid/$name")
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
            mapName = "Goseong"
            Log.d("$mapName", "click")
            // hashMap에 추가
            ClickedIMGS["$mapName"] = AllIMGS["$mapName"]!!
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
                        var srcBitmap = BitmapFactory.decodeFile(path)
                        // bitmap 사이즈 조절
                        //******* 추후 수정하겠음 - 서버에서 꺼내오는걸로
                        width =
                            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalWidth.toInt()
                        height =
                            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalHeight.toInt()
                        srcBitmap = Bitmap.createScaledBitmap(srcBitmap, width!!, height!!, true)
                        // 첫번째 방법 - 이미지뷰 이용
                        Log.d("mapName", mapName!!)
                        AllIMGS["$mapName"]?.setImageBitmap(convertToMap(srcBitmap)) // bitmap을 이미지뷰에 붙이기
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
        val haenamPathData =
            "M71.083,608.939l-5.167,-3.334l-2.167,6l-3.167,2.668v9.166l3.167,1.666v3.334l3.667,4.166l0.167,1.5l9.333,2.668l0.833,3.332l4.5,-2.5l4.167,4.168l1,3.5l0.5,7.166l-1.333,1.666l3.833,4.168l3.667,-2.668l-0.833,4.334c0,0 -2.833,1.334 -3,2.166c-0.167,0.834 -1.667,7 -1.667,7l4.333,-0.332l0.833,4.166l0.667,5.334l4.833,-4.168l6,0.834l-0.167,-6.334l2.333,-1.832l-1.667,-3.668l5.167,-3l6.167,-3.832l3.667,-1.168l1.333,-2.5l-1.333,-1l-4,-0.832l-0.333,-8.834l-1.5,-1.666l2.5,-3.834l-2.167,-5l3.333,-6l-1,-1.166l0.667,-3.834l-1.5,-3l-2.667,-1.5l-3.833,2.5l-10.167,-0.834l0.333,3.5l7,4l-5.167,0.834l-3.667,-3l-3.167,-0.5l-0.167,3.834l-5.833,-2.668l-5.5,-4l2.667,-4.5l-2.667,-0.332l-1,1.666l-3.167,-1.666l-1.667,0.332l0.167,3l-0.167,4.668l3.667,-0.834l2,2.834l4.833,3.5l-1.333,3.832l-7,-3.666l-4.167,-1l-1.833,-7.166l-2.167,-4.668l0.833,-3.166l-1.5,-1.166L71.083,608.939z"
        val haenamPath = PathParser.createPathFromPathData(haenamPathData)
        val goseongPathData =
            "M340.083,66.939l13.334,-7l-8.334,-17.5l-1.333,-4.333l-7.167,-10l1.334,-1.833l-8,-14.167V8.939l-6.5,-8.333l-5.334,2.167L317.75,16.94l-1.167,7.833l-10.166,12.833l2.833,2l2.5,0.833l0.833,0.167l-1,1.833l0.667,4.333l5.5,-1.5l1,3.667l0.833,5.5l4.167,0.333l1,3.833l3.667,1l3.833,-1.833l2.167,2.5v5.5L340.083,66.939z"
        // pathData를 이용해 path 생성
        val goseongPath = PathParser.createPathFromPathData(goseongPathData)
        //************ 클릭한 지도마다 path 다르게 해줘야 함
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
        val filePath =
            (context.applicationInfo.dataDir + File.separator + System.currentTimeMillis())
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


