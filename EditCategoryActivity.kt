package com.example.localloop

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.localloop.model.Category
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.widget.Toolbar

class EditCategoryActivity : AppCompatActivity() {

    private var categoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        // Standard Toolbar setup for all Activities
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow
        supportActionBar?.title = "Screen Title" // Set dynamically if needed

        val nameInput = findViewById<EditText>(R.id.editCategoryName)
        val descInput = findViewById<EditText>(R.id.editCategoryDesc)
        val updateBtn = findViewById<Button>(R.id.updateCategoryBtn)

        val name = intent.getStringExtra("categoryName")
        val desc = intent.getStringExtra("categoryDesc")
        categoryId = intent.getStringExtra("categoryId")

        nameInput.setText(name)
        descInput.setText(desc)

        updateBtn.setOnClickListener {
            val updatedName = nameInput.text.toString().trim()
            val updatedDesc = descInput.text.toString().trim()

            if (updatedName.isEmpty() || updatedDesc.isEmpty()) {
                Toast.makeText(this, "Category name cannot be empty, Both fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = Category(categoryId, updatedName, updatedDesc)

            FirebaseDatabase.getInstance().getReference("categories")
                .child(categoryId!!)
                .setValue(category)
                .addOnSuccessListener {
                    Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                }
        }
    }
}