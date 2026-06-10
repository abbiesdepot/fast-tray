package com.abbie.fast_tray.API

import com.abbie.fast_tray.models.*
import retrofit2.http.*

interface ApiService {

    // ── Public / Student ──────────────────────────────────────────
    @GET("stalls")
    suspend fun getStalls(): ApiResponse<List<Stall>>

    @GET("stalls/{id}/menu")
    suspend fun getStallWithMenu(@Path("id") stallId: Int): ApiResponse<Stall>

    @GET("menu-items")
    suspend fun getMenuItems(): ApiResponse<List<MenuItem>>

    @GET("orders")
    suspend fun getOrders(): ApiResponse<List<Order>>

    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequest): ApiResponse<Order>

    @POST("users/login")
    suspend fun login(@Body body: Map<String, String>): ApiResponse<User>

    // ── Admin ─────────────────────────────────────────────────────
    @GET("admin/users")
    suspend fun getUsers(): ApiResponse<List<User>>

    @PATCH("admin/users/{id}/warn")
    suspend fun warnUser(@Path("id") userId: Int): ApiResponse<User>

    @PATCH("admin/users/{id}/ban")
    suspend fun toggleBanUser(@Path("id") userId: Int): ApiResponse<User>

    @GET("admin/stalls")
    suspend fun getAdminStalls(): ApiResponse<List<Stall>>

    @POST("admin/stalls")
    suspend fun addStall(@Body request: StallRequest): ApiResponse<Stall>

    @PATCH("admin/stalls/{id}/toggle")
    suspend fun toggleStallStatus(@Path("id") stallId: Int): ApiResponse<Stall>
}