package com.hsu.mapapp.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

        profileBinding.profileLogoutBtn.setOnClickListener {
            signOut()
        }

        profileBinding.profileDeleteAccountBtn.setOnClickListener {
            revokeAccess()
        }

        //---------------------사용자 정보 load----------------------//

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        // 구글 로그인 정보
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        //----------------------------------------------------------//

        setProfile()

    }

    private fun setProfile() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            user?.let {
                val name = user.displayName
                val email = user.email
                val photoUrl = user.photoUrl
                val emailVerified = user.isEmailVerified
                val uid = user.uid

                profileBinding.profileNameTV.text = name
                profileBinding.profileEmailTV.text = email
            }
        } else {
            // No user is signed in
        }
    }

    private fun SetProfileModifyBtnClickEvent() {
        profileBinding.profileModifyBtn.setOnClickListener {
            startActivity(Intent(this, ProfileModifyActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
    }

    private fun signOut() { // 로그아웃
        firebaseAuth.signOut() // Firebase sign out
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    private fun revokeAccess() { //회원탈퇴
        val user = Firebase.auth.currentUser!!
        signOut()
        user.delete()
            .addOnCompleteListener { task ->
                Toast.makeText(this, "회원 정보 삭제 완료", Toast.LENGTH_SHORT).show()
            }

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}