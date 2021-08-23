package com.hsu.mapapp.Share_Folder

import androidx.lifecycle.ViewModel

class ShareViewModel : ViewModel() {
    //val shareLiveData:MutableLiveData<String> = MutableLiveData<String>()
    private var ver: String? = null
    private val name: String? = null
    private val api: String? = null

    fun getVer(): String? {
        return ver
    }

    fun getName(): String? {
        return name
    }

    fun getApi(): String? {
        return api
    }

    /*init{
       // shareLiveData.value=""
    }

    fun setShareText(a:String){
        //shareLiveData.value=a
    }*/
}