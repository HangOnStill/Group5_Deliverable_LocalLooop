package com.example.localloop

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class WelcomeActivity : AppCompatActivity() {
    private lateinit var welcomeText: TextView
    private lateinit var userListView: ListView
    private lateinit var refreshButton: Button
    private lateinit var contentText: TextView // For Organizer/Participant
    private val userList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed
        // Bind views
        welcomeText = findViewById(R.id.welcomeText)
        userListView = findViewById(R.id.userListView)
        refreshButton = findViewById(R.id.refreshUsersBtn)
        contentText = findViewById(R.id.contentText)

        // Get name and role from intent
        val name = intent.getStringExtra("name") ?: "User"
        val role = intent.getStringExtra("role") ?: "Participant"

        // Set welcome message
        "Welcome $name! You are logged in as \"$role\".".also { welcomeText.text = it }

        // Setup user list for admin
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        userListView.adapter = adapter

        if (role.equals("admin", ignoreCase = true)) {
            // Admin: show user list
            userListView.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            contentText.visibility = View.GONE
            loadUsers()

            refreshButton.setOnClickListener { loadUsers() }
        } else {
            // Organizer/Participant: show info, hide user list
            userListView.visibility = View.GONE
            refreshButton.visibility = View.GONE
            contentText.visibility = View.VISIBLE

            contentText.text = when (role.lowercase()) {
                "organizer" -> "As an Organizer, you can create and manage events!"
                else        -> "As a Participant, you can browse and join community events!"
            }
        }
    }

    private fun loadUsers() {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (child in snapshot.children) {
                    val name = child.child("name").getValue(String::class.java) ?: "Unknown"
                    val role = child.child("role").getValue(String::class.java) ?: "Unknown"
                    userList.add("$name ($role)")
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@WelcomeActivity,
                    "Failed to load users: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
