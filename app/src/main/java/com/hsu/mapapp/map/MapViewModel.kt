package com.hsu.mapapp.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.jvm.internal.MagicApiIntrinsics

class MapViewModel : ViewModel() {
    private val mapRepository = MapRepository()
    private val mutableData = MutableLiveData<MutableList<MapItemList>>()

    fun fetchData(): LiveData<MutableList<MapItemList>> {
        mapRepository.getData().observeForever {
            mutableData.value = it
        }
        return mutableData
    }

    init {
        mapRepository.getData().observeForever {
            mutableData.value = it
        }
    }

    fun addMap(mapItem: MapItemList) {
        mapRepository.addData(mapItem)
    }

    fun deleteMap(pos: Int) {
        mapRepository.deleteData(pos)
    }

    fun changeMapTitle(pos: Int, title: String) {
        mapRepository.editMapTitle(pos, title)
        Log.d("mapChange", title)
    }
}
