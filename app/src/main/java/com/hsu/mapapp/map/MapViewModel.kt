package com.hsu.mapapp.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.jvm.internal.MagicApiIntrinsics

data class MapItemList(
    val mapTitle: String
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
}
