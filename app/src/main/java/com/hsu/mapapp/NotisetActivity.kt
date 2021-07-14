package com.hsu.mapapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotisetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        setTitle("알림 설정")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notiset)
    }
}