package com.hsu.mapapp.map

import com.hsu.mapapp.R

data class MapItemList (
    val mapTitle: String,
    val previewImage: String = R.drawable.base_map.toString(),
    val mapSort: String = "korea"
        )
