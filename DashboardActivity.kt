package com.example.localloop

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val infoListView = findViewById<ListView>(R.id.infoListView)

        // Get data from Intent
        val username = intent.getStringExtra("username") ?: "Unknown"
        val role = intent.getStringExtra("role") ?: "Participant"

        // Welcome message
        welcomeText.text = getString(R.string.welcome_you_are_logged_in_as, username, role)

        // Populate list with options based on role
        val options = when (role.lowercase()) {
            "admin" -> listOf("Manage categories", "View/disable/delete users", "Review submitted events")
            "organizer" -> listOf("Create event", "View your events", "Manage join requests")
            "participant" -> listOf("Browse and join events", "My joined events")
            else -> listOf("Unknown role")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            options
        )
        infoListView.adapter = adapter

        // Handle click events for each option
        infoListView.setOnItemClickListener { _, _, position, _ ->
            when (role.lowercase()) {
                "admin" -> when (position) {
                    0 -> startActivity(Intent(this, CategoryListActivity::class.java))
                    1 -> startActivity(Intent(this, UserListActivity::class.java))
                    2 -> startActivity(Intent(this, EventModerationActivity::class.java)) // If you have this activity
                }
                "organizer" -> when (position) {
                    0 -> startActivity(Intent(this, AddEditEventActivity::class.java))
                    1 -> startActivity(Intent(this, EventListActivity::class.java))
                    2 -> startActivity(Intent(this, JoinRequestsActivity::class.java))
                }
                "participant" -> when (position) {
                    0 -> startActivity(Intent(this, EventListActivity::class.java)) // Browse events
                    1 -> startActivity(Intent(this, MyEventsActivity::class.java))   // Joined events (implement as needed)
                }
                else -> {
                    Toast.makeText(this, "No action for this role", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
