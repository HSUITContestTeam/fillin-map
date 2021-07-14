package com.hsu.mapapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ThemesetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        setTitle("테마 설정")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themeset)
    }
}