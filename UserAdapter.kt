package com.example.localloop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localloop.model.User

class UserAdapter(
    private val onAction: (User, String) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameEmailText: TextView = itemView.findViewById(R.id.nameEmailText)
        private val roleText: TextView = itemView.findViewById(R.id.roleText)
        private val statusToggleBtn: Button = itemView.findViewById(R.id.statusToggleBtn)
        private val deleteUserBtn: Button = itemView.findViewById(R.id.deleteUserBtn)

        fun bind(user: User) {
            //  Use string resource with placeholders
            nameEmailText.text = itemView.context.getString(R.string.user_info, user.username, user.email)
            roleText.text = itemView.context.getString(R.string.role_info, user.role)
            statusToggleBtn.text = if (user.active) itemView.context.getString(R.string.disable)
            else itemView.context.getString(R.string.enable)

            statusToggleBtn.setOnClickListener { onAction(user, "toggle") }
            deleteUserBtn.setOnClickListener { onAction(user, "delete") }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
