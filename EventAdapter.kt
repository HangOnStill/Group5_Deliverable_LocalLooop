package com.example.localloop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.Event
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import androidx.recyclerview.widget.DiffUtil


class EventAdapter(
    private val onDelete: (Event) -> Unit,
    private val onDetail: (Event) -> Unit,
    private val onJoinRequest: (Event) -> Unit,
    private val userRole: String
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.eventNameText)
        val descText: TextView = view.findViewById(R.id.eventDescText)
        val dateText: TextView = view.findViewById(R.id.eventDateText)
        val feeText: TextView = view.findViewById(R.id.eventFeeText)
        val editBtn: Button = view.findViewById(R.id.editEventBtn)
        val deleteBtn: Button = view.findViewById(R.id.deleteEventBtn)
        val detailsBtn: Button = view.findViewById(R.id.detailsEventBtn)
        val imageView: ImageView = view.findViewById(R.id.eventImageView)
        val joinRequestBtn: Button = view.findViewById(R.id.joinRequestBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item_layout, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.nameText.text = event.name
        holder.descText.text = event.description
        // If dateTime is Long, convert it to readable date string
        holder.dateText.text = try {
            val date = Date(event.dateTime)
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            event.dateTime.toString()
        }
        "Fee: ${event.fee}".also { holder.feeText.text = it }

        if (!event.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(event.imageUrl).into(holder.imageView)
        } else {
            holder.imageView.setImageResource(android.R.color.darker_gray) // fallback
        }

        // Button visibility by role
        when (userRole) {
            "Participant" -> {
                holder.editBtn.visibility = View.GONE
                holder.deleteBtn.visibility = View.GONE
                holder.joinRequestBtn.visibility = View.VISIBLE
            }
            "Organizer" -> {
                holder.editBtn.visibility = View.VISIBLE
                holder.deleteBtn.visibility = View.VISIBLE
                holder.joinRequestBtn.visibility = View.GONE
            }
            else -> {
                holder.editBtn.visibility = View.VISIBLE
                holder.deleteBtn.visibility = View.VISIBLE
                holder.joinRequestBtn.visibility = View.VISIBLE
            }
        }

        holder.deleteBtn.setOnClickListener { onDelete(event) }
        holder.detailsBtn.setOnClickListener { onDetail(event) }
        holder.joinRequestBtn.setOnClickListener { onJoinRequest(event) }
        holder.editBtn.setOnClickListener { onDetail(event) } // You may want separate logic for editing
    }
}

// Add this to the same file or a separate file

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem == newItem
}
