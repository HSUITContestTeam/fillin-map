package com.hsu.mapapp.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.hsu.mapapp.R

import com.hsu.mapapp.databinding.ActivityProfileBinding
import com.hsu.mapapp.login.CreateUserActivity
import com.hsu.mapapp.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileBinding : ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        SetProfileModifyBtnClickEvent()

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        profileBinding.profileLogoutBtn.setOnClickListener {
            signOut()
        }

    }
    private fun SetProfileModifyBtnClickEvent() {
        profileBinding.profileModifyBtn.setOnClickListener {
            startActivity(Intent(this, ProfileModifyActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    private fun signOut() { // 로그아웃
    }
}