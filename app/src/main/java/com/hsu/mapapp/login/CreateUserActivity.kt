package com.hsu.mapapp.login

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.databinding.ActivityCreateUserBinding

class CreateUserActivity : AppCompatActivity() {
    private lateinit var createUseBinding: ActivityCreateUserBinding

    // Firebase Auth
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var fbAuth : FirebaseAuth

    // android.util에서 제공하는 이메일 패턴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createUseBinding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(createUseBinding.root)

        firestore = FirebaseFirestore.getInstance() //Firestore선언
        fbAuth = FirebaseAuth.getInstance() // Firebase Auth 선언
        auth = Firebase.auth
        setJoinBtnEvent()
    }

    //-----------------------------회원 가입----------------------------------//

    // 계정 생성
    private fun createEmailUser(email: EditText, password: EditText) {
        auth.createUserWithEmailAndPassword(
            email.text.toString(),
            password.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공
                    val currentUser = auth.currentUser

                    //사용자 인증메일 보내기.
                    currentUser
                        ?.sendEmailVerification()
                        ?.addOnCompleteListener { varifiTask ->
                            if (varifiTask.isSuccessful) {
                                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                // 계정 정보 firbase에 추가
                                addUserInfoToFirebase()
                                // 회원가입 액티비티 종료
                                finish()
                            } else {
                                Toast.makeText(this, "에러", Toast.LENGTH_SHORT).show()
                            }
                        }

                } else {
                    // 계정 중복
                    Toast.makeText(this, "계정이 이미 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener(OnFailureListener() {
                it.printStackTrace()
            })


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

    // 가입 버튼 이벤트
    private fun setJoinBtnEvent() {
        createUseBinding.joinBtn.setOnClickListener {
            val email = createUseBinding.emailEt
            val password = createUseBinding.passwordEt

            if (checkForm(email, password)) {
                createEmailUser(email, password)
            }
        }
    }

    // 계정 정보 firbase에 추가
    private fun addUserInfoToFirebase() {
        // 계정 정보 firebase에 추가
        var userInfo = AddUser()

        userInfo.name = createUseBinding.nameEt.text.toString()
        userInfo.uid = auth?.uid //유저 정보 가져오기
        userInfo.userId = auth?.currentUser?.email

        //Firestore데이터 베이스에 업로드
        firestore?.collection("users")?.document(auth?.uid.toString())?.set(userInfo)
    }

}