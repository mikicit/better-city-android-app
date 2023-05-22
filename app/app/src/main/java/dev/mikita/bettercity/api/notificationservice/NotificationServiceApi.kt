package dev.mikita.bettercity.api.notificationservice

import com.haroldadmin.cnradapter.NetworkResponse
import dev.mikita.bettercity.api.ErrorResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface NotificationServiceApi {
    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/notifications")
    suspend fun getNotifications(): NetworkResponse<Notifications, ErrorResponse>
}