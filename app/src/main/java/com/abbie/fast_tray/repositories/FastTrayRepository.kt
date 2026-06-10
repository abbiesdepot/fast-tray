package com.abbie.fast_tray.repositories

import com.abbie.fast_tray.models.*
import com.abbie.fast_tray.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FastTrayRepository {

    private val api = RetrofitClient.instance

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _stalls = MutableStateFlow<List<Stall>>(emptyList())
    val stalls: StateFlow<List<Stall>> = _stalls.asStateFlow()

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // --- Fetch Methods ---

    suspend fun fetchUsers() {
        try {
            val response = api.getUsers()
            if (response.isSuccessful) {
                _users.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchStalls() {
        try {
            val response = api.getStalls()
            if (response.isSuccessful) {
                _stalls.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchMenuItems(stallId: Int) {
        try {
            val response = api.getMenuItems(stallId)
            if (response.isSuccessful) {
                _menuItems.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchStudentOrders(studentId: Int) {
        try {
            val response = api.getStudentOrders(studentId)
            if (response.isSuccessful) {
                _orders.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchStallQueue(stallId: Int) {
        try {
            val response = api.getStallQueue(stallId)
            if (response.isSuccessful) {
                _orders.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Actions ---

    suspend fun login(email: String, password: String): User? {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) response.body()?.data else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(name: String, email: String, password: String, role: UserRole): User? {
        return try {
            val response = api.register(RegisterRequest(name, email, password, role.name))
            if (response.isSuccessful) {
                val newUser = response.body()?.data
                fetchUsers()
                newUser
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun warnUser(userId: Int): User? {
        val response = api.warnUser(userId)
        return if (response.isSuccessful) {
            val updated = response.body()?.data
            fetchUsers()
            updated
        } else null
    }

    suspend fun toggleBanUser(userId: Int): User? {
        val response = api.banUser(userId)
        return if (response.isSuccessful) {
            val updated = response.body()?.data
            fetchUsers()
            updated
        } else null
    }

    suspend fun addStall(name: String, description: String, location: String, ownerId: Int): Stall? {
        val response = api.createStall(CreateStallRequest(name, description, location, ownerId))
        return if (response.isSuccessful) {
            val newStall = response.body()?.data
            fetchStalls()
            newStall
        } else null
    }

    suspend fun toggleStallActive(stallId: Int): Stall? {
        val response = api.toggleStall(stallId)
        return if (response.isSuccessful) {
            val updated = response.body()?.data
            fetchStalls()
            updated
        } else null
    }

    suspend fun addMenuItem(stallId: Int, name: String, description: String, price: Double, category: String): MenuItem? {
        val response = api.addMenuItem(stallId, MenuItemRequest(name, description, price, category))
        return if (response.isSuccessful) {
            val newItem = response.body()?.data
            fetchMenuItems(stallId)
            newItem
        } else null
    }

    suspend fun updateMenuItem(itemId: Int, stallId: Int, name: String, description: String, price: Double, category: String): MenuItem? {
        val response = api.updateMenuItem(itemId, MenuItemRequest(name, description, price, category))
        return if (response.isSuccessful) {
            val updated = response.body()?.data
            fetchMenuItems(stallId)
            updated
        } else null
    }

    suspend fun toggleMenuItemAvailable(itemId: Int, stallId: Int): MenuItem? {
        val response = api.toggleMenuItemAvailability(itemId)
        return if (response.isSuccessful) {
            val updated = response.body()?.data
            fetchMenuItems(stallId)
            updated
        } else null
    }

    suspend fun placeOrder(studentId: Int, stallId: Int, cartItems: List<CartItem>, pickupTime: String): Order? {
        val items = cartItems.map { OrderItemsRequest(it.menuItem.id, it.quantity, it.notes) }
        val response = api.placeOrder(PlaceOrderRequest(studentId, stallId, items, pickupTime))
        return if (response.isSuccessful) {
            val order = response.body()?.data
            fetchStudentOrders(studentId)
            order
        } else null
    }

    suspend fun updateOrderStatus(orderId: Int, stallId: Int, newStatus: OrderStatus, rejectionReason: String = "" ): Boolean {
        val response = api.updateOrderStatus(orderId, UpdateStatusRequest(newStatus.name, rejectionReason))
        if (response.isSuccessful) {
            fetchStallQueue(stallId)
            return true
        }
        return false
    }

    suspend fun getSalesSummary(stallId: Int): SalesSummary? {
        val response = api.getSalesSummary(stallId)
        return if (response.isSuccessful) response.body()?.data else null
    }
}
