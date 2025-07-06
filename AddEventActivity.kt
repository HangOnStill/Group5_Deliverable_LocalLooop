package com.example.localloop

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.localloop.model.Category
import com.example.localloop.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddEditEventActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var eventId: String? = null
    private var eventDateMillis: Long = 0L
    private val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_event)

        val nameInput = findViewById<EditText>(R.id.eventNameInput)
        val descInput = findViewById<EditText>(R.id.eventDescInput)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val feeInput = findViewById<EditText>(R.id.eventFeeInput)
        val dateBtn = findViewById<Button>(R.id.dateTimeBtn)
        val selectedDateText = findViewById<TextView>(R.id.selectedDateText)
        val selectImageBtn = findViewById<Button>(R.id.selectImageBtn)
        val eventImageView = findViewById<ImageView>(R.id.eventImageView)
        val saveBtn = findViewById<Button>(R.id.saveEventBtn)

        // -- CATEGORY SPINNER --
        val categoryAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        FirebaseDatabase.getInstance().getReference("categories")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    categories.clear()
                    categoryAdapter.clear()
                    for (catSnap in snapshot.children) {
                        val cat = catSnap.getValue(Category::class.java)
                        if (cat != null) {
                            categories.add(cat)
                            categoryAdapter.add(cat.name ?: "")
                        }
                    }
                    categoryAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        // -- DATE/TIME PICKER --
        dateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                TimePickerDialog(this, { _, h, min ->
                    calendar.set(y, m, d, h, min)
                    eventDateMillis = calendar.timeInMillis
                    val formatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(eventDateMillis))
                    selectedDateText.text = formatted
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // -- IMAGE PICKER --
        selectImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1002)
        }

        // -- LOAD EVENT FOR EDIT --
        eventId = intent.getStringExtra("eventId")
        if (eventId != null) {
            EventRepository.getEventById(eventId!!) { event ->
                if (event != null) {
                    nameInput.setText(event.name)
                    descInput.setText(event.description)
                    feeInput.setText(event.fee?.toString() ?: "")
                    eventDateMillis = event.dateTime
                    selectedDateText.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(event.dateTime))
                    if (!event.imageUrl.isNullOrEmpty()) {
                        // Load image with Picasso/Glide if available, otherwise skip
                        // Picasso.get().load(event.imageUrl).into(eventImageView)
                    }
                    val idx = categories.indexOfFirst { it.id == event.categoryId }
                    if (idx >= 0) categorySpinner.setSelection(idx)
                }
            }
        }

        // -- SAVE EVENT --
        saveBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val desc = descInput.text.toString().trim()
            val fee = feeInput.text.toString().toDoubleOrNull() ?: 0.0
            val categoryIdx = categorySpinner.selectedItemPosition
            if (name.isEmpty() || desc.isEmpty() || categoryIdx < 0 || eventDateMillis == 0L) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedCat = categories[categoryIdx]
            val organizerId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            fun saveEventWithImage(imageUrl: String?) {
                val event = Event(
                    id = eventId ?: "",
                    name = name,
                    description = desc,
                    categoryId = selectedCat.id ?: "",
                    organizerId = organizerId,
                    fee = fee,
                    dateTime = eventDateMillis,
                    imageUrl = imageUrl
                )
                if (eventId == null) {
                    EventRepository.addEvent(event) { ok ->
                        Toast.makeText(this, if (ok) "Added!" else "Error", Toast.LENGTH_SHORT).show()
                        if (ok) finish()
                    }
                } else {
                    EventRepository.updateEvent(event) { ok ->
                        Toast.makeText(this, if (ok) "Updated!" else "Error", Toast.LENGTH_SHORT).show()
                        if (ok) finish()
                    }
                }
            }

            if (imageUri != null) {
                val ref = FirebaseStorage.getInstance().reference.child("eventImages/${UUID.randomUUID()}.jpg")
                ref.putFile(imageUri!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            saveEventWithImage(uri.toString())
                        }
                    }
                    .addOnFailureListener { saveEventWithImage(null) }
            } else {
                saveEventWithImage(null)
            }
        }
    }

    @Deprecated("Use Activity Result API in new code")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            findViewById<ImageView>(R.id.eventImageView).setImageURI(imageUri)
        }
    }
}