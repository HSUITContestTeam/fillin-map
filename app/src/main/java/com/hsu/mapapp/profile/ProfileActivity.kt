package com.hsu.mapapp.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityProfileBinding
import com.hsu.mapapp.login.AddUser
import com.hsu.mapapp.login.LoginActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var profileBinding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG: String = "AppDebug"
    private val GALLERY_REQUEST_CODE = 1234
    private var uriPhoto: Uri? = null
    private var urlProfile: String? = null
    private var userID: String? = null

    private var firestore: FirebaseFirestore? = null
    private val uid = Firebase.auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)
        firestore = FirebaseFirestore.getInstance()

        setUpdatePasswordBtn()

        profileBinding.logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("????????? ???????????? ???????????????????")

            builder.setPositiveButton("???") { dialog, which ->
                signOut()
            }
            builder.setNegativeButton("?????????") { dialog, which ->
                builder.setCancelable(true)
            }
            builder.show()
        }

        profileBinding.deleteAccountBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("????????? ?????? ???????????????????")

            builder.setPositiveButton("???") { dialog, which ->
                revokeAccess()
            }
            builder.setNegativeButton("?????????") { dialog, which ->
                builder.setCancelable(true)
            }
            builder.show()

        }

        //---------------------????????? ?????? load----------------------//

        //firebase auth ??????
        firebaseAuth = FirebaseAuth.getInstance()

        // ?????? ????????? ??????
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //----------------------------------------------------------//

        // ????????? ??????
        setProfile()

        // name ??????
        profileBinding.buildNameIv.setOnClickListener {
            updateName()
        }

        //????????? ????????? ??????
        profileBinding.buildMessageIv.setOnClickListener{
            updateMessage()
        }

        // ????????? ?????? ??????
        profileBinding.buildProfileIv.setOnClickListener {
            //startActivity(Intent(this, CropImg::class.java))
            pickFromGallery()
        }
    }

    //---------------------????????? ??????----------------------//
    private fun setProfile() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            user.let {
                val email = user.email
                val firestore = FirebaseFirestore.getInstance()

                val docRef = firestore.collection("users").document(uid!!)
                docRef.get()
                    .addOnSuccessListener { document ->
                        profileBinding.profileNameTV.text = document.get("name").toString()
                        profileBinding.profileMessageTV.text = document.get("Message").toString()
                        if(profileBinding.profileMessageTV.text == "null"){
                            profileBinding.profileMessageTV.text = ""
                        }

                        val progressDialog = ProgressDialog(this)
                        progressDialog.setMessage("Fetching Image ...")
                        progressDialog.setCancelable(false)
                        progressDialog.show()
                        urlProfile = document.get("photoUrl").toString()
                        userID = document.get("uid").toString()
                        Glide.with(this)
                            .load(urlProfile)
                            .into(profileBinding.profileImageIV)
                        if (progressDialog.isShowing)
                            progressDialog.dismiss()
                        /*val localfile = File.createTempFile("tempImage","jpg")
                                val storageRef = FirebaseStorage.getInstance().reference.child("ProfileImage").child("$value").getFile(localfile)
                                storageRef.addOnSuccessListener {
                                    if(progressDialog.isShowing)
                                        progressDialog.dismiss()
                                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                    profileBinding.profileImageIV.setImageBitmap(bitmap)
                                }.addOnFailureListener{
                                    if(progressDialog.isShowing)
                                        progressDialog.dismiss()
                                    Toast.makeText(this, "Failed to retrieve your image",Toast.LENGTH_SHORT).show()
                                }*/
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
                //profileBinding.profileNameTV.text = name
                profileBinding.profileEmailTV.text = email

            }
        } else {
            // No user is signed in
        }
    }

    //---------------------????????????----------------------//
    private fun signOut() {
        firebaseAuth.signOut() // Firebase sign out
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    //---------------------?????? ??????----------------------//
    private fun revokeAccess() {
        val user = Firebase.auth.currentUser!!

        // firestore?????? ???????????? ??????
        firestore?.collection("users")?.document(user.uid)?.delete()
            ?.addOnSuccessListener { Log.d("firebase user info delete", "success")}
            ?.addOnFailureListener { e -> Log.w("firebase user info delete", "fail", e) }

        // firebase authentication ?????? ???????????? ??????
        user.delete()
            .addOnCompleteListener { task ->
                Toast.makeText(this, "?????? ?????? ?????? ??????", Toast.LENGTH_SHORT).show()
            }

        signOut() // ????????????

        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    //---------------------???????????? ?????????----------------------//
    private fun setUpdatePasswordBtn() {
        profileBinding.passwordChangeBtn.setOnClickListener {
            startActivity(Intent(this, UpdatePasswordActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        }
    }

    //---------------------????????? ?????????----------------------//
    private fun updateName() {
        val user = Firebase.auth.currentUser
        val uid = user?.uid
        var value_N = ""

        val linearLayout_N = View.inflate(this, R.layout.dialog_name, null)

        val editText: EditText = linearLayout_N.findViewById(R.id.name_editText)

        // firebase?????? ?????????, ??????????????? ????????? ?????????????????? ??????
        if (user != null) {
            user.let {
                val email = user.email
                val firestore = FirebaseFirestore.getInstance()

                val docRef = firestore.collection("users").document(uid!!)
                docRef.get()
                    .addOnSuccessListener { document ->
                        val userName = document.get("name").toString()
                        editText.setText(userName)
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
            }
        }

        if (user != null) {
            // ????????? ?????? ??????????????? ??????
            val builder = AlertDialog.Builder(this)
                .setView(linearLayout_N)
                .setPositiveButton("??????") { dialog, which ->
                    //val editText: EditText = linearLayout.findViewById(R.id.name_editText)
                    //val editText_M : EditText = linearLayout.findViewById(R.id.Message_editText)

                    value_N = editText.text.toString()

                    profileBinding.profileNameTV.text = value_N

                    // firestore - users - name ????????????
                    val profileRef = firestore?.collection("users")?.document(uid!!)
                    profileRef?.get()?.addOnSuccessListener {
                        profileRef.update("name", value_N)
                    }
                    // [END update_profile]

                }
                .setNegativeButton("??????") { dialog, which ->
                    dialog.dismiss()
                }
            // ??????????????? ??????
            builder.show()

        }
    }


    //---------------------????????? ????????? ?????????----------------------//
    private fun updateMessage() {
        val user = Firebase.auth.currentUser
        val uid = user?.uid
        var value_M = ""

        val linearLayout_M = View.inflate(this, R.layout.dialog_message, null)
        val editText: EditText = linearLayout_M.findViewById(R.id.Message_editText)

        //?????? ????????? ????????? ?????? ??????
        if (user != null) {
            user.let {
                val email = user.email
                val firestore = FirebaseFirestore.getInstance()
                val docRef = firestore.collection("users").document(uid!!)
                docRef.get()
                    .addOnSuccessListener { document ->
                        val userMessage = document.get("Message").toString()
                        editText.setText(userMessage)
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with", exception)
                    }
            }
        }

        if (user != null) {
            //??????????????? ??????
            val builder = AlertDialog.Builder(this)
                .setView(linearLayout_M)
                .setPositiveButton("??????") { diolog, which ->
                    value_M = editText.text.toString()
                    profileBinding.profileMessageTV.text = value_M

                    val profileRef = firestore?.collection("users")?.document(uid!!)
                    profileRef?.get()?.addOnSuccessListener {
                        profileRef.update("Message", value_M)
                    }
                }
                .setNegativeButton("??????") { diolog, which ->
                    diolog.dismiss()
                }
            builder.show()
        }
    }

    //------------------????????? ????????? ??????----------------------//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                } else {
                    Log.e(TAG, "Image selection error: Couldn't select that image from memory.")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                uriPhoto = result.uri
                if (resultCode == Activity.RESULT_OK) {
                    setImage(result.uri)
                    imageUpload()
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e(TAG, "Crop error: ${result.getError()}")
                }
            }
        }
    }

    private fun imageUpload() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReference("ProfileImage/$userID")

        storageReference.putFile(uriPhoto!!).addOnSuccessListener {
            Toast.makeText(this@ProfileActivity, "Successfully uploaded", Toast.LENGTH_SHORT).show()
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                var userInfo = AddUser()
                var fbFirestore = FirebaseFirestore.getInstance()
                var fbAuth = FirebaseAuth.getInstance()
                userInfo.photoUrl = uri.toString()
                fbFirestore?.collection("users")?.document(fbAuth?.uid.toString())
                    ?.update("photoUrl", userInfo.photoUrl.toString())
            }
            if (progressDialog.isShowing)
                progressDialog.dismiss()
        }.addOnFailureListener {
            if (progressDialog.isShowing)
                progressDialog.dismiss()
            Toast.makeText(this@ProfileActivity, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(profileBinding.profileImageIV)
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(this)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
}