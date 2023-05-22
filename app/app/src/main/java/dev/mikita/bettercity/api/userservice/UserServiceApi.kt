package dev.mikita.bettercity.api.userservice

import com.haroldadmin.cnradapter.NetworkResponse
import dev.mikita.bettercity.api.ErrorResponse
import dev.mikita.bettercity.util.IssueStatus
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UserServiceApi {
    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/residents/me")
    suspend fun getCurrentResident(): NetworkResponse<CurrentResident, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/residents/me/issues")
    suspend fun getCurrentResidentIssues(@Query("status") status: IssueStatus?): NetworkResponse<Issues, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/residents/me/issues/count")
    suspend fun getCurrentResidentIssuesCount(): NetworkResponse<ItemsCount, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/residents/{uid}")
    suspend fun getResident(@Path("uid") uid: String): NetworkResponse<Resident, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/residents/{uid}/issues/count")
    suspend fun getResidentIssuesCount(@Path("uid") uid: String): NetworkResponse<ItemsCount, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/services/{uid}")
    suspend fun getService(@Path("uid") uid: String): NetworkResponse<Service, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/services/{uid}/reservations/count")
    suspend fun getServiceReservationsCount(@Path("uid") uid: String): NetworkResponse<ItemsCount, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/services/{uid}/solutions/count")
    suspend fun getServiceSolutionsCount(@Path("uid") uid: String): NetworkResponse<ItemsCount, ErrorResponse>
}