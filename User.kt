package com.example.localloop.model

data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var role: String = "",
    var active: Boolean = true
)
