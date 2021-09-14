package com.hsu.mapapp.map

import LoadingDialog
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.PathParser
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.UploadTask
import com.hsu.mapapp.R
import com.hsu.mapapp.camera.GpsTracker
import com.hsu.mapapp.databinding.FragmentMapSeoulBinding
import com.richpath.RichPathView
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    private lateinit var mapIdViewModel: MapIdViewModel
    private var selectedMapId: String = ""

    val REQUEST_TAKE_PHOTO = 1
    var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()
        mapIdViewModel = ViewModelProvider(requireActivity()).get(MapIdViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapSeoulBinding.inflate(inflater, container, false)
        if(parentFragmentManager.findFragmentById(R.id.fragmentContainerView4)?.id == null)
            onClick()
        mapIdViewModel.mapId.observe(viewLifecycleOwner, androidx.lifecycle.Observer<String> { selectedMapId = it })
        Log.d("selectedMapId",selectedMapId)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("WrongThread")
    override fun onResume() {
        super.onResume()
        if (AllIMGS.isEmpty())
            initialImageViewHashMap()
        setALLIMGSsize() // 이미지뷰 사이즈 초기화
        imageWithFirebase() // 이미지뷰 서버에 업로드 및 가져오기
        uploadColorFromStorage()
        LoadingDialog.hideLoading() // 로딩 애니메이션 종료
        requireActivity().findViewById<FloatingActionButton>(R.id.fab_share).setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    //-----------------------------AllIMGS 해시맵 초기화----------------------------------//
    private fun initialImageViewHashMap() {
        Log.d("initialImageViewHashMap()", "실행")
        val mapOfKoreaRegions = resources.getStringArray(R.array.map_of_korea_regions)
        for (region in mapOfKoreaRegions) {
            val imageView = requireView().rootView.findViewWithTag<ImageView>(region)
            if (imageView != null) {
                AllIMGS[region] = imageView
            }
        }
    }

    //-----------------------------map color Firebase로부터 가져오기----------------------------------//
    private fun uploadColorFromStorage() {
        richPathView = binding.icMapOfSouthKorea
        val mapOfKoreaRegions = resources.getStringArray(R.array.map_of_korea_regions)
        for (region in mapOfKoreaRegions) {
            richPathView.findRichPathByName("$region")
                ?.setOnPathClickListener { mapName = "$region" }
        }

        val uidRef = storage.reference.child("mapColor/$selectedMapId")
        uidRef.listAll()
            .addOnSuccessListener(OnSuccessListener<ListResult> { result ->
                for (fileRef in result.items) {
                    val localFile = File.createTempFile(fileRef.name, "txt")
                    fileRef.getFile(localFile).addOnSuccessListener {
                        activity?.let {
                            val colorValue: String = localFile.readText()
                            localFile.delete()
                            richPathView.findRichPathByName(fileRef.name)?.fillColor =
                                    Color.parseColor(colorValue)

                        }
                    }.addOnFailureListener { }
                }
            })
            .addOnFailureListener(OnFailureListener {})
    }

    //-----------------------------이미지뷰 사이즈 초기화화---------------------------------//
    private fun setALLIMGSsize() {
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

    //-----------------------------이미지 업로드/가져오기 with Firebase---------------------------------//
    private fun imageWithFirebase() {
        if (ClickedIMGS.isNotEmpty()) { // 클릭한 이미지가 있는 경우 이미지뷰를 서버에 저장 후 표시
            Log.d("imageUploadToFirebase()", "실행")
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
                    val bitmapRef = storageRef.child("mapImageView/$selectedMapId/$name")
                    val uploadTask: UploadTask = bitmapRef.putBytes(data)
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                        Log.d("uploadTask", "Faliure")
                    }.addOnSuccessListener {
                        //선택한 지역에 색이 저장되어 있으면 삭제
                        deleteColorFromMap()
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Log.d("uploadTask", "Success")
                        // 파이어베이스에 클릭한 이미지 저장 후 불러오기
                        showImageViewFromStorage()
                    }
                }
            }
        } else {
            showImageViewFromStorage()
        }
    }

    //-----------------------------map ImageView Firebase에서 불러오기----------------------------------//
    private fun showImageViewFromStorage() {
        Log.d("showImageViewFromStorage()", "실행")
        val uidRef = storage.reference.child("mapImageView/$selectedMapId")
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

    //-----------------------------지도 클릭 이벤트 ----------------------------------//
    fun onClick() {
        richPathView = binding.icMapOfSouthKorea

        val mapOfKoreaRegions = resources.getStringArray(R.array.map_of_korea_regions)
        for (region in mapOfKoreaRegions) {
            richPathView.findRichPathByName(region)
                ?.setOnPathClickListener { mapName = region }
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
                            val uidRef = storage.reference.child("mapImageView/$selectedMapId")
                            uidRef.child("$mapName").delete().addOnSuccessListener {
                                Log.d("image delete", "success")
                                //Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show()
                                AllIMGS["$mapName"]?.isVisible = false
                                ClickedIMGS.remove("$mapName")
                            }.addOnFailureListener {
                                richPathView.findRichPathByName(mapName.toString())?.fillColor =
                                    Color.parseColor("#ffffff")
                            }

                            val uidColorRef = storage.reference.child("mapColor/$selectedMapId")
                            uidColorRef.child("$mapName").delete().addOnSuccessListener {
                                AllIMGS["$mapName"]?.isVisible = false
                                ClickedIMGS.remove("$mapName")
                            }.addOnFailureListener {
                                richPathView.findRichPathByName(mapName.toString())?.fillColor =
                                    Color.parseColor("#ffffff")
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
                        currentImageUri?.let { it1 ->
                            val imagePath = createCopyAndReturnRealPath(it1)
                            makeImageViewByPath(imagePath!!)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d("ActivityResult", "something wrong")
                }

            }
        }
    private fun makeImageViewByPath(imagePath: String) {
        Log.d("imagePath",imagePath)
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
        srcBitmap = rotateBitmap(srcBitmap, orientation)
        // bitmap 사이즈 조절
        width =
            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalWidth.toInt()
        height =
            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalHeight.toInt()
        srcBitmap = Bitmap.createScaledBitmap(srcBitmap, width!!, height!!, true)
        // bitmap을 이미지뷰에 붙이기
        Log.d("setImageBitmap", "실행")
        ClickedIMGS["$mapName"]!!.setImageBitmap(convertToMap(srcBitmap))
        ClickedIMGS["$mapName"]!!.layoutParams.width = width as Int
        ClickedIMGS["$mapName"]!!.layoutParams.height = height as Int
        Log.d("mapName is ", "$mapName")
        Log.d("width", width.toString())
        Log.d("height", height.toString())
    }
    // https://github.com/tarek360/Bitmap-Cropping 참고
    private fun convertToMap(src: Bitmap): Bitmap {
        Log.d("convertToMap","실행")
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
        return file.absolutePath
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
    // 이미지 gps 구하기
//    private fun getGps(photoPath: String) {
//        val exif = androidx.exifinterface.media.ExifInterface(photoPath)
//        val lat = GPS(exif).latitude.toDouble()
//        val lng = GPS(exif).longitude.toDouble()
//        Log.d("latitude", lat.toString())
//        Log.d("longtitude", lng.toString())
//        val g = Geocoder(context)
//        var address: List<Address>? = null
//        try {
//            address = g.getFromLocation(lat, lng, 10)
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Log.d("test", "입출력오류")
//        }
//        if (address != null) {
//            if (address.isEmpty()) {
//                Log.d("test", "주소찾기 오류")
//            } else {
//                Log.d("찾은 주소", address[0].getAddressLine(0).toString())
//            }
//        }
//    }

    //-----------------------------색 변경 activiy lancher----------------------------------//
    private val fillColorActivityLancher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {

                val colorResult = it.data?.getStringExtra("color")
                richPathView.findRichPathByName(mapName.toString())?.fillColor =
                    Color.parseColor(colorResult.toString())
                var uriColor: Uri? = null
                val storageReference = storage.getReference("mapColor/$selectedMapId/$mapName")

                //textfile 생성 후 업로드
                var output = getContext()?.openFileOutput("$mapName",Context.MODE_PRIVATE)
                var dos = DataOutputStream(output)
                dos.writeBytes("${colorResult.toString()}")
                dos.flush()
                dos.close()
                var path = context?.getFileStreamPath("$mapName").toString()
                var file = File(path)
                uriColor = Uri.fromFile(file)
                storageReference.putFile(uriColor).addOnSuccessListener {
                    deleteImageFromMap() // 이미지 삭제
                    Toast.makeText(this.context, "Successfully uploaded", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun deleteImageFromMap() {
        /* 이미지로 채워져 있으면 firebase storage에서 이미지 삭제 */
        val uidRef = storage.reference.child("mapImageView/$selectedMapId")
        uidRef.child("$mapName").delete().addOnSuccessListener {
            Log.d("image delete", "success")
            AllIMGS["$mapName"]?.isVisible = false
            ClickedIMGS.remove("$mapName")
        }.addOnFailureListener {
            Log.d("image delete", "fail")
        }
    }

    private fun deleteColorFromMap() {
        val uidColorRef = storage.reference.child("mapColor/$selectedMapId")
        uidColorRef.child("$mapName").delete().addOnSuccessListener {
            AllIMGS["$mapName"]?.isVisible = false
            ClickedIMGS.remove("$mapName")
        }
    }
    //-----------------------------카메라 실행----------------------------------//
    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                createImageFile().let {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.hsu.mapapp",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    this.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                    file = it
                }
            }
        }
    }

    //-----------------------------카메라 실행 결과 받기----------------------------------//
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> { // 카메라 앱
                    file.let {
                        mapName="seoul"
                        ClickedIMGS["$mapName"] = binding.seoul
                        pathData = "M158.71,145.146l3.923,2.53 2.084,-1.1h2.82v6.375l3.678,5.884v4.045l-2.207,1.839 1.594,2.574 3.555,-1.961h3.678l1.226,3.923 -3.555,3.065v2.084l0.736,3.187 -3.923,4.658h-3.31l-4.291,6.375 -2.574,-0.858 -1.348,-4.291 -5.762,-0.613 -2.942,4.168h-2.7l-2.7,-2.452 -3.065,2.452 -3.432,-8.7 -2.207,1.471 -3.555,1.226 -1.226,-3.432 1.839,-2.084 -1.839,-5.026 -6.129,-2.207 5.026,-7.478 4.291,3.923 3.31,-0.613 2.942,-2.207 2.084,-1.1v-6.13h2.574l2.7,-3.555 0.981,2.82 1.326,1.061 1.126,0.9 1.594,-1.226v-6.007z"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                Uri.fromFile(it)
                            )
                            ImageDecoder.decodeBitmap(source).let {
                                saveMediaToStorage(it)
                            }
                        } else {
                            MediaStore.Images.Media.getBitmap(
                                requireActivity().contentResolver,
                                Uri.fromFile(it)
                            ).let {
                                saveMediaToStorage(it)
                            }
                        }
                        val imagePath = file!!.absolutePath
                        var srcBitmap = BitmapFactory.decodeFile(imagePath)
                        // bitmap 사이즈 조절
                        width =
                            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalWidth.toInt()
                        height =
                            binding.icMapOfSouthKorea.findRichPathByName("$mapName")!!.originalHeight.toInt()
                        srcBitmap = Bitmap.createScaledBitmap(srcBitmap, width!!, height!!, true)
                        // bitmap을 이미지뷰에 붙이기
                        Log.d("setImageBitmap", ClickedIMGS["seoul"].toString())
                        ClickedIMGS["seoul"]!!.setImageBitmap(convertToMap(srcBitmap))
                        ClickedIMGS["seoul"]!!.layoutParams.width = width as Int
                        ClickedIMGS["seoul"]!!.layoutParams.height = height as Int
                        Log.d("mapName is ", "$mapName")
                        Log.d("width", width.toString())
                        Log.d("height", height.toString())
                    }
                }
            }
        }
    }

    //-----------------------------카메라로 찍은 사진 파일로 만들기----------------------------------//
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filename = "JPEG_${timeStamp}"
        return File(storageDir, filename)
    }

    //-----------------------------갤러리에 사진 저장----------------------------------//
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMediaToStorage(bitmap: Bitmap) {
        val date = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val todayDate = date.format(formatter)
        val filename = "JPEG_${todayDate}"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.requireActivity().contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                var contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                contentValues = ContentValues().apply {
                    put(
                        MediaStore.MediaColumns.MIME_TYPE,
                        getMimeType(imageUri.toString())
                    )
                }
                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(this.context, "현재 위치 "+getCurrentGPS(), Toast.LENGTH_SHORT)
                .show()
        }
    }

    // url = file path or whatever suitable URL you want.
    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
    //-----------------------------현재 위치 구하기----------------------------------//
    private fun getCurrentGPS(): String {
        val gpsTracker = GpsTracker(requireActivity())

        val latitude: Double = gpsTracker.getLatitude()
        val longitude: Double = gpsTracker.getLongitude()
        val address: String = getCurrentAddress(latitude, longitude)
        val string = address.split(" ")

//        Toast.makeText(requireActivity(), "현재위치 \n위도 $latitude\n경도 $longitude", Toast.LENGTH_SHORT)
//            .show()
        return string[1]+" "+string[2]
    }

    fun getCurrentAddress(latitude: Double, longitude: Double): String {
        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address> = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(requireContext(), "지오코더 서비스 사용불가", Toast.LENGTH_SHORT).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(requireContext(), "잘못된 GPS 좌표", Toast.LENGTH_SHORT).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses.isEmpty()) {
            Toast.makeText(requireContext(), "주소 미발견", Toast.LENGTH_SHORT).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        return address.getAddressLine(0).toString() + "\n"
    }
    override fun onDestroy() {
        super.onDestroy()
        binding.root.removeAllViewsInLayout()
    }
}
