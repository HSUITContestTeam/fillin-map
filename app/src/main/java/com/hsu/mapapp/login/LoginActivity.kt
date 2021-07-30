package com.hsu.mapapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
        setLoginBtnEvent()
        //initializig firebase auth object
        auth = Firebase.auth
    }
    // 로그인하기
    private fun loginEmail(email: EditText, password: EditText) {
        auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString())
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // 로그인 성공
                    Toast.makeText(this,"로그인 성공",Toast.LENGTH_SHORT).show()
                    val currentUser = auth.currentUser
                    updateUI(currentUser,email,password)
                }
                else {
                    // 로그인 실패
                    Toast.makeText(this,"로그인 실패",Toast.LENGTH_SHORT).show()
                    updateUI(null,email,password)
                }
        }
    }
    // UI 업데이트
    private fun updateUI(currentUser: FirebaseUser? = null,email: EditText, password: EditText) {
        if(currentUser != null) {

        }
        // 로그인 입력창 초기화
        email.setText("")
        password.setText("")
    }
    // 이메일, 비밀번호 형식 체크
    private fun checkForm(email: EditText, password: EditText): Boolean {
        if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일 혹은 비밀번호를 반드시 입력하세요", Toast.LENGTH_SHORT).show()
            return false;
        }
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        if (!emailPattern.matcher(email.text.toString()).matches()) {
            Toast.makeText(this, "이메일 형식을 확인하세요", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (password.text.toString().length < 6) {
            Toast.makeText(this, "비밀번호를 6자 이상 입력하세요", Toast.LENGTH_SHORT).show()
            return false;
        }
        return true;
    }
    // 로그인 버튼 이벤트
    private fun setLoginBtnEvent() {
        loginBinding.loginLOGINBtn.setOnClickListener {
            val email = loginBinding.loginIdET
            val password = loginBinding.loginPwdET
            if (checkForm(email,password))
            {
                loginEmail(email,password)
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
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