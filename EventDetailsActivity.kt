package com.example.localloop

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        val eventId = intent.getStringExtra("eventId")
        if (eventId != null) {
            EventRepository.getEventById(eventId) { event ->
                if (event != null) {
                    // Find and fill your views with event details, e.g.:
                    findViewById<TextView>(R.id.eventNameText).text = event.name
                    // ... etc for description, date, image, fee, etc.
                }
            }
        }
    }
}
