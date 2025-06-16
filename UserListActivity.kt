package com.example.localloop

import android.os.Bundle
import android.widget.Toast
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.User
import com.google.firebase.database.*

class UserListActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = ArrayList()
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
                userList.clear()
                for (userSnap in snapshot.children) {
                    val user = userSnap.getValue(User::class.java)
                    if (user != null) {
                        if (Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
                            userList.add(user)
                        } else {
                            Toast.makeText(
                                this@UserListActivity,
                                "Invalid email found for user: ${user.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                adapter.submitList(userList.toList())
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserListActivity, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
