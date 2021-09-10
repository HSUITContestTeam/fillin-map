package com.hsu.mapapp.Share_Folder

data class FriendsItemList(
     val FriendsName : String,
     val uid : String,
     val photoUrl : String,
     val Message:String
        ){
    constructor():this("","","","")
}
