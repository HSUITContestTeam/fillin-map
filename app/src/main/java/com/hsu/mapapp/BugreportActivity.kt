package com.hsu.mapapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BugreportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        setTitle("버그 신고")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bugreport)
    }
}