package com.hsu.mapapp.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MapViewModel : ViewModel() {
    var mapLiveData: MutableLiveData<ArrayList<MapItemList>> =
        MutableLiveData()

    private var firestore: FirebaseFirestore? = null
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    init {
        mapLiveData = fetch()
    }

    fun fetch(): MutableLiveData<ArrayList<MapItemList>> {
        firestore = FirebaseFirestore.getInstance()
        val listData: ArrayList<MapItemList> = arrayListOf()
        val myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get().addOnSuccessListener { document ->
            if (document.get("mapList") != null) {
                val mapList: ArrayList<Map<String, String>> =
                    document.get("mapList") as ArrayList<Map<String, String>>
                for (map in mapList) {
                    listData.add(
                        MapItemList(
                            map.getValue("mapTitle"),
                            map.getValue("previewImage"),
                            map.getValue("mapSort"),
                            map.getValue("mapId")
                        )
                    )
                }
            }
            mapLiveData.postValue(listData)
        }
        return mapLiveData
    }

    fun add(mapItem: MapItemList) {
        firestore = FirebaseFirestore.getInstance()
        val myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get().addOnSuccessListener { document ->
            myRef.update("mapList", FieldValue.arrayUnion(mapItem))
            mapLiveData.postValue(fetch().value)
        }
    }

    fun delete(pos: Int, mapId: String) {
        firestore = FirebaseFirestore.getInstance()
        val myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get().addOnSuccessListener { document ->
            if (document.get("mapList") != null) {
                val mapList: ArrayList<Map<String, String>> =
                    document.get("mapList") as ArrayList<Map<String, String>>
                myRef.update("mapList", FieldValue.arrayRemove(mapList[pos]))
                mapList.removeAt(pos)
                mapLiveData.postValue(fetch().value)

                // firebase storage에서 해당 map 삭제
                val storage: FirebaseStorage = FirebaseStorage.getInstance()
                val storageRef = storage.reference.child("mapImageView/$mapId")
                storageRef.listAll().addOnSuccessListener { result ->
                    for (file in result.items) {
                        file.delete()
                    }
                }
            }
        }
    }

    fun editTitle(pos: Int, title: String) {
    }
}
