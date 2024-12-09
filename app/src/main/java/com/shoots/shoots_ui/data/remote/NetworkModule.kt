package com.shoots.shoots_ui.data.remote

// NetworkModule.kt
import android.content.Context
import com.shoots.shoots_ui.BuildConfig
import com.shoots.shoots_ui.data.local.DatabaseModule
import com.shoots.shoots_ui.data.local.UserDao
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = BuildConfig.API_URL
    private var userDao: UserDao? = null

    fun initialize(context: Context) {
        userDao = DatabaseModule.getDatabase(context).userDao()
    }

    private val authInterceptor = Interceptor { chain ->
        val token = runBlocking {
            userDao?.getUser()?.accessToken
        }

        val request = chain.request().newBuilder().apply {
            addHeader("Content-Type", "application/json")
            token?.let {
                addHeader("Authorization", "Bearer $it")
            }
        }.build()

        chain.proceed(request)
    }


    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}