package com.example.localloop

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class AdminWelcomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_welcome)

        val userListView = findViewById<ListView>(R.id.userListView)
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val userList = ArrayList<String>()
        val cursor = db.rawQuery("SELECT username, role FROM users", null)
        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val role = cursor.getString(1)
            userList.add("$name ($role)")
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
        userListView.adapter = adapter
    }
}