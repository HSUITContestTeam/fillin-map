package com.hsu.mapapp.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hsu.mapapp.R

import com.hsu.mapapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileBinding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        SetProfileModifyBtnClickEvent()

    }
    private fun SetProfileModifyBtnClickEvent() {
        profileBinding.profileModifyBtn.setOnClickListener {
            startActivity(Intent(this, ProfileModifyActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }
}