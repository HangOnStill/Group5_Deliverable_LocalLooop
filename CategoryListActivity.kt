package com.example.localloop

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.localloop.model.Category
import com.google.firebase.database.*

class CategoryListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var categories: ArrayList<Category>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        listView = findViewById(R.id.categoryListView)
        categories = ArrayList()
        dbRef = FirebaseDatabase.getInstance().getReference("categories")

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList())
        listView.adapter = adapter

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                adapter.clear()
                for (catSnap in snapshot.children) {
                    val cat = catSnap.getValue(Category::class.java)
                    if (cat != null) {
                        categories.add(cat)
                        adapter.add("${cat.name}: ${cat.description}")
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CategoryListActivity, "Error loading", Toast.LENGTH_SHORT).show()
            }
        })

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = categories[position]
            val intent = Intent(this, EditCategoryActivity::class.java).apply {
                putExtra("categoryId", selected.id)
                putExtra("categoryName", selected.name)
                putExtra("categoryDesc", selected.description)
            }
            startActivity(intent)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selected = categories[position]
            onDeleteCategoryClicked(selected.id!!)
            true
        }
    }

    // -- Refined Deletion Handling --
    private fun onDeleteCategoryClicked(categoryId: String) {
        canDeleteCategory(categoryId) { canDelete ->
            if (canDelete) {
                AlertDialog.Builder(this)
                    .setTitle("Delete Category?")
                    .setMessage("Are you sure you want to delete this category?")
                    .setPositiveButton("Delete") { _, _ ->
                        dbRef.child(categoryId).removeValue()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(this, "Cannot delete: Category is in use.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Checks if any event uses the category
    private fun canDeleteCategory(categoryId: String, callback: (Boolean) -> Unit) {
        val eventsRef = FirebaseDatabase.getInstance().getReference("events")
        eventsRef.orderByChild("categoryId").equalTo(categoryId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(!snapshot.exists())
                }
                override fun onCancelled(error: DatabaseError) { callback(false) }
            })
    }
}