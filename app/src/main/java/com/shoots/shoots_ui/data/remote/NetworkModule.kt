package com.shoots.shoots_ui.data.remote

import android.content.Context
import com.shoots.shoots_ui.BuildConfig
import com.shoots.shoots_ui.data.local.UserDao
import com.shoots.shoots_ui.ui.auth.AuthState
import com.shoots.shoots_ui.ui.auth.AuthViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = BuildConfig.API_URL
    private var userDao: UserDao? = null
    private var authViewModel: AuthViewModel? = null

    fun initialize(context: Context, dao: UserDao, viewModel: AuthViewModel) {
        userDao = dao
        authViewModel = viewModel
    }

    private val authInterceptor = Interceptor { chain ->
        val token = runBlocking {
            userDao?.getAccessToken()
        }

        val request = chain.request().newBuilder().apply {
            addHeader("Content-Type", "application/json")
            token?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            // Unauthorized - clear user data and update auth state
            runBlocking {
                userDao?.deleteUser()
                authViewModel?.updateAuthState(AuthState.NotAuthenticated)
            }
        }

        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}