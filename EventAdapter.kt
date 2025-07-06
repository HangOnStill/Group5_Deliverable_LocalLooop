// EventAdapter.kt
package com.example.localloop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.Event
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class EventAdapter(
    private val events: List<Event>,
    private val onEdit: (Event) -> Unit,
    private val onDelete: (Event) -> Unit,
    private val onDetail: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.eventNameText)
        val descText: TextView = view.findViewById(R.id.eventDescText)
        val dateText: TextView = view.findViewById(R.id.eventDateText)
        val feeText: TextView = view.findViewById(R.id.eventFeeText)
        val editBtn: Button = view.findViewById(R.id.editEventBtn)
        val deleteBtn: Button = view.findViewById(R.id.deleteEventBtn)
        val detailsBtn: Button = view.findViewById(R.id.detailsEventBtn)
        val imageView: ImageView = view.findViewById(R.id.eventImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item_layout, parent, false)
        return EventViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.nameText.text = event.name
        holder.descText.text = event.description

        // Format date
        val date = Date(event.dateTime)
        val formatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
        holder.dateText.text = formatted
        holder.feeText.text = "Fee: $${event.fee ?: 0.0}"

        // Load image if available
        if (!event.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(event.imageUrl).into(holder.imageView)
        }

        holder.editBtn.setOnClickListener { onEdit(event) }
        holder.deleteBtn.setOnClickListener { onDelete(event) }
        holder.detailsBtn.setOnClickListener { onDetail(event) }
    }
}
