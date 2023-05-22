package dev.mikita.bettercity.api.issueservice

import com.haroldadmin.cnradapter.NetworkResponse
import dev.mikita.bettercity.api.ErrorResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface IssueServiceApi {
    @Headers("Cache-Control: max-age=600")
    @GET("/api/v1/issues")
    suspend fun getIssues(): NetworkResponse<Issues, ErrorResponse>

    @POST("/api/v1/issues")
    @Multipart
    suspend fun createIssue(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part("coordinate") coordinate: RequestBody,
        @Part photo: MultipartBody.Part
    ): NetworkResponse<Unit, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/issues/{id}")
    suspend fun getIssue(@Path("id") id: Long): NetworkResponse<Issue, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/issues/{id}/reservation")
    suspend fun getIssueReservation(@Path("id") id: Long): NetworkResponse<IssueReservation, ErrorResponse>

    @Headers("Cache-Control: max-age=60")
    @GET("/api/v1/issues/{id}/solution")
    suspend fun getIssueSolution(@Path("id") id: Long): NetworkResponse<IssueSolution, ErrorResponse>

    @POST("/api/v1/issues/{id}/like")
    suspend fun likeIssue(@Path("id") id: Long): NetworkResponse<Unit, ErrorResponse>

    @DELETE("/api/v1/issues/{id}/like")
    suspend fun unlikeIssue(@Path("id") id: Long): NetworkResponse<Unit, ErrorResponse>

    @Headers("Cache-Control: max-age=600")
    @GET("/api/v1/issues/{id}/likes/count")
    suspend fun getLikesCount(@Path("id") id: Long): NetworkResponse<Likes, ErrorResponse>

    @GET("/api/v1/issues/{id}/like/status")
    suspend fun getLikeStatus(@Path("id") id: Long): NetworkResponse<LikeStatus, ErrorResponse>

    @Headers("Cache-Control: max-age=600")
    @GET("/api/v1/categories")
    suspend fun getCategories(): NetworkResponse<Categories, ErrorResponse>

    @Headers("Cache-Control: max-age=600")
    @GET("/api/v1/categories/{id}")
    suspend fun getCategory(@Path("id") id: Long): NetworkResponse<Category, ErrorResponse>
}