package com.hsu.mapapp.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

class ProfileFragment : Fragment(R.layout.activity_profile) {
    private lateinit var _binding: ActivityProfileBinding
    private val profileBinding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG: String = "AppDebug"
    private val GALLERY_REQUEST_CODE = 1234
    private var uriPhoto: Uri? = null
    private var urlProfile: String? = null
    private var userID: String? = null

    private var firestore: FirebaseFirestore? = null
    private val uid = Firebase.auth.currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  ActivityProfileBinding.inflate(inflater, container, false)
        // appbar - 뒤로 가기 버튼 없애기
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        // 액션바 제목 변경
        (activity as AppCompatActivity).supportActionBar!!.title = "프로필"
        firestore = FirebaseFirestore.getInstance()

        // 로그아웃 이벤트
        profileBinding.logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("정말로 로그아웃 하시겠습니까?")

            builder.setPositiveButton("예") { dialog, which ->
                signOut()
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                builder.setCancelable(true)
            }
            builder.show()
        }

        // 회원탈퇴 이벤트
        profileBinding.deleteAccountBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity())
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

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        //----------------------------------------------------------//

        // 프로필 표시
        setProfile()

        // name 설정
        profileBinding.buildNameIv.setOnClickListener {
            updateName()
        }

        //프로필 메세지 설정
        profileBinding.buildMessageIv.setOnClickListener{
            updateMessage()
        }

        // 프로필 사진 설정
        profileBinding.buildProfileIv.setOnClickListener {
            pickFromGallery()
        }

        // 비밀번호 재설정
        setUpdatePasswordBtn()

        // 프로필 편집 기능 ON/OFF 설정
        modifyProfile()

        return profileBinding.root
    }

    override fun onResume() {
        super.onResume()
        // 프로필 편집 기능 off
        setModifyOff()
    }
    //---------------------프로필 표시----------------------//
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

                        val progressDialog = ProgressDialog(activity)
                        progressDialog.setMessage("Fetching Image ...")
                        progressDialog.setCancelable(false)
                        progressDialog.show()
                        urlProfile = document.get("photoUrl").toString()
                        userID = document.get("uid").toString()
                        Glide.with(this)
                            .load(urlProfile).circleCrop()
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

    //---------------------프로필 편집 기능 ON/OFF----------------------//
    private fun modifyProfile(){
        profileBinding.profileModifyButton.setOnClickListener {
            if(profileBinding.buildMessageIv.visibility == View.INVISIBLE){
                setModifyOn() // 프로필 편집 기능 ON
            } else{
                setModifyOff() // 프로필 편집 기능 OFF
            }

        }
    }
    // 프로필 편집 기능 ON
    private fun setModifyOn() {
        profileBinding.buildMessageIv.apply{visibility = View.VISIBLE}
        profileBinding.buildNameIv.apply{visibility = View.VISIBLE}
        profileBinding.buildProfileIv.apply{visibility = View.VISIBLE}
        profileBinding.profileModifyButton.setBackgroundColor(Color.parseColor("#8daaf6"))
    }
    // 프로필 편집 기능 OFF
    private fun setModifyOff() {
        profileBinding.buildMessageIv.apply{visibility = View.INVISIBLE}
        profileBinding.buildNameIv.apply{visibility = View.INVISIBLE}
        profileBinding.buildProfileIv.apply{visibility = View.INVISIBLE}
        profileBinding.profileModifyButton.setBackgroundColor(Color.parseColor("#1f57ed"))
    }


    //---------------------로그아웃----------------------//
    private fun signOut() {
        firebaseAuth.signOut() // Firebase sign out
        // Google sign out
//        googleSignInClient.signOut().addOnCompleteListener(this) {
//            //updateUI(null)
//        }

        val loginIntent = Intent(activity, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    //---------------------회원 탈퇴----------------------//
    private fun revokeAccess() {
        val user = Firebase.auth.currentUser!!

        // firestore에서 회원정보 삭제
        firestore?.collection("users")?.document(user.uid)?.delete()
            ?.addOnSuccessListener { Log.d("firebase user info delete", "success")}
            ?.addOnFailureListener { e -> Log.w("firebase user info delete", "fail", e) }

        // firebase authentication 에서 회원정보 삭제
        user.delete()
            .addOnCompleteListener { task ->
                Toast.makeText(activity, "회원 탈퇴가 성공적으로 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }

        signOut() // 로그아웃

        val loginIntent = Intent(activity, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    //---------------------비밀번호 재설정----------------------//
    private fun setUpdatePasswordBtn() {
        profileBinding.passwordChangeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_updatePasswordFragment)
        }
    }

    //---------------------닉네임 재설정----------------------//
    private fun updateName() {
        val user = Firebase.auth.currentUser
        val uid = user?.uid
        var value_N = ""

        val linearLayout_N = View.inflate(activity, R.layout.dialog_name, null)

        val editText: EditText = linearLayout_N.findViewById(R.id.name_editText)

        // firebase에서 닉네임, 상태메세지 불러와 다이얼로그에 표시
        user?.let {
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

        if (user != null) {
            // 닉네임 입력 다이얼로그 설정
            val builder = activity?.let {
                AlertDialog.Builder(it)
                    .setView(linearLayout_N)
                    .setPositiveButton("확인") { dialog, which ->
                        //val editText: EditText = linearLayout.findViewById(R.id.name_editText)
                        //val editText_M : EditText = linearLayout.findViewById(R.id.Message_editText)

                        value_N = editText.text.toString()

                        profileBinding.profileNameTV.text = value_N

                        // firestore - users - name 업데이트
                        val profileRef = firestore?.collection("users")?.document(uid!!)
                        profileRef?.get()?.addOnSuccessListener {
                            profileRef.update("name", value_N)
                        }
                        // [END update_profile]

                    }
                    .setNegativeButton("취소") { dialog, which ->
                        dialog.dismiss()
                    }
            }
            // 다이얼로그 실행
            builder?.show()

        }
        setModifyOff()
    }


    //---------------------프로필 메세지 재설정----------------------//
    private fun updateMessage() {
        val user = Firebase.auth.currentUser
        val uid = user?.uid
        var value_M = ""

        val linearLayout_M = View.inflate(requireActivity(), R.layout.dialog_message, null)
        val editText: EditText = linearLayout_M.findViewById(R.id.Message_editText)

        //기존 메세지 다이얼 로그 표시
        user?.let {
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

        if (user != null) {
            //다이얼로그 설정
            val builder = activity?.let {
                AlertDialog.Builder(it)
                    .setView(linearLayout_M)
                    .setPositiveButton("확인") { diolog, which ->
                        value_M = editText.text.toString()
                        profileBinding.profileMessageTV.text = value_M

                        val profileRef = firestore?.collection("users")?.document(uid!!)
                        profileRef?.get()?.addOnSuccessListener {
                            profileRef.update("Message", value_M)
                        }
                    }
                    .setNegativeButton("취소") { diolog, which ->
                        diolog.dismiss()
                    }
            }
            builder?.show()
        }
        setModifyOff()
    }

    //------------------프로필 이미지 크롭----------------------//
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
                Log.d("result","crop")
                val result = CropImage.getActivityResult(data)

                uriPhoto = result.uri

                if (resultCode == Activity.RESULT_OK) {
                    setImage(result.uri)
                    imageUpload()
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e(TAG, "Crop error: ${result.error}")
                }
            }
        }
    }

    private fun imageUpload() {
        val progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReference("ProfileImage/$userID")
        storageReference.putFile(uriPhoto!!).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val userInfo = AddUser()
                val fbFirestore = FirebaseFirestore.getInstance()
                val fbAuth = FirebaseAuth.getInstance()
                userInfo.photoUrl = uri.toString()
                fbFirestore.collection("users").document(fbAuth.uid.toString())
                    .update("photoUrl", userInfo.photoUrl.toString())
            }
            if (progressDialog.isShowing)
                progressDialog.dismiss()
        }.addOnFailureListener {
            if (progressDialog.isShowing)
                progressDialog.dismiss()
        }

    }

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri).circleCrop()
            .into(profileBinding.profileImageIV)
    }

    private fun launchImageCrop(uri: Uri) {
        val intent = CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .getIntent(requireActivity())
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


}