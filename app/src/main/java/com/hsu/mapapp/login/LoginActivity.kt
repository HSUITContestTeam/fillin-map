package com.hsu.mapapp.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var loginBinding: ActivityLoginBinding

    val GOOGLE_REQUEST_CODE = 99
    val TAG = "googleLogin"
    private lateinit var googleSignInClient: GoogleSignInClient
    // Firebase Auth
    private lateinit var auth: FirebaseAuth
    private lateinit var fbAuth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_MEDIA_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.VIBRATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        // ?????? ?????? ??????
        requestMultiplePermission(REQUIRED_PERMISSIONS)
//        if(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
//            hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)&&
//            hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)&&
//            hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)&&
//            hasPermission(Manifest.permission.CAMERA)&&
//            hasPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)&&
//            hasPermission(Manifest.permission.INTERNET) &&
//            hasPermission(Manifest.permission.VIBRATE))
//        {
//
//        }else{
//
//        }
        setCreateUserBtnEvent()
        setLoginBtnEvent()
        setGoogleLogin()

        //initializig firebase auth object
        firestore = FirebaseFirestore.getInstance() //Firestore??????
        fbAuth = FirebaseAuth.getInstance() // Firebase Auth ??????
        auth = Firebase.auth
    }
    //---------------------------- ?????? ?????? ?????? ----------------------------------//
    private fun requestMultiplePermission(perms: Array<String>) {
        val requestPerms = perms.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (requestPerms.isEmpty())
            return

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val noPerms = it.filter { item -> item.value == false }.keys
            if (noPerms.isNotEmpty()) { // there is a permission which is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage(getString(R.string.no_permission, noPerms.toString()))
                }.show()
            }
        }
        val showRationalePerms = requestPerms.filter {shouldShowRequestPermissionRationale(it)}
        if (showRationalePerms.isNotEmpty()) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage(getString(R.string.req_permission_reason, requestPerms))
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(requestPerms.toTypedArray()) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(requestPerms.toTypedArray())
        }
    }
    private fun hasPermission(perm: String) =
        checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED

    //---------------------------- ???????????? ----------------------------------//

    // ???????????? ?????? ?????????
    private fun setCreateUserBtnEvent() {
        loginBinding.loginCreateUserBtn.setOnClickListener {
            val createUserIntent = Intent(this, CreateUserActivity::class.java)
            startActivity(createUserIntent)
        }
    }

    //---------------------------- ????????? ????????? ----------------------------------//

    // ???????????????
    private fun loginEmail(email: EditText, password: EditText) {
        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser

                    //???????????? ??????????????? ??????.
                    if (currentUser?.isEmailVerified!!) {
                        updateUI(currentUser, email, password)
                    } else {
                        Toast.makeText(applicationContext, "????????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "????????? ??????", Toast.LENGTH_SHORT).show()
                    updateUI(null, email, password)
                }
            }

    }

    // UI ????????????
    private fun updateUI(currentUser: FirebaseUser? = null, email: EditText, password: EditText) {
        if (currentUser != null) { // ????????? ????????? ??????
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
        else{ // ????????? ????????? ??????
            Toast.makeText(applicationContext, "????????? ??????", Toast.LENGTH_SHORT).show()
        }
        // ????????? ????????? ?????????
        email.setText("")
        password.setText("")
    }

    // ?????????, ???????????? ?????? ??????
    private fun checkForm(email: EditText, password: EditText): Boolean {
        if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
            Toast.makeText(this, "????????? ?????? ??????????????? ????????? ???????????????", Toast.LENGTH_SHORT).show()
            return false;
        }
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        if (!emailPattern.matcher(email.text.toString()).matches()) {
            Toast.makeText(this, "????????? ????????? ???????????????", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (password.text.toString().length < 6) {
            Toast.makeText(this, "??????????????? 6??? ?????? ???????????????", Toast.LENGTH_SHORT).show()
            return false;
        }
        return true;
    }

    // ????????? ?????? ?????????
    private fun setLoginBtnEvent() {
        loginBinding.loginLOGINBtn.setOnClickListener {
            val email = loginBinding.loginIdET
            val password = loginBinding.loginPwdET
            if (checkForm(email, password)) {
                loginEmail(email, password)
            }
        }
    }

    //-----------------------------?????? ????????? ??????----------------------------------//

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
            println("???????????????")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "????????? ??????", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "????????? ??????")
                    val user = auth.currentUser
                    loginSuccess(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun loginSuccess(user: FirebaseUser?) {
        if (user != null) {
            // firestore??? ?????? ?????? ????????? ????????? ??????
            firestore.collection("users").document(user.uid).get().addOnSuccessListener { doc ->
                if (doc.exists()) { // ????????? MainActivity??? ??????
                    toMainActivity(user)
                }
                else { // ????????? ?????? ??????
                    setNickName(user)
                }
            }
        }
    }

    private fun setNickName(user: FirebaseUser?) {
        // ????????? ?????? ??????????????? ??????
        var nickname = ""
        val linearLayout = View.inflate(this, R.layout.dialog_name, null)
        val builder = AlertDialog.Builder(this)
            .setView(linearLayout)
            .setPositiveButton("??????") { dialog, which ->
                val editText: EditText = linearLayout.findViewById(R.id.name_editText)
                nickname = editText.text.toString()
                if (nickname != "") { // ????????? ?????? ?????????
                    addUserToFireStore(nickname, user)
                } else {
                    Snackbar.make(loginBinding.root, "???????????? ????????? ????????? ?????????!????", Snackbar.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("??????") { dialog, which ->
                dialog.dismiss()
            }
        // ??????????????? ??????
        builder.show()
    }

    private fun addUserToFireStore(nickname : String, user: FirebaseUser?) {
        val userInfo = AddUser()
        userInfo.uid = auth.uid //?????? ?????? ????????????
        userInfo.userId = auth.currentUser?.email
        userInfo.name = nickname
        firestore.collection("users").document(auth.uid.toString()).set(userInfo)
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
        // ?????? ????????? - Google
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            toMainActivity(auth.currentUser)
        }
        // ?????? ????????? - ?????????
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.isEmailVerified) {
            toMainActivity(auth.currentUser)
        }
    }

}