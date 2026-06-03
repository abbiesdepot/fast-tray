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

    private fun currentDateString(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // StateFlow dikosongkan terlebih dahulu (akan diisi dari API)
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _stalls = MutableStateFlow<List<Stall>>(emptyList())
    val stalls: StateFlow<List<Stall>> = _stalls.asStateFlow()

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // ==========================================
    // FUNGSI GET (MENGAMBIL DATA DARI API)
    // ==========================================

    suspend fun fetchAllData() {
        try {
            // Ambil daftar user dari backend
            val usersResponse = ApiClient.instance.getUsers()
            _users.value = usersResponse.data

             val stallsResponse = ApiClient.instance.getStalls()
             _stalls.value = stallsResponse.data

             val menuResponse = ApiClient.instance.getMenuItems()
             _menuItems.value = menuResponse.data

             val ordersResponse = ApiClient.instance.getOrders()
             _orders.value = ordersResponse.data

        } catch (e: Exception) {
            Log.e("REPO", "Gagal fetch data: ${e.message}")
        }
    }

    // ==========================================
    // USER & SESSION
    // ==========================================

    suspend fun login(username: String, role: UserRole): User? {
        // Logika login lokal (bisa diubah tembak API /login jika sudah siap)
        val normalizedUsername = username.trim()
        val existing = _users.value.firstOrNull {
            it.name.equals(normalizedUsername, ignoreCase = true) && it.role == role
        }
        if (existing != null) {
            if (existing.isBanned) return null
            return existing
        }

        val newId = (_users.value.maxOfOrNull { it.id } ?: 0) + 1
        val newUser = User(
            id = newId,
            name = username,
            email = "${username.lowercase().replace(" ", "")}@campus.edu",
            role = role,
            isActive = true
        )
        _users.value = _users.value + newUser
        return newUser
    }

    suspend fun warnUser(userId: Int): User? {
        var updatedUser: User? = null
        _users.value = _users.value.map { user ->
            if (user.id == userId) {
                val newCount = user.warningCount + 1
                val banned = newCount >= 3
                val newUser = user.copy(warningCount = newCount, isBanned = banned)
                updatedUser = newUser
                newUser
            } else {
                user
            }
        }
        return updatedUser
    }

    suspend fun toggleBanUser(userId: Int): User? {
        var updatedUser: User? = null
        _users.value = _users.value.map { user ->
            if (user.id == userId) {
                val isBanned = !user.isBanned
                val warnings = if (!isBanned) 0 else user.warningCount
                val newUser = user.copy(isBanned = isBanned, warningCount = warnings)
                updatedUser = newUser
                newUser
            } else {
                user
            }
        }
        return updatedUser
    }

    // ==========================================
    // STALL
    // ==========================================

    suspend fun addStall(name: String, description: String, location: String, ownerId: Int): Stall? {
        return try {
            // 1. Tembak ke API Backend
            val request = StallRequest(
                ownerId = ownerId,
                name = name,
                location = location,
                description = description
            )

            ApiClient.instance.addStall(request)

            // 2. Update state lokal
            val newId = (_stalls.value.maxOfOrNull { it.id } ?: 0) + 1
            val newStall = Stall(id = newId, ownerId = ownerId, name = name, description = description, location = location, isActive = true)
            _stalls.value = _stalls.value + newStall

            newStall
        } catch (e: Exception) {
            Log.e("REPO", "Gagal add stall: ${e.message}")
            null
        }
    }

    suspend fun updateStall(stallId: Int, name: String, description: String, location: String): Stall? {
        var updatedStall: Stall? = null
        _stalls.value = _stalls.value.map { stall ->
            if (stall.id == stallId) {
                val newStall = stall.copy(name = name, description = description, location = location)
                updatedStall = newStall
                newStall
            } else {
                stall
            }
        }
        return updatedStall
    }

    suspend fun toggleStallActive(stallId: Int): Stall? {
        var updatedStall: Stall? = null
        _stalls.value = _stalls.value.map { stall ->
            if (stall.id == stallId) {
                val newStall = stall.copy(isActive = !stall.isActive)
                updatedStall = newStall
                newStall
            } else {
                stall
            }
        }
        return updatedStall
    }

    // ==========================================
    // MENU ITEM
    // ==========================================

    suspend fun addMenuItem(stallId: Int, name: String, description: String, price: Double, category: String): MenuItem {
        val newId = (_menuItems.value.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = MenuItem(id = newId, stallId = stallId, name = name, description = description, price = price, category = category, isAvailable = true)
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
    // ORDER
    // ==========================================

    suspend fun placeOrder(studentId: Int, stallId: Int, cartItems: List<CartItem>, pickupTime: String): Order? {
        return try {
            // 1. Siapkan data untuk dikirim ke Backend API
            val orderItemRequests = cartItems.map {
                OrderItemRequest(menuItemId = it.menuItem.id, quantity = it.quantity, notes = it.notes)
            }
            val request = OrderRequest(studentId = studentId, stallId = stallId, pickupTime = pickupTime, items = orderItemRequests)

            ApiClient.instance.createOrder(request)

            // 2. Update state lokal sebagai fallback
            val student = _users.value.firstOrNull { it.id == studentId }
            if (student == null || student.isBanned) return null

            val orderId = (_orders.value.maxOfOrNull { it.id } ?: 1000) + 1
            val orderItems = cartItems.mapIndexed { index, cartItem ->
                OrderItem(id = index + 1, orderId = orderId, menuItemId = cartItem.menuItem.id, menuItemName = cartItem.menuItem.name, menuItemPrice = cartItem.menuItem.price, quantity = cartItem.quantity, notes = cartItem.notes)
            }
            val total = orderItems.sumOf { it.subtotal }
            val newOrder = Order(id = orderId, studentId = studentId, stallId = stallId, items = orderItems, status = OrderStatus.PENDING, pickupTime = pickupTime, totalPrice = total, createdAt = currentDateString() + " 12:00:00")

            _orders.value = listOf(newOrder) + _orders.value
            newOrder
        } catch (e: Exception) {
            Log.e("REPO", "Gagal place order: ${e.message}")
            null
        }
    }

    suspend fun updateOrderStatus(orderId: Int, newStatus: OrderStatus, rejectionReason: String = ""): Boolean {
        var success = false
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val current = order.status
                val isTransitionAllowed = when (current) {
                    OrderStatus.PENDING -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.REJECTED || newStatus == OrderStatus.CANCELLED
                    OrderStatus.ACCEPTED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED
                    OrderStatus.PREPARING -> newStatus == OrderStatus.READY
                    OrderStatus.READY -> newStatus == OrderStatus.COMPLETED
                    else -> false
                }

                if (isTransitionAllowed) {
                    success = true
                    val updatedOrder = order.copy(
                        status = newStatus,
                        rejectionReason = if (newStatus == OrderStatus.REJECTED) rejectionReason else order.rejectionReason,
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
    // SALES SUMMARY (LOKAL)
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
            itemSalesMap[item.menuItemId] = Triple(current.first, current.second + item.quantity, current.third + item.subtotal)
        }

        val topSelling = itemSalesMap.map { (id, triplet) ->
            TopSellingItem(menuItemId = id, menuItemName = triplet.first, quantitySold = triplet.second, revenue = triplet.third)
        }.sortedByDescending { it.quantitySold }.take(5)

        return SalesSummary(stallId = stallId, date = filterDate, totalOrders = stallOrders.size, completedOrders = completed.size, cancelledOrders = cancelled.size, rejectedOrders = rejected.size, totalRevenue = totalRevenue, topSellingItems = topSelling)
    }
}