package dev.mikita.bettercity.api.notificationservice

import java.util.Date

class Notifications : ArrayList<Notification>()

data class Notification(
    val creationDate: Date,
    val description: String
)