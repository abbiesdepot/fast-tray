package com.abbie.fast_tray.network

import com.abbie.fast_tray.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- User & Auth ---
    @POST("api/users/login")
    suspend fun login(@Body body: LoginRequest): Response<DataResponse<User>>

    @POST("api/users/")
    suspend fun register(@Body body: RegisterRequest): Response<DataResponse<User>>

    @GET("api/users/")
    suspend fun getUsers(): Response<DataResponse<List<User>>>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<DataResponse<User>>

    @PATCH("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body body: Map<String, String>): Response<DataResponse<User>>

    @PATCH("api/users/{id}/ban")
    suspend fun banUser(@Path("id") id: Int): Response<DataResponse<User>>

    @PATCH("api/users/{id}/warn")
    suspend fun warnUser(@Path("id") id: Int): Response<DataResponse<User>>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    // --- Stalls ---
    @GET("api/private/stalls/")
    suspend fun getStalls(): Response<DataResponse<List<Stall>>>

    @POST("api/private/stalls/")
    suspend fun createStall(@Body body: CreateStallRequest): Response<DataResponse<Stall>>

    @GET("api/private/stalls/{stallId}")
    suspend fun getStall(@Path("stallId") stallId: Int): Response<DataResponse<Stall>>

    @PATCH("api/private/stalls/{stallId}")
    suspend fun updateStall(@Path("stallId") stallId: Int, @Body body: Map<String, String>): Response<DataResponse<Stall>>

    @PATCH("api/private/stalls/{stallId}/toggle")
    suspend fun toggleStall(@Path("stallId") stallId: Int): Response<DataResponse<Stall>>

    @GET("api/private/stalls/{stallId}/sales-summary")
    suspend fun getSalesSummary(@Path("stallId") stallId: Int): Response<DataResponse<SalesSummary>>

    // --- Menu Items ---
    @GET("api/private/menu-items/stalls/{stallId}")
    suspend fun getMenuItems(@Path("stallId") stallId: Int): Response<DataResponse<List<MenuItem>>>

    @POST("api/private/menu-items/stalls/{stallId}")
    suspend fun addMenuItem(@Path("stallId") stallId: Int, @Body body: MenuItemRequest): Response<DataResponse<MenuItem>>

    @PATCH("api/private/menu-items/{menuItemId}")
    suspend fun updateMenuItem(@Path("menuItemId") menuItemId: Int, @Body body: MenuItemRequest): Response<DataResponse<MenuItem>>

    @PATCH("api/private/menu-items/{menuItemId}/availability")
    suspend fun toggleMenuItemAvailability(@Path("menuItemId") menuItemId: Int): Response<DataResponse<MenuItem>>

    @PATCH("api/private/menu-items/{menuItemId}/delete")
    suspend fun deleteMenuItem(@Path("menuItemId") menuItemId: Int): Response<Unit>

    // --- Orders ---
    @POST("api/private/orders/")
    suspend fun placeOrder(@Body body: PlaceOrderRequest): Response<DataResponse<Order>>

    @GET("api/private/orders/{orderId}")
    suspend fun getOrder(@Path("orderId") orderId: Int): Response<DataResponse<Order>>

    @GET("api/private/orders/students/{studentId}")
    suspend fun getStudentOrders(@Path("studentId") studentId: Int): Response<DataResponse<List<Order>>>

    @GET("api/private/orders/stalls/{stallId}/queue")
    suspend fun getStallQueue(@Path("stallId") stallId: Int): Response<DataResponse<List<Order>>>

    @PATCH("api/private/orders/{orderId}/status")
    suspend fun updateOrderStatus(@Path("orderId") orderId: Int, @Body body: UpdateStatusRequest): Response<DataResponse<Order>>

    @PATCH("api/private/orders/{orderId}/cancel")
    suspend fun cancelOrder(@Path("orderId") orderId: Int): Response<DataResponse<Order>>

    // --- Admin ---
    @GET("api/private/admin/users")
    suspend fun adminGetUsers(): Response<DataResponse<List<User>>>

    @GET("api/private/admin/stalls")
    suspend fun adminGetStalls(): Response<DataResponse<List<Stall>>>
}

// Request Models
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val role: String)
data class CreateStallRequest(val name: String, val description: String, val location: String, val ownerId: Int)
data class MenuItemRequest(val name: String, val description: String, val price: Double, val category: String)
data class PlaceOrderRequest(val studentId: Int, val stallId: Int, val items: List<OrderItemsRequest>, val pickupTime: String)
data class OrderItemsRequest(val menuItemId: Int, val quantity: Int, val notes: String)
data class UpdateStatusRequest(val status: String, val rejectionReason: String = "")
data class DataResponse<T>(val data: T)
