package com.hsu.mapapp.profile


import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.databinding.ActivityProfileModifyBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class ProfileModifyActivity : AppCompatActivity() {
    private lateinit var profileModifyBinding: ActivityProfileModifyBinding

    private var currentImageUri: Uri? = null
    private var path: String = ""

    private val user = Firebase.auth.currentUser

    // Firebase Auth
//    private lateinit var auth: FirebaseAuth
//    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileModifyBinding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(profileModifyBinding.root)

        //  프로필 화면 클릭 시
        profileModifyBinding.profilemodifyProfileIV.setOnClickListener {
            /*  내부저장소에서 image파일들만 불러오는 인텐트  */
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra("crop", true)
            filterActivityLauncher.launch(intent)
        }

    }

    //-----------------------------프로필 사진 설정----------------------------------//

    private fun cropImage(uri: Uri?) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            //사각형 모양으로 자른다
            .start(this)

    }

    //  Register a request to start an activity for result, designated by the given contract.
    //  원래 onActivityResult 에서 데이터(인텐트 결과)를 가져오듯이 로직을 구성
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                /*  currentImageUri = 갤러리에서 고른 사진 Uri   */
                currentImageUri = it.data?.data // it.data == intent
                cropImage(currentImageUri)

                /*  사진을 bitmap으로 변환 후 imageView에 표시 */
                if (currentImageUri != null) {
                    try {
                        currentImageUri.let {
                            if (Build.VERSION.SDK_INT < 28) {        // Build 버전이 28 미만일 때
                                /*  사진 Uri를 bitmap으로 변환 */
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    this.contentResolver,
                                    currentImageUri
                                )
                                /*  내부저장소에서 선택한 사진을 profilemodifyProfileIV(이미지뷰)에 표시    */
                                profileModifyBinding.profilemodifyProfileIV.setImageBitmap(bitmap)
                            } else {                                // Build 버전이 28 이상일 때
                                /*  사진 Uri를 bitmap으로 변환 */
                                val source =
                                    ImageDecoder.createSource(
                                        this.contentResolver,
                                        currentImageUri!!
                                    )
                                val bitmap = ImageDecoder.decodeBitmap(source)
                                /*  내부저장소에서 선택한 사진을 profilemodifyProfileIV(이미지뷰)에 표시    */
                                profileModifyBinding.profilemodifyProfileIV.setImageBitmap(bitmap)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d("ActivityResult", "something wrong")
                }
                // 이미지 uri를 절대 경로로 바꾸기
                currentImageUri?.let { it1 ->
                    createCopyAndReturnRealPath(it1)
                }
            }
        }

    //  권한 요청
    private fun requestPermissions() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Log.d("권한요청", "$it")
        }.launch(PERMISSIONS_REQUESTED)
    }

    //  권한 정의
    companion object {
        /* 권한 종류: CAMERA,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,ACCESS_MEDIA_LOCATION    */
        private const val PERMISSION_CAMERA = android.Manifest.permission.CAMERA
        private const val PERMISSION_READ_EXTERNAL_STORAGE =
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE =
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val PERMISSION_ACCESS_MEDIA_LOCATION =
            android.Manifest.permission.ACCESS_MEDIA_LOCATION

        private val PERMISSIONS_REQUESTED: Array<String> = arrayOf(
            PERMISSION_CAMERA,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
            PERMISSION_ACCESS_MEDIA_LOCATION
        )
    }

    // 이미지 uri를 절대 경로로 바꾸기
    private fun createCopyAndReturnRealPath(uri: Uri): String? {
        val context = applicationContext
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
        path = file.getAbsolutePath()
        /*  절대 경로를 getGps()에 넘겨주기   */
        getGps(path)

        return path
    }

    // 이미지 gps 구하기
    private fun getGps(photoPath: String) {
        var valid: Boolean = false
        var latitude: Float
        var longitude: Float

        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(photoPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val lat = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        // TAG_GPS_LATITUDE_REF: Indicates whether the latitude is north or south latitude
        val lat_ref = exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val lon = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        // TAG_GPS_LONGITUDE_REF: Indicates whether the longitude is east or west longitude.
        val lon_ref = exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
        if ((lat != null) && (lat_ref != null) && (lon != null)
            && (lon_ref != null)
        ) {
            valid = true
        }
        Log.d("latitude", lat.toString())
        Log.d("longtitude", lon.toString())

    }

}