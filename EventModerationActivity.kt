package com.example.localloop

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.Event
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class EventModerationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventModerationAdapter
    private val events = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_moderation)

        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        recyclerView = findViewById(R.id.eventsRecyclerView)
        adapter = EventModerationAdapter(events,
            onApprove = { event -> updateEventStatus(event, "approved") },
            onReject = { event -> updateEventStatus(event, "rejected") },
            onDelete = { event -> deleteEvent(event) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchPendingEvents()
    }

    private fun fetchPendingEvents() {
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.orderByChild("status").equalTo("pending")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    events.clear()
                    for (child in snapshot.children) {
                        val event = child.getValue(Event::class.java)
                        if (event != null) events.add(event)
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateEventStatus(event: Event, status: String) {
        val ref = FirebaseDatabase.getInstance().getReference("events").child(event.id)
        ref.child("status").setValue(status)
            .addOnSuccessListener {
                Toast.makeText(this, "Event $status", Toast.LENGTH_SHORT).show()
                fetchPendingEvents()
            }
    }

    private fun deleteEvent(event: Event) {
        val ref = FirebaseDatabase.getInstance().getReference("events").child(event.id)
        ref.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show()
                fetchPendingEvents()
            }
    }
}