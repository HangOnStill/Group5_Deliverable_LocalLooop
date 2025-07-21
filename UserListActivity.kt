package com.example.localloop

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.User
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class UserListActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed
        // Set up Toolbar (make sure you have one in your XML)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Users"

        // Toolbar back button
        // (works if you call setSupportActionBar above)
        // Handles back navigation
        overridePendingTransition(0, 0)

        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter { user, action ->
            if (action == "delete") {
                dbRef.child(user.uid).removeValue()
            } else if (action == "toggle") {
                dbRef.child(user.uid).child("active").setValue(!user.active)
            }
        }
        recyclerView.adapter = adapter

        dbRef = FirebaseDatabase.getInstance().getReference("users")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<User>()
                for (userSnap in snapshot.children) {
                    val user = userSnap.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                adapter.submitList(userList)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserListActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })

        // If you want a physical back button:
        findViewById<android.widget.Button>(R.id.backButton)?.setOnClickListener { finish() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
