package dev.mikita.bettercity.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mikita.bettercity.api.ErrorResponse
import dev.mikita.bettercity.repository.IssueRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddNewIssueViewModel @Inject constructor(
    private val issueRepository: IssueRepository
    ) : ViewModel() {

    // Steps
    private val currentStep = MutableLiveData<Step>()

    // Data
    private var coordinates: LatLng? = null
    private var title: String? = null
    private var description: String? = null

    // Liva Data
    private val categoriesLiveData = MutableLiveData<List<CategoryData>>()
    private var photoLiveData = MutableLiveData<File?>()
    private val categoryLiveData = MutableLiveData<Long?>()
    private var errorLiveData = MutableLiveData<ErrorResponse?>()
    private var successLiveData = MutableLiveData<Boolean>()
    private var loadingLiveData = MutableLiveData<Boolean>()

    // Validation Flags
    var isValidScreen = MutableLiveData<Boolean>()
    var titleIsValid = MutableLiveData<Boolean>()
    var descriptionIsValid = MutableLiveData<Boolean>()

    // Utils
    private var fetchJobCategory: Job? = null
    private var fetchJobIssue: Job? = null

    init {
        currentStep.value = Step.LOCATION
    }

    fun getCurrentStep() = currentStep
    fun getCategoriesLiveData() = categoriesLiveData
    fun getPhotoLiveData() = photoLiveData
    fun getCategoryLiveData() = categoryLiveData
    fun getCoordinates() = coordinates
    fun getTitle() = title
    fun getDescription() = description
    fun getErrorLiveData() = errorLiveData
    fun getLoadingLiveData() = loadingLiveData
    fun getSuccessLiveData() = successLiveData

    fun sendIssue() {
        fetchJobIssue?.cancel()
        fetchJobIssue = viewModelScope.launch {
            try {
                loadingLiveData.postValue(true)
                val issueData = IssueData(
                    title = title!!,
                    description = description!!,
                    categoryId = categoryLiveData.value!!,
                    coordinate = coordinates!!,
                    photo = photoLiveData.value!!
                )

                when (val issueResponse = issueRepository.createIssue(issueData)) {
                    is NetworkResponse.Success -> {
                        loadingLiveData.postValue(false)
                        successLiveData.postValue(true)
                    }
                    is NetworkResponse.Error -> {
                        loadingLiveData.postValue(false)
                        errorLiveData.postValue(issueResponse.body)
                        Timber.e(issueResponse.error)
                    }
                }
            } catch (e: Exception) {
                loadingLiveData.postValue(false)
                errorLiveData.postValue(ErrorResponse("Unknown error"))
                Timber.e(e)
            }
        }
    }

    fun loadCategories() {
        fetchJobCategory?.cancel()
        fetchJobCategory = viewModelScope.launch {
            try {
                when (val categoriesResponse = issueRepository.getCategories()) {
                    is NetworkResponse.Success -> {
                        val categories = categoriesResponse.body
                        val categoriesData = categories.map {
                            CategoryData(
                                id = it.id,
                                name = it.name
                            )
                        }
                        categoriesLiveData.postValue(categoriesData)
                    }
                    is NetworkResponse.Error -> {
                        Timber.e(categoriesResponse.error)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun updateTitle(title: String) {
        this.title = title
        titleIsValid.value = title.length in 8..64
    }

    fun updateDescription(description: String) {
        this.description = description
        descriptionIsValid.value = description.length in 20..1000
    }

    fun updateCategory(categoryId: Long?) {
        categoryLiveData.value = categoryId
    }

    fun setCurrentStep(step: Step) {
        currentStep.value = step
    }

    fun updateCoordinate(coordinate: LatLng) {
        coordinates = coordinate
    }

    fun updatePhoto(photo: File) {
        photoLiveData.value = photo
    }

    fun clearError() {
        errorLiveData.value = null
    }

    fun clear() {
        coordinates = null
        photoLiveData = MutableLiveData<File?>()
        titleIsValid = MutableLiveData<Boolean>()
        descriptionIsValid = MutableLiveData<Boolean>()
        isValidScreen.value = false
        categoryLiveData.value = null
        title = null
        description = null
        errorLiveData = MutableLiveData<ErrorResponse?>()
        loadingLiveData = MutableLiveData<Boolean>()
        successLiveData = MutableLiveData<Boolean>()
    }

    data class IssueData(
        var title: String,
        var description: String,
        var categoryId: Long,
        var coordinate: LatLng,
        var photo: File
    )

    data class CategoryData(
        var id: Long,
        var name: String
    )

    enum class Step {
        LOCATION,
        PHOTO,
        CATEGORY,
        DESCRIPTION,
        CONFIRMATION
    }
}