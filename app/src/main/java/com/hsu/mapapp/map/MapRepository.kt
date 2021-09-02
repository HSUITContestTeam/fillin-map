package com.hsu.mapapp.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MapRepository {
    private var firestore: FirebaseFirestore? = null
    private val uid = Firebase.auth.currentUser?.uid

    fun getData(): LiveData<MutableList<MapItemList>> {
        val mutableData = MutableLiveData<MutableList<MapItemList>>()
        firestore = FirebaseFirestore.getInstance()
        val myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get().addOnSuccessListener { document ->
            val listData: MutableList<MapItemList> = mutableListOf<MapItemList>()
            if (document.get("mapList") != null) {
                val mapList: ArrayList<Map<String, String>> =
                    document.get("mapList") as ArrayList<Map<String, String>>
                for (map in mapList) {
                    listData.add(
                        MapItemList(
                            map["mapTitle"].toString(),
                            map["previewImage"].toString(),
                            map["mapSort"].toString()
                        )
                    )
                    mutableData.value = listData
                }
            }
        }
        return mutableData
    }

    fun addData(item: MapItemList) {
        firestore = FirebaseFirestore.getInstance()
        val myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get().addOnSuccessListener { document ->
            val listData: MutableList<MapItemList> = mutableListOf()
            if (document.get("mapList") != null) {
                myRef.update("mapList", FieldValue.arrayUnion(item))
                Log.d("mapEdit", "add : ${item}")
            }
        }
    }

    fun deleteData(pos: Int) {
        firestore = FirebaseFirestore.getInstance()
        val myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get().addOnSuccessListener { document ->
            if (document.get("mapList") != null) {
                val mapList: ArrayList<Map<String, String>> =
                    document.get("mapList") as ArrayList<Map<String, String>>
                myRef.update("mapList", FieldValue.arrayRemove(mapList[pos]))
                mapList.removeAt(pos)
                Log.d("mapEdit", "delete : mapList[${pos}]")
            }
        }
    }

    fun editMapTitle(pos: Int, title: String) {
        firestore = FirebaseFirestore.getInstance()
        val myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get().addOnSuccessListener { document ->
            if (document.get("mapList") != null) {
                val mapList: ArrayList<Map<String, String>> =
                    document.get("mapList") as ArrayList<Map<String, String>>
                val remove = mapList[pos]
                val replace = MapItemList(
                    title,
                    mapList[pos].getValue("mapSort"),
                    mapList[pos].getValue("previewImage")
                )
                myRef.update("mapList", FieldValue.arrayRemove(mapList[pos]))
                myRef.update("mapList", FieldValue.arrayUnion(replace))
                Log.d("mapEdit", "titleChanged")
            }
        }
    }
}