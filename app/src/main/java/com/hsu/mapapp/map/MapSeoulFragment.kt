package com.hsu.mapapp.map

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
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
import java.util.*
import kotlin.collections.set

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
    private var sizeFlag:Int = 0
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
        onClick()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialImageViewHashMap()
        uploadColorFromStorage()
    }

    //-----------------------------AllIMGS 해시맵 초기화----------------------------------//
    private fun initialImageViewHashMap() {
        val mapOfKoreaRegions = resources.getStringArray(R.array.map_of_korea_regions)
        for (region in mapOfKoreaRegions) {
            val imageView = requireView().rootView.findViewWithTag<ImageView>(region)
            if (imageView != null) {
                AllIMGS[region] = imageView
            }
        }
    }

    //-----------------------------map ImageView 서버에서 불러오기----------------------------------//
    private fun uploadImageFromStorage() {
        val uidRef = storage.reference.child("mapImageView/$uid")
        uidRef.listAll()
            .addOnSuccessListener(OnSuccessListener<ListResult> { result ->
                for (fileRef in result.items) {
                    uidRef.child(fileRef.name).downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Glide.with(this).load(it.result)
                                .into(AllIMGS[fileRef.name]!!)
                        }
                    }
                }
            })
            .addOnFailureListener(OnFailureListener {})
    }

    private fun uploadColorFromStorage() {
        val uidRef = storage.reference.child("mapColor/$uid")
        uidRef.listAll()
            .addOnSuccessListener(OnSuccessListener<ListResult> { result ->
                for (fileRef in result.items) {
                    val localFile = File.createTempFile(fileRef.name, "txt")
                    Log.d("fileName", fileRef.name)
                    fileRef.getFile(localFile).addOnSuccessListener {
                        activity?.let {
                            val colorValue: String = localFile.readText()
                            Log.d("colorValue", colorValue)
                            localFile.delete()
                            richPathView.findRichPathByName(fileRef.name)?.fillColor =
                                Color.parseColor(colorValue)
                        }
                    }.addOnFailureListener { }
                }
            })
            .addOnFailureListener(OnFailureListener {})
    }

    //-----------------------------map ImageView 서버에 저장----------------------------------//
    @SuppressLint("WrongThread")
    override fun onResume() {
        super.onResume()
        imageUploadToFirebase()
        setALLIMGSsize() // 이미지뷰 사이즈 초기화
        uploadImageFromStorage()
    }
    private fun setALLIMGSsize(){
        sizeFlag = 1
        val mapOfKoreaRegions = resources.getStringArray(R.array.map_of_korea_regions)
        for (region in mapOfKoreaRegions) {
            val imageView = requireView().rootView.findViewWithTag<ImageView>(region)
            if (imageView != null) {
                width =
                    binding.icMapOfSouthKorea.findRichPathByName(region)!!.originalWidth.toInt()
                height =
                    binding.icMapOfSouthKorea.findRichPathByName(region)!!.originalHeight.toInt()
                AllIMGS[region]!!.layoutParams.width = width as Int
                AllIMGS[region]!!.layoutParams.height = height as Int

            }
        }
    }
    private fun imageUploadToFirebase() {
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
            richPathView.findRichPathByName("$region")
                ?.setOnPathClickListener { mapName = "$region" }
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

                        2 -> { // 삭제하기
                            val uidRef = storage.reference.child("mapImageView/$uid")
                            uidRef.child("$mapName").delete().addOnSuccessListener {
                                Log.d("image delete", "success")
                                //Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show()
                                AllIMGS["$mapName"]?.isVisible = false
                                ClickedIMGS.remove("$mapName")
                            }.addOnFailureListener {
                                richPathView.findRichPathByName(mapName.toString())?.fillColor =
                                    Color.parseColor("#d2d2d2")
                            }

                            val uidColorRef = storage.reference.child("mapColor/$uid")
                            uidColorRef.child("$mapName").delete().addOnSuccessListener {
                                AllIMGS["$mapName"]?.isVisible = false
                                ClickedIMGS.remove("$mapName")
                            }.addOnFailureListener {
                                richPathView.findRichPathByName(mapName.toString())?.fillColor =
                                    Color.parseColor("#d2d2d2")
                            }
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
                        var imagePath = ""
                        currentImageUri?.let { it1 ->
                            imagePath = createCopyAndReturnRealPath(it1).toString()
                        }
                        // 이미지뷰 회전 체크
                        var exif: ExifInterface? = null
                        try {
                            exif = ExifInterface(imagePath)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        val orientation = exif!!.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )
                        var srcBitmap = BitmapFactory.decodeFile(imagePath)
                        srcBitmap = rotateBitmap(srcBitmap,orientation)
                        // bitmap 사이즈 조절
                        width =
                            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalWidth.toInt()
                        height =
                            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalHeight.toInt()
                        srcBitmap = Bitmap.createScaledBitmap(srcBitmap,width!!,height!!,true)
                        // bitmap을 이미지뷰에 붙이기
                        AllIMGS["$mapName"]?.setImageBitmap(convertToMap(srcBitmap))
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
                deleteImageFromMap() // 이미지 삭제
                val colorResult = it.data?.getStringExtra("color")
                richPathView.findRichPathByName(mapName.toString())?.fillColor =
                    Color.parseColor(colorResult.toString())
                /*var uriColor: Uri? = null
                val storageReference = storage.getReference("mapColor/$uid/$mapName")
                val fo = FileWriter("$mapName",false)
                fo.write("$colorResult")
                fo.close()
                val result =
                    StreamResult(File(Environment.getExternalStorageDirectory(), "$mapName"))
                var output: Writer? = null
                val path = "$mapName"
                val file = File(path)
                output = BufferedWriter(FileWriter(file))
                output.write("${colorResult.toString()}")
                output.close()
                uriColor = Uri.fromFile(file)
                storageReference.putFile(uriColor).addOnSuccessListener {
                    Toast.makeText(this.context, "Successfully uploaded", Toast.LENGTH_SHORT).show()
                }*/

            }
        }

    private fun deleteImageFromMap() {
        /* 이미지로 채워져 있으면 firebase storage에서 이미지 삭제 */
        val uidRef = storage.reference.child("mapImageView/$uid")
        uidRef.child("$mapName").delete().addOnSuccessListener {
            Log.d("image delete", "success")
            AllIMGS["$mapName"]?.isVisible = false
            ClickedIMGS.remove("$mapName")
        }.addOnFailureListener {
            Log.d("image delete", "fail")
        }
    }
    //-----------------------------이미지뷰 회전 관련 함수----------------------------------//
    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale((-1).toFloat(), 1F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180F)
                matrix.postScale((-1).toFloat(), 1F)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90F)
                matrix.postScale((-1).toFloat(), 1F)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate((-90).toFloat())
                matrix.postScale((-1).toFloat(), 1F)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate((-90).toFloat())
            else -> return bitmap
        }
        return try {
            val bmRotated =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }
}


