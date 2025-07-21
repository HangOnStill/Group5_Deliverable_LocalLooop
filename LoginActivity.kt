package com.example.localloop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.localloop.model.User
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- Admin Login ---
            if (username == "admin" && password == "XPI76SZUqyCjVxgnUjm0") {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("username", "admin")
                intent.putExtra("role", "Admin")
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            // --- Organizer / Participant Login ---
            val usersRef = FirebaseDatabase.getInstance().getReference("users")
            usersRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var found = false
                        for (child in snapshot.children) {
                            val user = child.getValue(User::class.java)
                            if (user != null && user.password == password) {
                                found = true
                                if (!user.active) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Account disabled. Contact admin.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                }
                                // Route by role
                                when (user.role) {
                                    "Organizer" -> {
                                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                        intent.putExtra("username", user.username)
                                        intent.putExtra("role", user.role)
                                        startActivity(intent)
                                        finish()
                                    }
                                    "Participant" -> {
                                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                        intent.putExtra("username", user.username)
                                        intent.putExtra("role", user.role)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        Toast.makeText(this@LoginActivity, "Unknown user role", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                break
                            }
                        }
                        if (!found) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid credentials",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Database error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
