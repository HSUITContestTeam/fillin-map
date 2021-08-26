package com.hsu.mapapp.login

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
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.MainActivity
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


    val GOOGLE_REQUEST_CODE = 99
    val TAG = "googleLogin"
    private lateinit var googleSignInClient: GoogleSignInClient

    // Firebase Auth
    private lateinit var auth: FirebaseAuth
    private lateinit var fbAuth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    private lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        setCreateUserBtnEvent()
        setLoginBtnEvent()
        setGoogleLogin()

        //initializig firebase auth object
        firestore = FirebaseFirestore.getInstance() //Firestore선언
        fbAuth = FirebaseAuth.getInstance() // Firebase Auth 선언
        auth = Firebase.auth
    }

    //---------------------------- 회원가입 ----------------------------------//

    // 회원가입 버튼 이벤트
    private fun setCreateUserBtnEvent() {
        loginBinding.loginCreateUserBtn.setOnClickListener {
            val createUserIntent = Intent(this, CreateUserActivity::class.java)
            startActivity(createUserIntent)
        }
    }

    //---------------------------- 이메일 로그인 ----------------------------------//

    // 로그인하기
    private fun loginEmail(email: EditText, password: EditText) {
        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser

                    //인증받은 사용자인지 확인.
                    if (currentUser?.isEmailVerified!!) {
                        Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                        updateUI(currentUser, email, password)
                    } else {
                        Toast.makeText(applicationContext, "이메일 인증을 하지 않았습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    updateUI(null, email, password)
                }
            }

    }

    // UI 업데이트
    private fun updateUI(currentUser: FirebaseUser? = null, email: EditText, password: EditText) {
        if (currentUser != null) { // 입력한 계정이 있음
            Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
        else{ // 입력한 계정이 없음
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
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
            if (checkForm(email, password)) {
                loginEmail(email, password)
            }
        }
    }

    //-----------------------------구글 로그인 설정----------------------------------//

    private fun setGoogleLogin() {
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        loginBinding.loginGoogleBtn.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE)
    }

    // onActivityResult() function : this is where
    // we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            println("로그인시도")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "로그인 성공")
                    val user = auth!!.currentUser
                    loginSuccess(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun loginSuccess(user: FirebaseUser?) {
        if (user != null) {
            // firestore에 해당 계정 정보가 있는지 체크
            firestore.collection("users").document(user.uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) { // 있으면 MainActivity로 이동
                    toMainActivity(user)
                }
                else { // 없으면 새로 등록
                    setNickName(user)
                }
            }
        }
    }

    private fun setNickName(user: FirebaseUser?) {
        // 닉네임 입력 다이얼로그 설정
        var nickname = ""
        val linearLayout = View.inflate(this, R.layout.dialog_name, null)
        val builder = AlertDialog.Builder(this)
            .setView(linearLayout)
            .setPositiveButton("확인") { dialog, which ->
                val editText: EditText = linearLayout.findViewById(R.id.name_editText)
                nickname = editText.text.toString()
                if (nickname != "") { // 닉네임 입력 안하면
                    addUserToFireStore(nickname, user)
                } else {
                    Snackbar.make(loginBinding.root, "닉네임을 반드시 입력해 주세요!😊", Snackbar.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }
        // 다이얼로그 실행
        builder.show()
    }

    private fun addUserToFireStore(nickname : String, user: FirebaseUser?) {
        val userInfo = AddUser()
        userInfo.uid = auth?.uid //유저 정보 가져오기
        userInfo.userId = auth?.currentUser?.email
        userInfo.name = nickname
        firestore?.collection("users")?.document(auth?.uid.toString())?.set(userInfo)
        toMainActivity(user)
    }

    private fun toMainActivity(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            toMainActivity(auth.currentUser)
        }
    }

}