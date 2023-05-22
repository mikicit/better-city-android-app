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
class SolutionViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val userRepository: UserRepository
    ) : ViewModel() {

    private val errorLiveData = MutableLiveData<ErrorResponse>()
    private val solutionLiveData = MutableLiveData<SolutionData>()
    private var fetchJob: Job? = null

    fun getSolutionLivedata() = solutionLiveData
    fun getErrorLiveData() = errorLiveData

    fun loadData(id: Long) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                when (val solution = issueRepository.getIssueSolution(id)) {
                    is NetworkResponse.Success -> {
                        when (val service = userRepository.getService(solution.body.serviceId)) {
                            is NetworkResponse.Success -> {
                                val solutionData = SolutionData(
                                    photo = solution.body.photo,
                                    description = solution.body.description,
                                    creationDate = solution.body.creationDate,
                                    service = ServiceData(
                                        uid = service.body.uid,
                                        photo = service.body.photo,
                                        name = service.body.name
                                    )
                                )
                                solutionLiveData.postValue(solutionData)
                            }
                            is NetworkResponse.Error -> {
                                Timber.e(service.error)
                                errorLiveData.postValue(service.body)
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(solution.error)
                        errorLiveData.postValue(solution.body)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    data class SolutionData(
        val photo: String,
        val description: String,
        val creationDate: Date,
        val service: ServiceData,
    )

    data class ServiceData(
        val uid: String,
        val photo: String,
        val name: String
    )
}