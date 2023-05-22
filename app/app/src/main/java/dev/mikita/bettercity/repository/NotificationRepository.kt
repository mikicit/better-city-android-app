package dev.mikita.bettercity.repository

import dev.mikita.bettercity.api.notificationservice.NotificationServiceApi

class NotificationRepository(private val notificationServiceApi: NotificationServiceApi) {
    suspend fun getNotifications() = notificationServiceApi.getNotifications()
}