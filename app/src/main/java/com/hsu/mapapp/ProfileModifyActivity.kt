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

    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK && it.data !=null) {
                var currentImageUri:Uri ? = it.data?.data
                try {
                    currentImageUri?.let {
                        if(Build.VERSION.SDK_INT < 28) {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                currentImageUri
                            )
                            profileModifyBinding.profilemodifyProfileIV.setImageBitmap(bitmap)
                        } else {
                            val source = ImageDecoder.createSource(this.contentResolver, currentImageUri)
                            val bitmap = ImageDecoder.decodeBitmap(source)
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

        profileModifyBinding.profilemodifyProfileIV?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("image/*")
                filterActivityLauncher.launch(intent)
            }

        })
    }
    // 권한 요청
    private fun requestPermissions() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Log.d("권한요청", "$it")
        }.launch(PERMISSIONS_REQUESTED)
    }

    // 권한 종류들
    companion object {
        private const val PERMISSION_CAMERA = android.Manifest.permission.CAMERA
        private const val PERMISSION_READ_EXTERNAL_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        private val PERMISSIONS_REQUESTED: Array<String> = arrayOf(
            PERMISSION_CAMERA,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
        )
    }

}