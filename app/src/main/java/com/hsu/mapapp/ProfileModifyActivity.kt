package com.hsu.mapapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hsu.mapapp.databinding.ActivityProfileModifyBinding


class ProfileModifyActivity : AppCompatActivity() {
    private lateinit var profileModifyBinding : ActivityProfileModifyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileModifyBinding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(profileModifyBinding.root)
    }
}