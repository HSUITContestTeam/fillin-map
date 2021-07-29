package com.hsu.mapapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.MainActivity
import com.hsu.mapapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding : ActivityLoginBinding
    // Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        setCreateUserBtnEvent()
        //initializig firebase auth object
        auth = Firebase.auth
    }

    private fun loginEmail() {
        auth.signInWithEmailAndPassword(loginBinding.loginIdET.text.toString(),loginBinding.loginPwdET.text.toString())
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // 로그인 성공
                    Toast.makeText(this,"Authentication success",Toast.LENGTH_SHORT).show()
                    val currentUser = auth.currentUser
                    //updateUI(currentUser)
                }
                else {
                    // 로그인 실패
                    Toast.makeText(this,"Authentication failed",Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
        }
    }
    private fun updateUI(currentUser: FirebaseAuth? = null) {
        if(currentUser != null) {
            setLoginBtnEvent()
        }
        // 로그인 입력창 초기화
        loginBinding.loginIdET.setText("")
        loginBinding.loginPwdET.setText("")
    }

    // 로그인 버튼 이벤트
    private fun setLoginBtnEvent() {
        loginBinding.loginLOGINBtn.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
    }
    // 회원가입 버튼 이벤트
    private fun setCreateUserBtnEvent() {
        loginBinding.loginCreateUserBtn.setOnClickListener {
            val createUserIntent = Intent(this,CreateUserActivity::class.java)
            startActivity(createUserIntent)
        }
    }
}