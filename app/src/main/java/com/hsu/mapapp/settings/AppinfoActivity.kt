package com.hsu.mapapp.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hsu.mapapp.R

class AppinfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        setTitle("앱 정보")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appinfo)
    } 
}