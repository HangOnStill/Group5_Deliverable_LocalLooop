package com.example.localloop.model

data class JoinRequest(
    var id: String = "",
    var eventId: String = "",
    var attendeeId: String = "",
    var attendeeName: String? = null, // optional: display name
    var status: String = "pending"    // "pending", "accepted", "rejected"
)