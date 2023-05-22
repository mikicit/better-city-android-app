package dev.mikita.bettercity.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
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
class IssueViewModel @Inject constructor(
    private val issueRepository: IssueRepository,
    private val userRepository: UserRepository,
    ) : ViewModel() {

    private val errorLiveData = MutableLiveData<ErrorResponse>()
    private val issueLiveData = MutableLiveData<IssueData>()
    private val isLikedLiveData = MutableLiveData<Boolean>()
    private var fetchJob: Job? = null
    private var checkLikeJob: Job? = null
    private var likeJob: Job? = null
    private var unlikeJob: Job? = null

    fun getIssueLiveData() = issueLiveData
    fun getErrorLiveData() = errorLiveData
    fun getIsLikedLiveData() = isLikedLiveData

    fun unlike(id: Long) {
        unlikeJob?.cancel()
        unlikeJob = viewModelScope.launch {
            try {
                when (val response = issueRepository.unlikeIssue(id)) {
                    is NetworkResponse.Success -> {
                        isLikedLiveData.postValue(false)
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(response.error)
                        isLikedLiveData.postValue(isLikedLiveData.value)
                        errorLiveData.postValue(response.body)
                    }
                }
            } catch (e: Exception) {
                isLikedLiveData.postValue(isLikedLiveData.value)
                Timber.e(e)
            }
        }
    }

    fun like(id: Long) {
        likeJob?.cancel()
        likeJob = viewModelScope.launch {
            try {
                when (val response = issueRepository.likeIssue(id)) {
                    is NetworkResponse.Success -> {
                        isLikedLiveData.postValue(true)
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(response.error)
                        isLikedLiveData.postValue(isLikedLiveData.value)
                        errorLiveData.postValue(response.body)
                    }
                }
            } catch (e: Exception) {
                isLikedLiveData.postValue(isLikedLiveData.value)
                Timber.e(e)
            }
        }
    }

    fun isLiked(id: Long) {
        checkLikeJob?.cancel()
        checkLikeJob = viewModelScope.launch {
            try {
                when (val response = issueRepository.getLikeStatus(id)) {
                    is NetworkResponse.Success -> {
                        isLikedLiveData.postValue(response.body.status)
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(response.error)
                        errorLiveData.postValue(response.body)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun loadData(id: Long) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                when (val issue = issueRepository.getIssue(id)) {
                    is NetworkResponse.Success -> {
                        when (val category = issueRepository.getCategory(issue.body.categoryId)) {
                            is NetworkResponse.Success -> {
                                when (val author = userRepository.getResident(issue.body.authorId)) {
                                    is NetworkResponse.Success -> {
                                        when (val likes = issueRepository.getLikesCount(issue.body.id)) {
                                            is NetworkResponse.Success -> {
                                                issueLiveData.postValue(
                                                    IssueData(
                                                        id = issue.body.id,
                                                        photo = issue.body.photo,
                                                        title = issue.body.title,
                                                        description = issue.body.description,
                                                        creationDate = issue.body.creationDate,
                                                        coordinates = LatLng(issue.body.coordinates.latitude, issue.body.coordinates.longitude),
                                                        author = ResidentData(
                                                            uid = author.body.uid,
                                                            photo = author.body.photo,
                                                            firstName = author.body.firstName,
                                                            lastName = author.body.lastName
                                                        ),
                                                        category = category.body.name,
                                                        status = issue.body.status,
                                                        likes = likes.body.count
                                                    )
                                                )
                                            }
                                            is NetworkResponse.Error -> {
                                                Timber.e(likes.error)
                                                errorLiveData.postValue(likes.body)
                                            }
                                        }
                                    }
                                    is NetworkResponse.Error -> {
                                        Timber.e(author.error)
                                        errorLiveData.postValue(author.body)
                                    }
                                }
                            }
                            is NetworkResponse.Error -> {
                                Timber.e(category.error)
                                errorLiveData.postValue(category.body)
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(issue.error)
                        errorLiveData.postValue(issue.body)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    data class IssueData(
        val id: Long,
        val photo: String,
        val title: String,
        val description: String,
        val creationDate: Date,
        val coordinates: LatLng,
        val author: ResidentData,
        val category: String,
        val status: String,
        val likes: Int
    )

    data class ResidentData(
        val uid: String,
        val photo: String,
        val firstName: String,
        val lastName: String
    )
}