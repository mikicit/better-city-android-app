package dev.mikita.bettercity.repository

import dev.mikita.bettercity.api.userservice.UserServiceApi
import dev.mikita.bettercity.util.IssueStatus

class UserRepository(private val userServiceApi: UserServiceApi) {
    suspend fun getCurrentResident() = userServiceApi.getCurrentResident()
    suspend fun getCurrentResidentIssues(status: IssueStatus?) = userServiceApi.getCurrentResidentIssues(status)
    suspend fun getCurrentResidentIssuesCount() = userServiceApi.getCurrentResidentIssuesCount()
    suspend fun getResident(uid: String) = userServiceApi.getResident(uid)
    suspend fun getResidentIssuesCount(uid: String) = userServiceApi.getResidentIssuesCount(uid)
    suspend fun getService(uid: String) = userServiceApi.getService(uid)
    suspend fun getServiceReservationsCount(uid: String) = userServiceApi.getServiceReservationsCount(uid)
    suspend fun getServiceSolutionsCount(uid: String) = userServiceApi.getServiceSolutionsCount(uid)
}