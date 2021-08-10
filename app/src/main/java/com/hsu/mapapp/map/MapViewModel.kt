package com.hsu.mapapp.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.jvm.internal.MagicApiIntrinsics

data class MapItemList(
    var mapTitle: String
)

class MapViewModel : ViewModel() {
    var mapLiveData: MutableLiveData<ArrayList<MapItemList>> =
        MutableLiveData()
    var mapData = ArrayList<MapItemList>()

    init {
        mapData.add(MapItemList("Map1"))
        mapData.add(MapItemList("Map2"))
        mapData.add(MapItemList("Map4"))
        mapLiveData.postValue(mapData)
    }

    fun addMap(mapItem: MapItemList) {
        mapData.add(mapItem)
        mapLiveData.value = mapData
    }

    fun deleteMap(pos: Int) {
        mapData.removeAt(pos)
    }

    fun changeMapTitle(pos: Int, title: String) {
        mapData[pos].mapTitle = title
        Log.d("mapChange", title)
    }
}
