package com.abbie.fast_tray.API

import com.abbie.fast_tray.models.ApiResponse
import com.abbie.fast_tray.models.StallRequest
import com.abbie.fast_tray.models.OrderRequest
import com.abbie.fast_tray.models.Order
import com.abbie.fast_tray.models.Stall
import com.abbie.fast_tray.models.User
import com.abbie.fast_tray.models.MenuItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
//    @GET("users")
//    suspend fun getUsers(): List<User>
    @GET("users")
    suspend fun getUsers(): ApiResponse<List<User>>

//    @GET("stalls")
//    suspend fun getStalls(): List<Stall>
    @GET("stalls")
    suspend fun getStalls(): ApiResponse<List<Stall>>

//    @POST("stalls")
//    suspend fun addStall(@Body request: StallRequest): Stall
    @POST("stalls")
    suspend fun addStall(@Body request: StallRequest): ApiResponse<Stall>

//    @POST("orders")
//    suspend fun createOrder(@Body request: OrderRequest): Order
    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequest): ApiResponse<Order>

    // ==========================================
    // TAMBAHAN UNTUK MENU & ORDER
    // ==========================================

    @GET("menu-items")
    suspend fun getMenuItems(): ApiResponse<List<MenuItem>>

    @GET("orders")
    suspend fun getOrders(): ApiResponse<List<Order>>
}