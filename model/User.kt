package com.example.localloop.model

data class User(
    val uid: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val role: String = "", // "admin", "organizer", "participant"
    val active: Boolean = true
)
