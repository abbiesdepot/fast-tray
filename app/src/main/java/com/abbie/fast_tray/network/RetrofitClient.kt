package com.abbie.fast_tray.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/" // 10.0.2.2 is localhost for Android Emulator

    // Holds the current logged-in user's identity for auth headers
    private var currentUserId: Int? = null
    private var currentUserRole: String? = null
    private var currentUserEmail: String? = null

    fun setCurrentUser(id: Int?, role: String?, email: String?) {
        currentUserId = id
        currentUserRole = role
        currentUserEmail = email
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        currentUserId?.let { requestBuilder.addHeader("x-user-id", it.toString()) }
        currentUserRole?.let { requestBuilder.addHeader("x-user-role", it) }
        currentUserEmail?.let { requestBuilder.addHeader("x-user-email", it) }
        chain.proceed(requestBuilder.build())
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ApiService::class.java)
    }
}
