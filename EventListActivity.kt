package com.example.localloop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.Category
import com.example.localloop.model.Event
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class EventListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var searchView: SearchView
    private lateinit var addEventBtn: Button

    private var allEvents: List<Event> = emptyList()
    private var categoryMap: Map<String, Category> = emptyMap()

    // Get user info from Intent (passed by LoginActivity)
    private val currentUserId: String? by lazy { intent.getStringExtra("userId") }
    private val currentUserRole: String? by lazy { intent.getStringExtra("role") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        // View binding
        searchView = findViewById(R.id.eventSearchView)
        recyclerView = findViewById(R.id.eventRecyclerView)
        addEventBtn = findViewById(R.id.addEventBtn) // Make sure to add this to XML

        // Hide addEventBtn for Participant
        addEventBtn.visibility = if (currentUserRole == "Organizer") View.VISIBLE else View.GONE

        addEventBtn.setOnClickListener {
            startActivity(Intent(this, AddEditEventActivity::class.java))
        }

        // Set up RecyclerView and adapter
        adapter = EventAdapter(
            onDelete = { event ->
                if (event.organizerId == currentUserId && currentUserRole == "Organizer") {
                    // Only organizer can delete their own events
                    // TODO: Implement EventRepository.deleteEvent()
                    Toast.makeText(this, "Delete event not implemented", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "You cannot delete this event", Toast.LENGTH_SHORT).show()
                }
            },
            onDetail = { event ->
                val intent = Intent(this, EventDetailsActivity::class.java)
                intent.putExtra("eventId", event.id)
                startActivity(intent)
            },
            onJoinRequest = { event ->
                if (currentUserRole == "Participant" && currentUserId != event.organizerId) {
                    val joinReqRef = FirebaseDatabase.getInstance()
                        .getReference("joinRequests")
                        .child(event.id)
                    joinReqRef.orderByChild("attendeeId").equalTo(currentUserId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(this@EventListActivity, "Already requested to join", Toast.LENGTH_SHORT).show()
                                } else {
                                    val reqId = joinReqRef.push().key!!
                                    val req = mapOf("attendeeId" to currentUserId, "status" to "pending")
                                    joinReqRef.child(reqId).setValue(req)
                                        .addOnSuccessListener {
                                            Toast.makeText(this@EventListActivity, "Request sent!", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@EventListActivity, "Failed to send request.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            },
            userRole = currentUserRole ?: "Participant" // default role
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load categories, then events
        loadCategories { cats ->
            categoryMap = cats.associateBy { it.id ?: "" }
            reloadEvents()
        }

        // Search bar logic
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterEvents(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterEvents(newText)
                return true
            }
        })
    }

    private fun loadCategories(onLoaded: (List<Category>) -> Unit) {
        FirebaseDatabase.getInstance().getReference("categories")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cats = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                    onLoaded(cats)
                }
                override fun onCancelled(error: DatabaseError) {
                    onLoaded(emptyList())
                }
            })
    }

    private fun reloadEvents() {
        val ref = FirebaseDatabase.getInstance().getReference("events")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                allEvents = if (currentUserRole == "Organizer") {
                    events.filter { it.organizerId == currentUserId }
                } else {
                    events
                }
                adapter.submitList(allEvents)
            }
            override fun onCancelled(error: DatabaseError) {
                allEvents = emptyList()
                adapter.submitList(allEvents)
            }
        })
    }

    private fun filterEvents(query: String?) {
        val filtered = allEvents.filter { event ->
            val nameMatches = event.name.contains(query ?: "", ignoreCase = true)
            val categoryMatches = categoryMap[event.categoryId]?.name?.contains(query ?: "", ignoreCase = true) == true
            nameMatches || categoryMatches
        }
        adapter.submitList(filtered)
    }
}
