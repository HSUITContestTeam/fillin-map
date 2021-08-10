package com.hsu.mapapp.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityProfileBinding
import com.hsu.mapapp.login.LoginActivity


class ProfileActivity : AppCompatActivity() {
    private lateinit var profileBinding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)

        setProfileModifyBtnClickEvent()
        setUpdatePasswordBtn()
        profileBinding.logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("정말로 로그아웃 하시겠습니까?")

            builder.setPositiveButton("예") { dialog, which ->
                signOut()
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                builder.setCancelable(true)
            }
            builder.show()
        }

        profileBinding.deleteAccountBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("정말로 탈퇴 하시겠습니까?")

            builder.setPositiveButton("예") { dialog, which ->
                revokeAccess()
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                builder.setCancelable(true)
            }
            builder.show()

        }

        //---------------------사용자 정보 load----------------------//

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        // 구글 로그인 정보
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //----------------------------------------------------------//

        // 프로필 표시
        setProfile()

        // name 설정
        profileBinding.buildNameIv.setOnClickListener {
            updateName()
        }

        // 프로필 사진 설정
        profileBinding.buildProfileIv.setOnClickListener {
            startActivity(Intent(this, CropImg::class.java))
        }
    }

    //---------------------프로필 표시----------------------//
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

    // 프로필 수정 버튼 이벤트
    private fun setProfileModifyBtnClickEvent() {
        profileBinding.profileModifyBtn.setOnClickListener {
            startActivity(Intent(this, CropImg::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        }
    }

    //---------------------로그아웃----------------------//
    private fun signOut() {
        firebaseAuth.signOut() // Firebase sign out
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    //---------------------회원 탈퇴----------------------//
    private fun revokeAccess() {
        val user = Firebase.auth.currentUser!!
        signOut()
        user.delete()
            .addOnCompleteListener { task ->
                Toast.makeText(this, "회원 정보 삭제 완료", Toast.LENGTH_SHORT).show()
            }

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    //---------------------비밀번호 재설정----------------------//
    private fun setUpdatePasswordBtn() {
        profileBinding.passwordChangeBtn.setOnClickListener {
            startActivity(Intent(this, UpdatePasswordActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        }
    }

    //---------------------닉네임 재설정----------------------//
    private fun updateName() {
        val user = Firebase.auth.currentUser
        var value = ""
        val linearLayout = View.inflate(this, R.layout.dialog_name, null)
        if (user != null) {
            // 닉네임 입력 다이얼로그 설정
            val builder = AlertDialog.Builder(this)
                .setView(linearLayout)
                .setPositiveButton("확인") { dialog, which ->
                    val editText: EditText = linearLayout.findViewById(R.id.name_editText)
                    value = editText.text.toString()
                    // user auth 업데이트
                    val profileUpdates = userProfileChangeRequest {
                        displayName = value
                        //photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                    }

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("user name", "updated")
                                // 바뀐 name text 설정
                                profileBinding.profileNameTV.text = user.displayName
                            }
                        }
                    // [END update_profile]
                }
                .setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }
            // 다이얼로그 실행
            builder.show()

        }
    }

}