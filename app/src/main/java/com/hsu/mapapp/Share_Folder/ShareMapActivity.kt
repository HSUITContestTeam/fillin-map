package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.databinding.ActivityShareMapBinding

class ShareMapActivity : AppCompatActivity() {
    private lateinit var shareMapBinding: ActivityShareMapBinding
    private var firestore : FirebaseFirestore?= FirebaseFirestore.getInstance()
    private val uid = Firebase.auth.currentUser?.uid
    private val mapList = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareMapBinding = ActivityShareMapBinding.inflate(layoutInflater)
        setContentView(shareMapBinding.root)

        firestore = FirebaseFirestore.getInstance()

        val friendUid: String = intent?.getStringExtra("FriendUid") ?: ""
        val uidRef = firestore!!.collection("users").document(friendUid)
        uidRef.get().addOnSuccessListener {document ->
            if(document.get("mapList") != null){

            }
        }
    }

}