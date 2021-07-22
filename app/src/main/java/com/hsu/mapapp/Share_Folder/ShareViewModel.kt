package com.hsu.mapapp.Share_Folder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareViewModel : ViewModel() {
    val shareLiveData:MutableLiveData<String> = MutableLiveData<String>()
    init{
        shareLiveData.value=""
    }

    fun setShareText(a:String){
        shareLiveData.value=a
    }
}