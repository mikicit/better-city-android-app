package dev.mikita.bettercity.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mikita.bettercity.api.issueservice.IssueServiceApi
import dev.mikita.bettercity.api.notificationservice.NotificationServiceApi
import dev.mikita.bettercity.api.userservice.UserServiceApi
import dev.mikita.bettercity.repository.IssueRepository
import dev.mikita.bettercity.repository.NotificationRepository
import dev.mikita.bettercity.repository.UserRepository
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL = "https://abettercity.free.beeceptor.com/"
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create()

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        @ApplicationContext context: Context
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .cache(Cache(context.cacheDir, (10 * 1024 * 1024).toLong()))
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideIssueServiceApi(retrofit: Retrofit): IssueServiceApi =
        retrofit.create(IssueServiceApi::class.java)

    @Singleton
    @Provides
    fun provideNotificationServiceApi(retrofit: Retrofit): NotificationServiceApi =
        retrofit.create(NotificationServiceApi::class.java)

    @Singleton
    @Provides
    fun provideUserServiceApi(retrofit: Retrofit): UserServiceApi =
        retrofit.create(UserServiceApi::class.java)

    @Singleton
    @Provides
    fun providesIssueRepository(apiService: IssueServiceApi): IssueRepository =
        IssueRepository(apiService)

    @Singleton
    @Provides
    fun providesNotificationRepository(apiService: NotificationServiceApi): NotificationRepository =
        NotificationRepository(apiService)

    @Singleton
    @Provides
    fun providesUserRepository(apiService: UserServiceApi): UserRepository =
        UserRepository(apiService)
}