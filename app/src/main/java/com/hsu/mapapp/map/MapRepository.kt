package com.hsu.mapapp.map

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MapRepository {
    private var firestore: FirebaseFirestore? = null
    private val uid = Firebase.auth.currentUser?.uid
    interface MyCallback {
        fun onCallback(mapList : ArrayList<MapItemList>)
    }
    fun getData(myCallback: MyCallback){
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
                            map["mapTitle"].toString(),
                            map["previewImage"].toString(),
                            map["mapSort"].toString()
                        )
                    )
                }
                myCallback.onCallback(listData)
            }
        }
    }

    //    MapViewModel에서 원래 있던
//    init {
//        mapRepository.getData(object : MapRepository.MyCallback {
//            override fun onCallback(mapList : ArrayList<MapItemList>) {
//                mapData = mapList
//                Log.d("mapData",mapData.toString())
//            }
//        })
//        mapLiveData.postValue(mapData)
//        println("mapViewModel: $mapData")
//    }

    // 원래 있던 거
//    fun addData(item: MapItemList): ArrayList<MapItemList> {
//        firestore = FirebaseFirestore.getInstance()
//        val myRef = firestore?.collection("users")?.document("$uid")
//        myRef!!.get().addOnSuccessListener { document ->
//            myRef.update("mapList", FieldValue.arrayUnion(item))
//        }
//        return getData()
//    }
//
//    fun deleteData(pos: Int): ArrayList<MapItemList> {
//        firestore = FirebaseFirestore.getInstance()
//        val myRef = firestore?.collection("users")?.document("$uid")
//        myRef!!.get().addOnSuccessListener { document ->
//            if (document.get("mapList") != null) {
//                val mapList: ArrayList<Map<String, String>> =
//                    document.get("mapList") as ArrayList<Map<String, String>>
//                myRef.update("mapList", FieldValue.arrayRemove(mapList[pos]))
//                mapList.removeAt(pos)
//            }
//        }
//        return getData()
//    }
//
//    fun editMapTitle(pos: Int, title: String): ArrayList<MapItemList> {
//        firestore = FirebaseFirestore.getInstance()
//        val myRef = firestore?.collection("users")?.document("$uid")
//        myRef!!.get().addOnSuccessListener { document ->
//            if (document.get("mapList") != null) {
//                val mapList: ArrayList<Map<String, String>> =
//                    document.get("mapList") as ArrayList<Map<String, String>>
//                val remove = mapList[pos]
//                val replace = MapItemList(
//                    title,
//                    mapList[pos].getValue("mapSort"),
//                    mapList[pos].getValue("previewImage")
//                )
//                myRef.update("mapList", FieldValue.arrayRemove(mapList[pos]))
//                myRef.update("mapList", FieldValue.arrayUnion(replace))
//            }
//        }
//        return getData()
//    }
}