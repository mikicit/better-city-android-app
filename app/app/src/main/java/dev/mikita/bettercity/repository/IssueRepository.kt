package dev.mikita.bettercity.repository

import com.haroldadmin.cnradapter.NetworkResponse
import dev.mikita.bettercity.api.ErrorResponse
import dev.mikita.bettercity.api.issueservice.IssueServiceApi
import dev.mikita.bettercity.main.viewmodel.AddNewIssueViewModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class IssueRepository(private val issueServiceApi: IssueServiceApi) {
    suspend fun getIssues() = issueServiceApi.getIssues()
    suspend fun getIssue(id: Long) = issueServiceApi.getIssue(id)
    suspend fun getIssueReservation(id: Long) = issueServiceApi.getIssueReservation(id)
    suspend fun getIssueSolution(id: Long) = issueServiceApi.getIssueSolution(id)
    suspend fun likeIssue(id: Long) = issueServiceApi.likeIssue(id)
    suspend fun unlikeIssue(id: Long) = issueServiceApi.unlikeIssue(id)
    suspend fun getLikesCount(id: Long) = issueServiceApi.getLikesCount(id)
    suspend fun getLikeStatus(id: Long) = issueServiceApi.getLikeStatus(id)
    suspend fun getCategories() = issueServiceApi.getCategories()
    suspend fun getCategory(id: Long) = issueServiceApi.getCategory(id)

    suspend fun createIssue(issueData: AddNewIssueViewModel.IssueData): NetworkResponse<Unit, ErrorResponse> {
        val titleRequestBody = issueData.title.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionRequestBody =
            issueData.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryIdRequestBody =
            issueData.categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val coordinateRequestBody =
            issueData.coordinate.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val photoRequestBody = issueData.photo.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData("photo", issueData.photo.name, photoRequestBody)

        return issueServiceApi.createIssue(titleRequestBody, descriptionRequestBody, categoryIdRequestBody, coordinateRequestBody, photoPart)
    }
}