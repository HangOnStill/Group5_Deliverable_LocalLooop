package com.example.localloop

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
            dbRef.child(selected.id!!).removeValue()
            true
        }
    }
}
