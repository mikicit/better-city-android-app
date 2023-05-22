package dev.mikita.bettercity.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikita.bettercity.api.ErrorResponse
import dev.mikita.bettercity.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val userRepository: UserRepository
    ) : ViewModel() {

    private val errorLiveData = MutableLiveData<ErrorResponse>()
    private val residentLiveData = MutableLiveData<ServiceData>()
    private var fetchJob: Job? = null

    fun getServiceLiveData() = residentLiveData
    fun getErrorLiveData() = errorLiveData

    fun loadData(uid: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                when (val service = userRepository.getService(uid)) {
                    is NetworkResponse.Success -> {
                        when (val reservationsCount = userRepository.getServiceReservationsCount(uid)) {
                            is NetworkResponse.Success -> {
                                when (val solutionsCount = userRepository.getServiceSolutionsCount(uid)) {
                                    is NetworkResponse.Success -> {
                                        val serviceData = ServiceData(
                                            uid = service.body.uid,
                                            photo = service.body.photo,
                                            name = service.body.name,
                                            description = service.body.description,
                                            reservationsCount = reservationsCount.body.count,
                                            solutionsCount = solutionsCount.body.count
                                        )
                                        residentLiveData.postValue(serviceData)
                                    }
                                    is NetworkResponse.Error -> {
                                        Timber.e(solutionsCount.error)
                                        errorLiveData.postValue(solutionsCount.body)
                                    }
                                }
                            }
                            is NetworkResponse.Error -> {
                                Timber.e(reservationsCount.error)
                                errorLiveData.postValue(reservationsCount.body)
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(service.error)
                        errorLiveData.postValue(service.body)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    data class ServiceData(
        val uid: String,
        val photo: String,
        val name: String,
        val description: String,
        val reservationsCount: Int,
        val solutionsCount: Int
    )
}