package com.example.localloop

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.JoinRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class JoinRequestsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JoinRequestsAdapter
    private val joinRequestList = mutableListOf<JoinRequest>()
    private var eventId: String = ""
    private var eventOrganizerId: String = ""

    private val currentUserId get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_requests)

        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        recyclerView = findViewById(R.id.joinRequestsRecyclerView)

        eventId = intent.getStringExtra("eventId") ?: ""
        eventOrganizerId = intent.getStringExtra("eventOrganizerId") ?: ""

        adapter = JoinRequestsAdapter(
            joinRequestList,
            canManage = (currentUserId == eventOrganizerId),
            onAccept = { req -> updateJoinRequest(eventId, req.id, "accepted") },
            onReject = { req -> updateJoinRequest(eventId, req.id, "rejected") }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadJoinRequests()
    }

    private fun loadJoinRequests() {
        FirebaseDatabase.getInstance().getReference("joinRequests")
            .child(eventId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    joinRequestList.clear()
                    for (child in snapshot.children) {
                        val req = child.getValue(JoinRequest::class.java)
                        if (req != null) {
                            req.id = child.key ?: ""
                            joinRequestList.add(req)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@JoinRequestsActivity, "Error loading requests", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateJoinRequest(eventId: String, reqId: String, newStatus: String) {
        FirebaseDatabase.getInstance().getReference("joinRequests")
            .child(eventId).child(reqId).child("status").setValue(newStatus)
    }
}
