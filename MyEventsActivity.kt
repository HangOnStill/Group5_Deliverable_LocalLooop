package com.example.localloop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class MyEventsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)
        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        recyclerView = findViewById(R.id.myEventsRecyclerView)
        adapter = EventAdapter(
            onDelete = { event -> /* handle delete */ },
            onDetail = { event -> /* open details */ },
            onJoinRequest = { event -> /* send join request */ },
            userRole = "Participant" // or "Organizer" if needed
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadMyJoinedEvents()
    }

    private fun loadMyJoinedEvents() {
        val userId = currentUserId ?: return
        val joinRef = FirebaseDatabase.getInstance().getReference("joinRequests")
        joinRef.orderByChild("attendeeId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val joinedEventIds = snapshot.children.mapNotNull {
                        it.child("eventId").getValue(String::class.java)
                    }
                    fetchEvents(joinedEventIds)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchEvents(eventIds: List<String>) {
        if (eventIds.isEmpty()) {
            adapter.submitList(emptyList())
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Event>()
                for (child in snapshot.children) {
                    val event = child.getValue(Event::class.java)
                    if (event != null && event.id in eventIds) {
                        list.add(event)
                    }
                }
                adapter.submitList(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
