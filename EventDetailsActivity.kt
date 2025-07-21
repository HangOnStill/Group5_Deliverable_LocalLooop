package com.example.localloop

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class EventDetailsActivity : AppCompatActivity() {
    private lateinit var joinEventBtn: Button
    private lateinit var joinStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        joinEventBtn = findViewById(R.id.joinEventBtn)
        joinStatusText = findViewById(R.id.joinStatusText)

        val eventId = intent.getStringExtra("eventId")
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (eventId != null) {
            EventRepository.getEventById(eventId) { event ->
                if (event != null) {
                    // Example: Display event info
                    findViewById<TextView>(R.id.eventNameText).text = event.name
                    // ... other fields as before

                    // --------- JOIN REQUEST LOGIC -------------
                    if (currentUserId != null && event.organizerId != currentUserId) {
                        checkAndHandleJoinRequest(eventId, currentUserId)
                    } else {
                        joinEventBtn.visibility = View.GONE // Hide for organizer
                    }
                }
            }
        }
    }

    private fun checkAndHandleJoinRequest(eventId: String, attendeeId: String) {
        val joinReqRef = FirebaseDatabase.getInstance().getReference("joinRequests").child(eventId)
        joinReqRef.orderByChild("attendeeId").equalTo(attendeeId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Already requested, show status
                        joinEventBtn.visibility = View.GONE
                        for (snap in snapshot.children) {
                            val status = snap.child("status").getValue(String::class.java) ?: "pending"
                            joinStatusText.visibility = View.VISIBLE
                            joinStatusText.text = getString(R.string.join_request_status, status)

                        }
                    } else {
                        joinEventBtn.visibility = View.VISIBLE
                        joinStatusText.visibility = View.GONE
                        joinEventBtn.setOnClickListener {
                            sendJoinRequest(eventId, attendeeId)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendJoinRequest(eventId: String, attendeeId: String) {
        val joinReqRef = FirebaseDatabase.getInstance().getReference("joinRequests").child(eventId)
        val reqId = joinReqRef.push().key!!
        val req = mapOf(
            "attendeeId" to attendeeId,
            "status" to "pending"
        )
        joinReqRef.child(reqId).setValue(req)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.request_sent), Toast.LENGTH_SHORT).show()
                joinEventBtn.visibility = View.GONE
                joinStatusText.visibility = View.VISIBLE
                joinStatusText.text = getString(R.string.join_request_status, "pending")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send request.", Toast.LENGTH_SHORT).show()
            }
    }
}
