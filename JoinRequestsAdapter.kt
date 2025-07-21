package com.example.localloop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.JoinRequest
import java.util.Locale

class JoinRequestsAdapter(
    private val requests: List<JoinRequest>,
    private val canManage: Boolean,
    private val onAccept: (JoinRequest) -> Unit,
    private val onReject: (JoinRequest) -> Unit
) : RecyclerView.Adapter<JoinRequestsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val attendeeNameText: TextView = view.findViewById(R.id.attendeeNameText)
        val requestStatusText: TextView = view.findViewById(R.id.requestStatusText)
        val acceptBtn: Button = view.findViewById(R.id.acceptBtn)
        val rejectBtn: Button = view.findViewById(R.id.rejectBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_join_request, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = requests.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val req = requests[position]
        holder.attendeeNameText.text = req.attendeeName ?: req.attendeeId
        holder.requestStatusText.text = req.status.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
        holder.acceptBtn.isEnabled = canManage && req.status == "pending"
        holder.rejectBtn.isEnabled = canManage && req.status == "pending"
        holder.acceptBtn.setOnClickListener { onAccept(req) }
        holder.rejectBtn.setOnClickListener { onReject(req) }
    }
}
