package com.example.localloop.model

data class Event(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val categoryId: String = "", // Reference to Category
    val organizerId: String = "", // Reference to User (Organizer)
    val fee: Double = 0.0,
    val dateTime: Long = 0L, // Store as timestamp
    val imageUrl: String? = null // Optional, for event photo
)