package dev.mikita.bettercity.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikita.bettercity.api.ErrorResponse
import dev.mikita.bettercity.main.data.IssueCardData
import dev.mikita.bettercity.repository.UserRepository
import dev.mikita.bettercity.util.IssueStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyIssuesListViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    private val errorLiveData = MutableLiveData<ErrorResponse>()
    private val issuesLiveData = MutableLiveData<List<IssueCardData>?>()
    private var fetchJob: Job? = null

    fun getIssues() = issuesLiveData
    fun getErrorLiveData() = errorLiveData

    fun loadIssues(status: IssueStatus?) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                when (val issues = repository.getCurrentResidentIssues(status)) {
                    is NetworkResponse.Success -> {
                        var issuesList = listOf<IssueCardData>()

                        issues.body.forEach {
                            issuesList = issuesList + IssueCardData(
                                id = it.id,
                                photo = it.photo,
                                title = it.title,
                                description = it.description,
                                creationDate = it.creationDate,
                                coordinates = LatLng(it.coordinates.latitude, it.coordinates.longitude),
                                categoryId = it.categoryId,
                                authorId = it.authorId,
                                status = it.status
                            )
                        }

                        issuesLiveData.postValue(issuesList)
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(issues.error)
                        errorLiveData.postValue(issues.body)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}