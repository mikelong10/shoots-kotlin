package com.shoots.shoots_ui.data.remote

import android.content.Context
import com.shoots.shoots_ui.BuildConfig
import com.shoots.shoots_ui.data.local.UserDao
import com.shoots.shoots_ui.ui.auth.AuthState
import com.shoots.shoots_ui.ui.auth.AuthViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object NetworkModule {
    private const val BASE_URL = BuildConfig.API_URL
    private var userDao: UserDao? = null
    private var authViewModel: AuthViewModel? = null
    private val refreshTokenLock = ReentrantLock()
    private var cookieJar: AppCookieManager? = null
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private var _apiService: ApiService = createDefaultApiService()
    
    val apiService: ApiService 
        get() = _apiService

    private fun createDefaultApiService(): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val authInterceptor by lazy {
        Interceptor { chain ->
            val originalRequest = chain.request()

            fun createAuthorizedRequest(request: Request, accessToken: String): Request {
                return request.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            }

            var request = originalRequest
            val token = runBlocking {
                userDao?.getAccessToken()
            }

            if (token != null) {
                request = createAuthorizedRequest(originalRequest, token)
            }

            val response = chain.proceed(request)

            if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                refreshTokenLock.withLock {
                    response.close()

                    val refreshToken = runBlocking {
                        userDao?.getRefreshToken()
                    }

                    if (refreshToken != null) {
                        try {
                            val refreshClient = OkHttpClient.Builder()
                                .apply { cookieJar?.let { jar -> cookieJar(jar) } }
                                .addInterceptor(loggingInterceptor)
                                .build()

                            val refreshRetrofit = Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .client(refreshClient)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()

                            val refreshApi = refreshRetrofit.create(ApiService::class.java)
                            val refreshResponse = refreshApi.refresh(RefreshRequest(refreshToken)).execute()

                            if (refreshResponse.isSuccessful && refreshResponse.body()?.success == true) {
                                val newAccessToken = refreshResponse.body()?.data?.accessToken

                                if (newAccessToken != null) {
                                    val newRequest = createAuthorizedRequest(originalRequest, newAccessToken)
                                    return@Interceptor chain.proceed(newRequest)
                                }
                            } else {
                                handleAuthFailure()
                            }
                        } catch (e: Exception) {
                            handleAuthFailure()
                        }
                    } else {
                        handleAuthFailure()
                    }
                }
            }
            response
        }
    }

    fun initialize(context: Context, dao: UserDao, viewModel: AuthViewModel) {
        userDao = dao
        authViewModel = viewModel
        if (cookieJar == null) {
            cookieJar = AppCookieManager(context, dao)
        }
        _apiService = createAuthenticatedApiService()
    }

    private fun createAuthenticatedApiService(): ApiService {
        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)

        cookieJar?.let { jar ->
            clientBuilder.cookieJar(jar)
        }

        val client = clientBuilder.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun handleAuthFailure() {
        runBlocking {
            userDao?.deleteUser()
            cookieJar?.clearCookies()
            authViewModel?.updateAuthState(AuthState.NotAuthenticated)
        }
    }
}