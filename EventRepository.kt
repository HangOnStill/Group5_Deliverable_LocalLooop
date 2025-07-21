package com.example.localloop

import com.example.localloop.model.Event
import com.google.firebase.database.*

object EventRepository {

    private val db = FirebaseDatabase.getInstance().getReference("events")

    // Add Event
    fun addEvent(event: Event, onResult: (Boolean) -> Unit) {
        val key = event.id
        event.id = key
        db.child(key).setValue(event)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Update Event
    fun updateEvent(event: Event, onResult: (Boolean) -> Unit) {
        db.child(event.id).setValue(event)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Delete Event
    fun deleteEvent(eventId: String, onResult: (Boolean) -> Unit) {
        db.child(eventId).removeValue()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Fetch all events for a specific organizer (returns once)
    fun getEventsForOrganizer(organizerId: String, onResult: (List<Event>) -> Unit) {
        db.orderByChild("organizerId").equalTo(organizerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val myEvents = mutableListOf<Event>()
                    for (child in snapshot.children) {
                        val event = child.getValue(Event::class.java)
                        if (event != null) myEvents.add(event)
                    }
                    onResult(myEvents)
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    // Optionally: Fetch a single event by its id
    fun getEventById(eventId: String, onResult: (Event?) -> Unit) {
        db.child(eventId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onResult(snapshot.getValue(Event::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }
}