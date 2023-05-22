package dev.mikita.bettercity.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikita.bettercity.api.ErrorResponse
import dev.mikita.bettercity.repository.NotificationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel
    @Inject constructor(private val repository: NotificationRepository) : ViewModel() {
    private val errorLiveData = MutableLiveData<ErrorResponse>()
    private val notificationLiveData = MutableLiveData<List<Notification>?>()
    private var fetchJob: Job? = null

    fun getNotification() = notificationLiveData
    fun getErrorLiveData() = errorLiveData

    fun loadNotifications() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                when (val notifications = repository.getNotifications()) {
                    is NetworkResponse.Success -> {
                        val notificationList = notifications.body.map { (creationDate, description) ->
                            Notification(creationDate, description)
                        }
                        notificationLiveData.postValue(notificationList)
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(notifications.error)
                        errorLiveData.postValue(notifications.body)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    data class Notification(
        val creationDate: Date,
        val description: String
    )
}