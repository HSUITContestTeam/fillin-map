package com.hsu.mapapp.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hsu.mapapp.R

class BugreportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        setTitle("버그 신고")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bugreport)
    }
}