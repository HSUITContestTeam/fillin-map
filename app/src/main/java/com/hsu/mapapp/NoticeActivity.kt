package com.hsu.mapapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NoticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        setTitle("공지사항")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
    }
}