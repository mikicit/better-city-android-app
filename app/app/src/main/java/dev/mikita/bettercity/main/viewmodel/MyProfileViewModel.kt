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
class MyProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
    ) : ViewModel() {

    private val errorLiveData = MutableLiveData<ErrorResponse>()
    private val residentLiveData = MutableLiveData<ResidentData>()
    private var fetchJob: Job? = null

    fun getResidentLiveData() = residentLiveData
    fun getErrorLiveData() = errorLiveData

    fun loadData() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                when (val residentResponse = userRepository.getCurrentResident()) {
                    is NetworkResponse.Success -> {
                        val resident = residentResponse.body
                        when (val issuesCountResponse =
                            userRepository.getCurrentResidentIssuesCount()) {
                            is NetworkResponse.Success -> {
                                val issuesCount = issuesCountResponse.body.count
                                val residentData = ResidentData(
                                    uid = resident.uid,
                                    photo = resident.photo,
                                    firstName = resident.firstName,
                                    lastName = resident.lastName,
                                    email = resident.email,
                                    issuesCount = issuesCount
                                )
                                residentLiveData.postValue(residentData)
                            }
                            is NetworkResponse.Error -> {
                                Timber.e(issuesCountResponse.error)
                                errorLiveData.postValue(issuesCountResponse.body)
                            }
                        }
                    }

                    is NetworkResponse.Error -> {
                        Timber.e(residentResponse.error)
                        errorLiveData.postValue(residentResponse.body)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    data class ResidentData(
        val uid: String,
        val photo: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val issuesCount: Int
    )
}