package dev.mikita.bettercity.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikita.bettercity.api.ErrorResponse
import dev.mikita.bettercity.repository.IssueRepository
import dev.mikita.bettercity.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val userRepository: UserRepository
    ) : ViewModel() {

    private val errorLiveData = MutableLiveData<ErrorResponse>()
    private val reservationLiveData = MutableLiveData<ReservationData>()
    private var fetchJob: Job? = null

    fun getReservationLiveData() = reservationLiveData
    fun getErrorLiveData() = errorLiveData

    fun loadData(id: Long) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                when (val reservation = issueRepository.getIssueReservation(id)) {
                    is NetworkResponse.Success -> {
                        when (val service = userRepository.getService(reservation.body.serviceId)) {
                            is NetworkResponse.Success -> {
                                val reservationData = ReservationData(
                                    creationDate = reservation.body.creationDate,
                                    service = ServiceData(
                                        uid = service.body.uid,
                                        photo = service.body.photo,
                                        name = service.body.name
                                    )
                                )
                                reservationLiveData.postValue(reservationData)
                            }
                            is NetworkResponse.Error -> {
                                Timber.e(service.error)
                                errorLiveData.postValue(service.body)
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(reservation.error)
                        errorLiveData.postValue(reservation.body)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    data class ReservationData(
        val creationDate: Date,
        val service: ServiceData,
    )

    data class ServiceData(
        val uid: String,
        val photo: String,
        val name: String
    )
}