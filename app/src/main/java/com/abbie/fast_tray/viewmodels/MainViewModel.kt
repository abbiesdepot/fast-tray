package com.abbie.fast_tray.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abbie.fast_tray.models.*
import com.abbie.fast_tray.repositories.FastTrayRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("unused")
class MainViewModel : ViewModel() {

    private val repository = FastTrayRepository

    // session
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    @Suppress("unused")
    private val _currentRole = MutableStateFlow<UserRole?>(null)
    @Suppress("unused")
    val currentRole: StateFlow<UserRole?> = _currentRole.asStateFlow()

    private val _ownerActiveStallId = MutableStateFlow<Int>(1)
    val ownerActiveStallId: StateFlow<Int> = _ownerActiveStallId.asStateFlow()

    // Flow from repo
    val users: StateFlow<List<User>> = repository.users
    val stalls: StateFlow<List<Stall>> = repository.stalls
    val menuItems: StateFlow<List<MenuItem>> = repository.menuItems
    val orders: StateFlow<List<Order>> = repository.orders

    // Cart
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartStallId = MutableStateFlow<Int?>(null)
    val cartStallId: StateFlow<Int?> = _cartStallId.asStateFlow()

    @Suppress("unused")
    private val _searchQuery = MutableStateFlow("")
    @Suppress("unused")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @Suppress("unused")
    private val _selectedCategory = MutableStateFlow("All")
    @Suppress("unused")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null && user.role == UserRole.STALL_OWNER) {
                    val stall = stalls.value.firstOrNull { it.ownerId == user.id }
                    if (stall != null) {
                        _ownerActiveStallId.value = stall.id
                    }
                }
            }
        }
    }

//SESSION

    fun selectRoleAndUser(role: UserRole, user: User) {
        _currentRole.value = role
        _currentUser.value = user
        clearCart()
    }

    fun loginDemoUser(username: String, role: UserRole): Boolean {
        val user = repository.login(username, role)
        return if (user != null) {
            _currentUser.value = user
            _currentRole.value = role
            clearCart()
            true
        } else {
            false
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentRole.value = null
        clearCart()
    }

//cart stuff

    fun addToCart(item: MenuItem, quantity: Int, notes: String): Boolean {
        if (_cartStallId.value != null && _cartStallId.value != item.stallId) {
            // Cannot add items from different stalls. Caller can prompt to clear cart.
            return false
        }

        if (_cartStallId.value == null) {
            _cartStallId.value = item.stallId
        }

        val existingIndex = _cartItems.value.indexOfFirst { it.menuItem.id == item.id }
        if (existingIndex >= 0) {
            val updatedList = _cartItems.value.toMutableList()
            val existing = updatedList[existingIndex]
            updatedList[existingIndex] = existing.copy(
                quantity = existing.quantity + quantity,
                notes = if (notes.isNotEmpty()) notes else existing.notes
            )
            _cartItems.value = updatedList
        } else {
            _cartItems.value = _cartItems.value + CartItem(menuItem = item, quantity = quantity, notes = notes)
        }
        return true
    }

    fun updateCartQuantity(itemId: Int, quantity: Int) {
        if (quantity <= 0) {
            _cartItems.value = _cartItems.value.filter { it.menuItem.id != itemId }
            if (_cartItems.value.isEmpty()) {
                _cartStallId.value = null
            }
        } else {
            _cartItems.value = _cartItems.value.map {
                if (it.menuItem.id == itemId) it.copy(quantity = quantity) else it
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartStallId.value = null
    }

    fun checkout(pickupTime: String): Order? {
        val student = _currentUser.value ?: return null
        val stallId = _cartStallId.value ?: return null
        if (_cartItems.value.isEmpty()) return null

        val order = repository.placeOrder(
            studentId = student.id,
            stallId = stallId,
            cartItems = _cartItems.value,
            pickupTime = pickupTime
        )
        if (order != null) {
            clearCart()
        }
        return order
    }

//ngesearch

    @Suppress("unused")
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    @Suppress("unused")
    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

//INI ORDER ACTIONS
    fun cancelOrder(orderId: Int): Boolean {
        // Student yg cancel
        return repository.updateOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    fun acceptOrder(orderId: Int): Boolean {
        // Stall Owner accept
        return repository.updateOrderStatus(orderId, OrderStatus.PREPARING)
    }

    fun rejectOrder(orderId: Int, reason: String): Boolean {
        // Stall Owner rejects order pake rsn
        return repository.updateOrderStatus(orderId, OrderStatus.REJECTED, reason)
    }

    fun markOrderAsReady(orderId: Int): Boolean {
        // Stall Owner ready for pickup
        return repository.updateOrderStatus(orderId, OrderStatus.READY)
    }

    fun completeOrder(orderId: Int): Boolean {
        // kl student picks up order yauda status update complete
        return repository.updateOrderStatus(orderId, OrderStatus.COMPLETED)
    }

// PNY STALL OWNER

    @Suppress("unused")
    fun setOwnerActiveStall(stallId: Int) {
        _ownerActiveStallId.value = stallId
    }

    fun toggleStallOpenClosed(stallId: Int) {
        repository.toggleStallActive(stallId)
    }

    fun toggleMenuItemAvailability(itemId: Int) {
        repository.toggleMenuItemAvailable(itemId)
    }

    fun saveMenuItem(itemId: Int?, name: String, description: String, price: Double, category: String) {
        val stallId = _ownerActiveStallId.value
        if (itemId != null) {
            repository.updateMenuItem(itemId, name, description, price, category)
        } else {
            repository.addMenuItem(stallId, name, description, price, category)
        }
    }

    fun getActiveSalesSummary(dateString: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())): SalesSummary {
        val stallId = _ownerActiveStallId.value
        return repository.getSalesSummary(stallId, dateString)
    }

//    INI ADMIN
    fun registerNewStall(name: String, description: String, location: String, ownerId: Int) {
        repository.addStall(name, description, location, ownerId)
    }

    fun warnUserAccount(userId: Int) {
        repository.warnUser(userId)
    }

    fun toggleBanUserAccount(userId: Int) {
        repository.toggleBanUser(userId)
    }
}
