package com.hsu.mapapp.map

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.PathParser
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.UploadTask
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentMapSeoulBinding
import com.richpath.RichPathView
import java.io.*


class MapSeoulFragment : Fragment() {
    private var _binding: FragmentMapSeoulBinding? = null
    private val binding get() = _binding!!

    private lateinit var richPathView: RichPathView
    private var mapName: String? = null // 선택된 지도 이름
    private var pathData: String? = null

    private var currentImageUri: Uri? = null
    private var colorResult: String? = null // 색 채우기

    private lateinit var storage: FirebaseStorage
    private val uid = Firebase.auth.currentUser?.uid

    // 클릭된 이미지뷰 저장하는 해시맵
    private var ClickedIMGS: HashMap<String, ImageView> = hashMapOf<String, ImageView>()

    // 모든 이미지뷰 저장하는 해시맵
    private var AllIMGS: HashMap<String, ImageView> = hashMapOf<String, ImageView>()
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
        AllIMGS["gangwondoGoseong"] = binding.gangwondoGoseong
        AllIMGS["haenam"] = binding.haenam
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
    @SuppressLint("WrongThread")
    override fun onResume() {
        super.onResume()
        val keySet = ClickedIMGS.keys
        for (name in keySet) {
            val imageView = ClickedIMGS[name]
            if (imageView != null && imageView.drawable is BitmapDrawable) {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, baos)
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                }
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

        val mapOfKoreaRegions = resources.getStringArray(R.array.map_of_korea_regions)

        for (region in mapOfKoreaRegions) {
            richPathView.findRichPathByName("$region")?.setOnPathClickListener { mapName = "$region" }
        }

        richPathView.setOnPathClickListener {
            pathOnClicked()
        }


    }

    private fun pathOnClicked() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        builder
            .setTitle(resources.getIdentifier("$mapName", "string", "com.hsu.mapapp"))
            .setItems(
                R.array.fillMapItems,
                DialogInterface.OnClickListener { dialog, pos ->
                    when (pos) {
                        0 -> { //이미지로 채우기
                            getPathDataFromFirebase()
                            Log.d("$mapName", "click")
                            // hashMap에 추가
                            ClickedIMGS["$mapName"] = AllIMGS["$mapName"]!!
                            //  갤러리 불러오기
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            intent.type = "image/*"
                            intent.data =
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            filterActivityLauncher.launch(intent)
                        }
                        1 -> { // 색칠하기
                            val intent =
                                Intent(this.context, FillMapWithColorActivity::class.java)
                            fillColorActivityLancher.launch(intent)
                        }
                    }
                })
        builder.show()
    }

    // 서버에서 pathData불러오기
    private fun getPathDataFromFirebase() {
        val firestore = FirebaseFirestore.getInstance()
        val docRef = firestore.collection("pathData").document("$mapName")
        docRef.get()
            .addOnSuccessListener { document ->
                pathData = document.get("pathData").toString()
                println("$mapName path: $pathData")
            }
            .addOnFailureListener { exception ->
                Log.d("getMapPath()", "get failed with ", exception)
            }
    }

    //-----------------------------갤러리 이벤트 ----------------------------------//
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK && it.data != null) {
                /* 색칠된 path 흰색으로 변경 */
                richPathView.findRichPathByName(mapName.toString())?.fillColor = Color.WHITE
                AllIMGS["$mapName"]?.isVisible = true

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
                        ClickedIMGS["$mapName"]!!.layoutParams.width = width as Int
                        ClickedIMGS["$mapName"]!!.layoutParams.height = height as Int
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
        // pathData를 이용해 path 생성
        val pathValue = PathParser.createPathFromPathData(pathData)
        //************ 클릭한 지도마다 path 다르게 해줘야 함
        return resizePath(
            pathValue,
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
                richPathView.findRichPathByName(mapName.toString())?.fillColor =
                    Color.parseColor(colorResult.toString())

                /*val firestore = FirebaseFirestore.getInstance()
                firestore?.collection("pathColor").document("$uid").set(color)*/

                /* 이미지로 채워져 있으면 firebase storage에서 이미지 삭제 */
                val uidRef = storage.reference.child("mapImageView/$uid")
                uidRef.child("$mapName").delete().addOnSuccessListener{
                    Log.d("image delete", "success")
                    AllIMGS["$mapName"]?.isVisible = false
                    ClickedIMGS.remove("$mapName")
                }.addOnFailureListener {
                    Log.d("image delete", "fail")
                }
            }
        }

}


