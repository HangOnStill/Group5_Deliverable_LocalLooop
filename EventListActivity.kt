// EventListActivity.kt
package com.example.localloop

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.Event
import com.google.firebase.auth.FirebaseAuth

class EventListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private val eventList = mutableListOf<Event>()
    private val currentUserId get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        recyclerView = findViewById(R.id.eventRecyclerView)
        adapter = EventAdapter(eventList,
            onEdit = { event ->
                val intent = Intent(this, EditEventActivity::class.java)
                intent.putExtra("eventId", event.id)
                startActivity(intent)
            },
            onDelete = { event ->
                EventRepository.deleteEvent(event.id!!) { success ->
                    Toast.makeText(this,
                        if (success) "Deleted!" else "Delete failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onDetail = { event ->
                val intent = Intent(this, EventDetailsActivity::class.java)
                intent.putExtra("eventId", event.id)
                startActivity(intent)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load events for this organizer
        EventRepository.getEventsForOrganizer(currentUserId!!) { events ->
            eventList.clear()
            eventList.addAll(events)
            adapter.notifyDataSetChanged()
        }
    }
}
