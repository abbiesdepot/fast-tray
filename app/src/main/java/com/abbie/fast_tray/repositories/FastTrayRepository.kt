package com.abbie.fast_tray.repositories

import android.util.Log
import com.abbie.fast_tray.API.ApiClient
import com.abbie.fast_tray.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FastTrayRepository {

    private fun currentDateString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _stalls = MutableStateFlow<List<Stall>>(emptyList())
    val stalls: StateFlow<List<Stall>> = _stalls.asStateFlow()

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // ==========================================
    // FETCH ALL DATA
    // ==========================================

    suspend fun fetchAllData() {
        // 1. Fetch Users (via admin endpoint)
        try {
            val usersResponse = ApiClient.instance.getUsers()
            _users.value = if (usersResponse.data.isEmpty()) getDefaultDummyUsers()
            else usersResponse.data
        } catch (e: Exception) {
            Log.e("REPO", "Gagal fetch users: ${e.message}")
            _users.value = getDefaultDummyUsers()
        }

        // 2. Fetch Stalls (publik, untuk student)
        try {
            val stallsResponse = ApiClient.instance.getStalls()
            _stalls.value = stallsResponse.data
        } catch (e: Exception) {
            Log.e("REPO", "Gagal fetch stalls: ${e.message}")
        }

        // 3. Fetch Menu Items
        try {
            val menuResponse = ApiClient.instance.getMenuItems()
            _menuItems.value = menuResponse.data
        } catch (e: Exception) {
            Log.e("REPO", "Gagal fetch menu items: ${e.message}")
        }

        // 4. Fetch Orders
        try {
            val ordersResponse = ApiClient.instance.getOrders()
            _orders.value = ordersResponse.data
        } catch (e: Exception) {
            Log.e("REPO", "Gagal fetch orders: ${e.message}")
        }
    }

    private fun getDefaultDummyUsers(): List<User> {
        return listOf(
            User(id = 101, name = "Pak Budi (Owner)", email = "budi@owner.com", role = UserRole.STALL_OWNER, warningCount = 0, isBanned = false),
            User(id = 102, name = "Bu Siti (Owner)", email = "siti@owner.com", role = UserRole.STALL_OWNER, warningCount = 0, isBanned = false),
            User(id = 999, name = "Admin Kampus", email = "admin@campus.com", role = UserRole.ADMIN, warningCount = 0, isBanned = false),
            User(id = 1, name = "Mahasiswa Demo", email = "demo@student.com", role = UserRole.STUDENT, warningCount = 0, isBanned = false)
        )
    }

    // ==========================================
    // LOGIN
    // ==========================================

    suspend fun login(username: String, role: UserRole): User? {
        return try {
            val email = "${username.lowercase().replace(" ", "")}@fasttray.com"
            val body = mapOf("email" to email, "name" to username, "role" to role.name)
            val response = ApiClient.instance.login(body)
            response.data
        } catch (e: Exception) {
            Log.e("REPO", "Gagal API login, menggunakan local fallback: ${e.message}")
            _users.value.firstOrNull { it.name.contains(username, ignoreCase = true) }
        }
    }

    // ==========================================
    // ADMIN — USER ACTIONS
    // ==========================================

    suspend fun warnUser(userId: Int): User? {
        return try {
            val response = ApiClient.instance.warnUser(userId)
            val updatedUser = response.data
            if (updatedUser != null) {
                _users.value = _users.value.map { if (it.id == userId) updatedUser else it }
            }
            updatedUser
        } catch (e: Exception) {
            Log.e("REPO", "Gagal warn user: ${e.message}")
            null
        }
    }

    suspend fun toggleBanUser(userId: Int): User? {
        return try {
            val response = ApiClient.instance.toggleBanUser(userId)
            val updatedUser = response.data
            if (updatedUser != null) {
                _users.value = _users.value.map { if (it.id == userId) updatedUser else it }
            }
            updatedUser
        } catch (e: Exception) {
            Log.e("REPO", "Gagal ban/unban user: ${e.message}")
            null
        }
    }

    // ==========================================
    // ADMIN — STALL ACTIONS
    // ==========================================

    suspend fun addStall(name: String, description: String, location: String, ownerId: Int): Stall? {
        return try {
            val request = StallRequest(
                ownerId = ownerId,
                name = name,
                location = location,
                description = description
            )
            val response = ApiClient.instance.addStall(request)
            val newStall = response.data
            if (newStall != null) {
                _stalls.value = _stalls.value + newStall
            }
            newStall
        } catch (e: Exception) {
            Log.e("REPO", "Gagal add stall: ${e.message}")
            null
        }
    }

    suspend fun toggleStallActive(stallId: Int): Stall? {
        return try {
            val response = ApiClient.instance.toggleStallStatus(stallId)
            val updatedStall = response.data
            if (updatedStall != null) {
                _stalls.value = _stalls.value.map { if (it.id == stallId) updatedStall else it }
            }
            updatedStall
        } catch (e: Exception) {
            Log.e("REPO", "Gagal ubah status stall: ${e.message}")
            null
        }
    }

    // ==========================================
    // MENU ITEMS
    // ==========================================

    suspend fun addMenuItem(stallId: Int, name: String, description: String, price: Double, category: String): MenuItem {
        val newId = (_menuItems.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = MenuItem(
            id = newId,
            stallId = stallId,
            name = name,
            description = description,
            price = price,
            category = category,
            isAvailable = true
        )
        _menuItems.value = _menuItems.value + newItem
        return newItem
    }

    suspend fun updateMenuItem(itemId: Int, name: String, description: String, price: Double, category: String): MenuItem? {
        var updatedItem: MenuItem? = null
        _menuItems.value = _menuItems.value.map { item ->
            if (item.id == itemId) {
                val newItem = item.copy(name = name, description = description, price = price, category = category)
                updatedItem = newItem
                newItem
            } else {
                item
            }
        }
        return updatedItem
    }

    suspend fun toggleMenuItemAvailable(itemId: Int): MenuItem? {
        var updatedItem: MenuItem? = null
        _menuItems.value = _menuItems.value.map { item ->
            if (item.id == itemId) {
                val newItem = item.copy(isAvailable = !item.isAvailable)
                updatedItem = newItem
                newItem
            } else {
                item
            }
        }
        return updatedItem
    }

    // ==========================================
    // ORDERS
    // ==========================================

    suspend fun placeOrder(
        studentId: Int,
        stallId: Int,
        cartItems: List<CartItem>,
        pickupTime: String
    ): Order? {
        return try {
            val orderItemRequests = cartItems.map {
                OrderItemRequest(
                    menuItemId = it.menuItem.id,
                    quantity = it.quantity,
                    notes = it.notes
                )
            }
            val request = OrderRequest(
                studentId = studentId,
                stallId = stallId,
                pickupTime = pickupTime,
                items = orderItemRequests
            )
            ApiClient.instance.createOrder(request)

            val student = _users.value.firstOrNull { it.id == studentId }
            if (student == null || student.isBanned) return null

            val orderId = (_orders.value.maxOfOrNull { it.id } ?: 1000) + 1
            val orderItems = cartItems.mapIndexed { index, cartItem ->
                OrderItem(
                    id = index + 1,
                    orderId = orderId,
                    menuItemId = cartItem.menuItem.id,
                    menuItemName = cartItem.menuItem.name,
                    menuItemPrice = cartItem.menuItem.price,
                    quantity = cartItem.quantity,
                    notes = cartItem.notes
                )
            }
            val total = orderItems.sumOf { it.subtotal }
            val newOrder = Order(
                id = orderId,
                studentId = studentId,
                stallId = stallId,
                items = orderItems,
                status = OrderStatus.PENDING,
                pickupTime = pickupTime,
                totalPrice = total,
                createdAt = currentDateString() + " 12:00:00"
            )

            _orders.value = listOf(newOrder) + _orders.value
            newOrder
        } catch (e: Exception) {
            Log.e("REPO", "Gagal place order: ${e.message}")
            null
        }
    }

    suspend fun updateOrderStatus(
        orderId: Int,
        newStatus: OrderStatus,
        rejectionReason: String = ""
    ): Boolean {
        var success = false
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val current = order.status
                val isTransitionAllowed = when (current) {
                    OrderStatus.PENDING -> newStatus == OrderStatus.PREPARING
                            || newStatus == OrderStatus.REJECTED
                            || newStatus == OrderStatus.CANCELLED
                    OrderStatus.ACCEPTED -> newStatus == OrderStatus.PREPARING
                            || newStatus == OrderStatus.CANCELLED
                    OrderStatus.PREPARING -> newStatus == OrderStatus.READY
                    OrderStatus.READY -> newStatus == OrderStatus.COMPLETED
                    else -> false
                }

                if (isTransitionAllowed) {
                    success = true
                    val updatedOrder = order.copy(
                        status = newStatus,
                        rejectionReason = if (newStatus == OrderStatus.REJECTED) rejectionReason
                        else order.rejectionReason,
                        updatedAt = currentDateString() + " 12:00:00"
                    )
                    if (newStatus == OrderStatus.CANCELLED) {
                        warnUser(order.studentId)
                    }
                    updatedOrder
                } else {
                    order
                }
            } else {
                order
            }
        }
        return success
    }

    // ==========================================
    // SALES SUMMARY
    // ==========================================

    fun getSalesSummary(stallId: Int, filterDate: String): SalesSummary {
        val stallOrders = _orders.value.filter {
            it.stallId == stallId && it.createdAt.startsWith(filterDate)
        }

        val completed = stallOrders.filter { it.status == OrderStatus.COMPLETED }
        val cancelled = stallOrders.filter { it.status == OrderStatus.CANCELLED }
        val rejected = stallOrders.filter { it.status == OrderStatus.REJECTED }
        val totalRevenue = completed.sumOf { it.totalPrice }

        val itemSalesMap = mutableMapOf<Int, Triple<String, Int, Double>>()
        completed.flatMap { it.items }.forEach { item ->
            val current = itemSalesMap[item.menuItemId] ?: Triple(item.menuItemName, 0, 0.0)
            itemSalesMap[item.menuItemId] = Triple(
                current.first,
                current.second + item.quantity,
                current.third + item.subtotal
            )
        }

        val topSelling = itemSalesMap.map { (id, triplet) ->
            TopSellingItem(
                menuItemId = id,
                menuItemName = triplet.first,
                quantitySold = triplet.second,
                revenue = triplet.third
            )
        }.sortedByDescending { it.quantitySold }.take(5)

        return SalesSummary(
            stallId = stallId,
            date = filterDate,
            totalOrders = stallOrders.size,
            completedOrders = completed.size,
            cancelledOrders = cancelled.size,
            rejectedOrders = rejected.size,
            totalRevenue = totalRevenue,
            topSellingItems = topSelling
        )
    }
}