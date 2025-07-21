package com.example.localloop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.Event

class EventModerationAdapter(
    private val events: List<Event>,
    val onApprove: (Event) -> Unit,
    val onReject: (Event) -> Unit,
    val onDelete: (Event) -> Unit
) : RecyclerView.Adapter<EventModerationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.eventName)
        val approveBtn: Button = view.findViewById(R.id.btnApprove)
        val rejectBtn: Button = view.findViewById(R.id.btnReject)
        val deleteBtn: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_moderation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.name.text = event.name
        holder.approveBtn.setOnClickListener { onApprove(event) }
        holder.rejectBtn.setOnClickListener { onReject(event) }
        holder.deleteBtn.setOnClickListener { onDelete(event) }
    }
}
