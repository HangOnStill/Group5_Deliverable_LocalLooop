package com.example.localloop

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_dashboard_activity)

        val userListView = findViewById<ListView>(R.id.userListView)

        val dummyUsers = arrayListOf(
            "Alice (Organizer)",
            "Bob (Participant)",
            "Charlie (Organizer)"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            dummyUsers
        )

        userListView.adapter = adapter
    }
}
