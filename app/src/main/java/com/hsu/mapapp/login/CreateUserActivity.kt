package com.hsu.mapapp.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.databinding.ActivityCreateUserBinding

class CreateUserActivity : AppCompatActivity() {
    private lateinit var createUseBinding:ActivityCreateUserBinding
    // Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createUseBinding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(createUseBinding.root)

        auth = Firebase.auth
        setJoinBtnEvent()
    }
    // 계정 생성
    private fun createEmailUser() {
        auth.createUserWithEmailAndPassword(createUseBinding.emailET.text.toString(),createUseBinding.passwordET.text.toString())
            .addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful) {
                    // 회원가입 성공
                    val currentUser = auth.currentUser
                    Toast.makeText(this,"Authentication success", Toast.LENGTH_SHORT).show()
                    // 회원가입 액티비티 종료
                    finishActivity(1)
                }
                else {
                    // 회원가입 실패
                    Toast.makeText(this,"Authentication failed", Toast.LENGTH_SHORT).show()

                }
            }
            .addOnFailureListener(OnFailureListener(){
                it.printStackTrace()
            })


    }
    // 가입 버튼 이벤트
    private fun setJoinBtnEvent() {
        createUseBinding.joinBtn.setOnClickListener {
            createEmailUser()
        }
    }
}