package com.hsu.mapapp.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.internal.MagicApiIntrinsics

class MapViewModel : ViewModel() {
    val mapRepository = MapRepository()
    var mapData = ArrayList<MapItemList>()
    var mapLiveData: MutableLiveData<ArrayList<MapItemList>> =
        MutableLiveData()

    init {
        mapData = mapRepository.getData()
        mapLiveData.postValue(mapData)
        println("mapViewModel: $mapData")
    }

    fun addMap(mapItem: MapItemList) {
        mapData.clear()
        mapData = mapRepository.addData(mapItem)
        mapLiveData.postValue(mapData)
    }

    fun deleteMap(pos: Int) {
        mapData.clear()
        mapData = mapRepository.deleteData(pos)
        mapLiveData.postValue(mapData)
    }

    fun editMapTitle(pos: Int, title: String) {
        mapData.clear()
        mapData = mapRepository.editMapTitle(pos, title)
        mapLiveData.postValue(mapData)
    }
}
