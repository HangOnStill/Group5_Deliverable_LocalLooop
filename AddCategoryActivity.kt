package com.example.localloop

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.localloop.model.Category
import com.google.firebase.database.*
import androidx.appcompat.widget.Toolbar

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var descInput: EditText
    private lateinit var addBtn: Button
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        nameInput = findViewById(R.id.categoryNameInput)
        descInput = findViewById(R.id.categoryDescInput)
        addBtn = findViewById(R.id.addCategoryBtn)
        databaseRef = FirebaseDatabase.getInstance().getReference("categories")

        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        addBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val description = descInput.text.toString().trim()

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Both fields are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prevent duplicate category names
            databaseRef.orderByChild("name").equalTo(name)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(
                                this@AddCategoryActivity,
                                "Category name already exists.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val id = databaseRef.push().key
                            if (id != null) {
                                val category = Category(id, name, description)
                                databaseRef.child(id).setValue(category)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@AddCategoryActivity,
                                            "Category added.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@AddCategoryActivity,
                                            "Failed: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    this@AddCategoryActivity,
                                    "Error generating ID.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@AddCategoryActivity,
                            "Database error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
