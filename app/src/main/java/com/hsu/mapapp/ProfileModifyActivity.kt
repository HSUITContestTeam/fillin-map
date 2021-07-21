package com.hsu.mapapp

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.view.View


import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hsu.mapapp.databinding.ActivityProfileModifyBinding
import java.lang.Exception




class ProfileModifyActivity : AppCompatActivity() {
    private lateinit var profileModifyBinding: ActivityProfileModifyBinding

    //  Register a request to start an activity for result, designated by the given contract.
    //  원래 onActivityResult 에서 데이터(인텐트 결과)를 가져오듯이 로직을 구성
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK && it.data !=null) {
                /*  currentImageUri = 갤러리에서 고른 사진 Uri   */
                var currentImageUri:Uri ? = it.data?.data       // it.data == intent
                if (currentImageUri != null) { }
                try {
                    currentImageUri?.let {
                        if(Build.VERSION.SDK_INT < 28) {        // Build 버전이 28 미만일 때
                            /*  사진 Uri를 bitmap으로 변환 */
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                currentImageUri
                            )
                            /*  내부저장소에서 선택한 사진을 profilemodifyProfileIV(이미지뷰)에 표시    */
                            profileModifyBinding.profilemodifyProfileIV.setImageBitmap(bitmap)
                        }
                        else {                                // Build 버전이 28 이상일 때
                            /*  사진 Uri를 bitmap으로 변환 */
                            val source = ImageDecoder.createSource(this.contentResolver, currentImageUri)
                            val bitmap = ImageDecoder.decodeBitmap(source)
                            /*  내부저장소에서 선택한 사진을 profilemodifyProfileIV(이미지뷰)에 표시    */
                            profileModifyBinding.profilemodifyProfileIV.setImageBitmap(bitmap)
                        }
                    }


                }catch(e:Exception){
                    e.printStackTrace()
                }
            } else {
                Log.d("ActivityResult","something wrong")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileModifyBinding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(profileModifyBinding.root)

        //  프로필 화면 클릭 시
        profileModifyBinding.profilemodifyProfileIV?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                /*  내부저장소에서 image파일들만 불러오는 인텐트  */
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                filterActivityLauncher.launch(intent)
            }

        })
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
        private const val PERMISSION_READ_EXTERNAL_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val PERMISSION_ACCESS_MEDIA_LOCATION = android.Manifest.permission.ACCESS_MEDIA_LOCATION

        private val PERMISSIONS_REQUESTED: Array<String> = arrayOf(
            PERMISSION_CAMERA,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
            PERMISSION_ACCESS_MEDIA_LOCATION
        )
    }

}