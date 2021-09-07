package com.hsu.mapapp.friend_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FriendMapViewFactory(private val uid: String) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FriendMapViewModel::class.java)){
            return FriendMapViewModel(uid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}