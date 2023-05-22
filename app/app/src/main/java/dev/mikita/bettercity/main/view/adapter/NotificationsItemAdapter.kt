package dev.mikita.bettercity.main.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.mikita.bettercity.R
import dev.mikita.bettercity.main.viewmodel.NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class NotificationsItemAdapter @Inject constructor():
    RecyclerView.Adapter<NotificationsItemAdapter.NotificationItemViewHolder>() {

    // Data
    var notifications: List<NotificationsViewModel.Notification> = emptyList()

    // Utils
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    class NotificationItemViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
        val notificationDescription: TextView = view!!.findViewById(R.id.notification_description)
        val notificationDate: TextView = view!!.findViewById(R.id.notification_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)

        return NotificationItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationItemViewHolder, position: Int) {
        val item = notifications[position]

        holder.notificationDescription.text = item.description
        holder.notificationDate.text = dateFormat.format(item.creationDate)
    }
}