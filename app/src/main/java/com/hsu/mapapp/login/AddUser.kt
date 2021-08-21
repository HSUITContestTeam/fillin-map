package com.hsu.mapapp.login

data class AddUser(
    var userId: String? = null, // 유저 email
    var uid: String? = null, // 유저 고유 코드
    var name: String? = null,
    var photoUrl: String? = null
) {

}