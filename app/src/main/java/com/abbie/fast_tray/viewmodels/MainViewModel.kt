package com.abbie.fast_tray.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abbie.fast_tray.models.*
import com.abbie.fast_tray.network.RetrofitClient
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

    private val _currentRole = MutableStateFlow<UserRole?>(null)
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

    private val _salesSummary = MutableStateFlow<SalesSummary?>(null)
    val salesSummary: StateFlow<SalesSummary?> = _salesSummary.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    // Fetch initial data
                    if (user.role == UserRole.ADMIN) {
                        repository.fetchAdminStalls()
                        repository.fetchAdminUsers()
                    } else {
                        repository.fetchStalls()
                        repository.fetchUsers()
                    }

                    // Fetch relevant data based on user type
                    if (user.role == UserRole.STUDENT) {
                        repository.fetchStudentOrders(user.id)
                    } else if (user.role == UserRole.STALL_OWNER) {
                        val stall = stalls.value.firstOrNull { it.ownerId == user.id }
                        if (stall != null) {
                            _ownerActiveStallId.value = stall.id
                            repository.fetchStallQueue(stall.id)
                            repository.fetchMenuItems(stall.id)
                        }
                    }
                }
            }
        }
    }

//SESSION

    fun loginWithPassword(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(email, pass)
            if (user != null) {
                if (user.isBanned) {
                    onResult(false, "Your account has been banned.")
                } else {
                    _currentUser.value = user
                    _currentRole.value = user.role
                    RetrofitClient.setCurrentUser(user.id, user.role.name, user.email)
                    clearCart()
                    onResult(true, null)
                }
            } else {
                onResult(false, "Invalid email or password.")
            }
        }
    }

    fun registerStudent(name: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val user = repository.register(name, email, pass, UserRole.STUDENT)
            if (user != null) {
                _currentUser.value = user
                _currentRole.value = user.role
                RetrofitClient.setCurrentUser(user.id, user.role.name, user.email)
                clearCart()
                onResult(true, null)
            } else {
                onResult(false, "Registration failed. Email might be in use.")
            }
        }
    }
    
    fun registerOwner(name: String, email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val user = repository.register(name, email, pass, UserRole.STALL_OWNER, isAdmin = true)
            if (user != null) {
                onResult(true, null)
            } else {
                onResult(false, "Registration failed.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentRole.value = null
        RetrofitClient.setCurrentUser(null, null, null)
        clearCart()
    }

//cart stuff

    fun addToCart(item: MenuItem, quantity: Int, notes: String): Boolean {
        if (_cartStallId.value != null && _cartStallId.value != item.stallId) {
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

    fun checkout(pickupTime: String, onOrderPlaced: (Int) -> Unit = {}) {
        val student = _currentUser.value ?: return
        val stallId = _cartStallId.value ?: return
        if (_cartItems.value.isEmpty()) return

        viewModelScope.launch {
            val order = repository.placeOrder(
                studentId = student.id,
                stallId = stallId,
                cartItems = _cartItems.value,
                pickupTime = pickupTime
            )
            if (order != null) {
                clearCart()
                onOrderPlaced(order.id)
            }
        }
    }

//ngesearch

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun fetchMenuItemsForStall(stallId: Int) {
        viewModelScope.launch {
            repository.fetchMenuItems(stallId)
        }
    }

    //INI ORDER ACTIONS
    fun cancelOrder(orderId: Int, stallId: Int) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null && user.role == UserRole.STUDENT) {
                repository.cancelOrderStudent(orderId, user.id)
            } else {
                repository.updateOrderStatus(orderId, stallId, OrderStatus.CANCELLED)
            }
        }
    }

    fun acceptOrder(orderId: Int, stallId: Int) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, stallId, OrderStatus.PREPARING)
        }
    }

    fun rejectOrder(orderId: Int, stallId: Int, reason: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, stallId, OrderStatus.REJECTED, reason)
        }
    }

    fun markOrderAsReady(orderId: Int, stallId: Int) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, stallId, OrderStatus.READY)
        }
    }

    fun completeOrder(orderId: Int, stallId: Int) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null && user.role == UserRole.STUDENT) {
                repository.updateOrderStatus(orderId, stallId, OrderStatus.COMPLETED, studentId = user.id)
            } else {
                repository.updateOrderStatus(orderId, stallId, OrderStatus.COMPLETED)
            }
        }
    }

// PNY STALL OWNER

    fun setOwnerActiveStall(stallId: Int) {
        _ownerActiveStallId.value = stallId
        viewModelScope.launch {
            repository.fetchMenuItems(stallId)
            repository.fetchStallQueue(stallId)
        }
    }

    fun toggleStallOpenClosed(stallId: Int) {
        viewModelScope.launch {
            repository.toggleStallActive(stallId, isAdmin = true)
        }
    }

    fun toggleMenuItemAvailability(itemId: Int) {
        viewModelScope.launch {
            repository.toggleMenuItemAvailable(itemId, _ownerActiveStallId.value)
        }
    }

    fun saveMenuItem(itemId: Int?, name: String, description: String, price: Double, category: String) {
        val stallId = _ownerActiveStallId.value
        viewModelScope.launch {
            if (itemId != null) {
                repository.updateMenuItem(itemId, stallId, name, description, price, category)
            } else {
                repository.addMenuItem(stallId, name, description, price, category)
            }
        }
    }

    fun fetchActiveSalesSummary() {
        viewModelScope.launch {
            val stallId = _ownerActiveStallId.value
            _salesSummary.value = repository.getSalesSummary(stallId)
        }
    }

    //    INI ADMIN
    fun registerNewStall(name: String, description: String, location: String, ownerId: Int) {
        viewModelScope.launch {
            repository.addStall(name, description, location, ownerId, isAdmin = true)
        }
    }

    fun warnUserAccount(userId: Int) {
        viewModelScope.launch {
            repository.warnUser(userId, isAdmin = true)
        }
    }

    fun toggleBanUserAccount(userId: Int) {
        viewModelScope.launch {
            repository.toggleBanUser(userId, isAdmin = true)
        }
    }
}
