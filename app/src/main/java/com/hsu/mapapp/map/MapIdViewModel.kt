package com.hsu.mapapp.map

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel


class MapIdViewModel : ViewModel() {
    val mapId = MutableLiveData<String>()
    fun setMapId(id: String) {
        mapId.value = id
    }
}