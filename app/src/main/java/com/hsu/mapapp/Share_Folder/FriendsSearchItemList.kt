package com.hsu.mapapp.Share_Folder

data class FriendsSearchItemList (
    val userId : String, //연결하고자 하는 것과 이름이 일치해야함
    val uid : String
) {
    constructor():this("","")
}