package com.abbie.fast_tray.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Gunakan 10.0.2.2 untuk nyambung ke localhost laptop dari Emulator
    // tanda "/" di akhir URL wajib ada!
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}