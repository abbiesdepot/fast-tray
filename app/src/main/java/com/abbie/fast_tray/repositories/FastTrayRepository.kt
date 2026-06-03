package com.abbie.fast_tray.repositories

import com.abbie.fast_tray.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FastTrayRepository {

    private fun currentDateString(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // MOCK
    private val _users = MutableStateFlow<List<User>>(
        listOf(
            User(id = 1, name = "Alex Rivera", email = "alex.rivera@campus.edu", role = UserRole.STUDENT, warningCount = 0),
            User(id = 2, name = "Sarah Chen", email = "sarah.chen@campus.edu", role = UserRole.STUDENT, warningCount = 1),
            User(id = 3, name = "Jordan Smith", email = "jordan.smith@campus.edu", role = UserRole.STUDENT, warningCount = 2), // 1 warning away from ban
            User(id = 4, name = "Prof. Henderson", email = "henderson@campus.edu", role = UserRole.STUDENT, warningCount = 0),
            User(id = 5, name = "Chef Marcus", email = "marcus@fasttray.com", role = UserRole.STALL_OWNER),
            User(id = 6, name = "Chef Mei", email = "mei@fasttray.com", role = UserRole.STALL_OWNER),
            User(id = 7, name = "Admin Abbie", email = "admin@fasttray.com", role = UserRole.ADMIN)
        )
    )
    val users: StateFlow<List<User>> = _users.asStateFlow()

    // MOCK Stalls
    private val _stalls = MutableStateFlow<List<Stall>>(
        listOf(
            Stall(id = 1, ownerId = 5, name = "The Faculty Grill", description = "High-protein, brain-fueling ingredients for the rigorous academic environment.", location = "Block A, Level 1", imageUrl = "faculty_grill"),
            Stall(id = 2, ownerId = 5, name = "The Scholar's Grill", description = "Premium artisanal burgers and hand-cut fries designed for peak cognitive performance.", location = "Block A, Level 1", imageUrl = "scholars_grill"),
            Stall(id = 3, ownerId = 6, name = "Asian Fusion Hub", description = "Authentic wok-fried rice and savory noodles made fresh.", location = "Food Court, Stall 02", imageUrl = "asian_fusion"),
            Stall(id = 4, ownerId = 6, name = "Dean's List Delights", description = "Healthy green bowls and organic refreshments for focused study sessions.", location = "Main Plaza, Stall 04", imageUrl = "deans_delights"),
            Stall(id = 5, ownerId = 5, name = "Espresso Economics", description = "Sleek coffee, pastries, and quick sandwiches.", location = "Block B, Level 2", imageUrl = "espresso_economics"),
            Stall(id = 6, ownerId = 6, name = "The Library Lounge", description = "Central library cafe offering hot tea and light wraps.", location = "Central Library Annex", imageUrl = "library_lounge", isActive = false)
        )
    )
    val stalls: StateFlow<List<Stall>> = _stalls.asStateFlow()

    // Mock Menu Items + FULL DUMMY DATA NDAPERLU DIBACA
    private val _menuItems = MutableStateFlow<List<MenuItem>>(
        listOf(
            // The Faculty Grill (Stall 1)
            MenuItem(id = 1, stallId = 1, name = "Dean's List Double", description = "Two 100% grass-fed beef patties, aged cheddar, balsamic caramelized onions, and house-made aioli on a brioche bun.", price = 11.50, category = "Popular Choices"),
            MenuItem(id = 2, stallId = 1, name = "PhD Protein Bowl", description = "Quinoa base, grilled lemon-herb chicken breast, roasted kale, chickpeas, avocado, and a light tahini lemon dressing.", price = 12.00, category = "Popular Choices"),
            MenuItem(id = 3, stallId = 1, name = "The Thesis Defense", description = "Spicy jalapeno-infused beef, pepper jack cheese, and hot chili relish for when you need a jolt.", price = 12.50, category = "Signature Burgers"),
            MenuItem(id = 4, stallId = 1, name = "Curriculum Vitae", description = "A classic cheeseburger with lettuce, tomato, and pickles. The foundation experience of any campus meal.", price = 9.50, category = "Signature Burgers"),
            MenuItem(id = 5, stallId = 1, name = "Adjunct Avocado", description = "Plant-based patty, smashed avocado, sprouts, and vegan lime crema. Sustaining and sustainable.", price = 10.50, category = "Signature Burgers"),

            // The Scholar's Grill (Stall 2)
            MenuItem(id = 6, stallId = 2, name = "Classic Scholar Burger", description = "Single patty, cheddar, lettuce, tomato, house sauce.", price = 8.50, category = "Burgers"),
            MenuItem(id = 7, stallId = 2, name = "Truffle Mushroom Burger", description = "Beef patty, sautéed wild mushrooms, swiss cheese, truffle oil drizzle.", price = 11.00, category = "Burgers"),
            MenuItem(id = 8, stallId = 2, name = "Hand-Cut Garlic Fries", description = "Crispy double-cooked russet potatoes with minced garlic and rosemary.", price = 4.00, category = "Sides"),

            // Asian Fusion Hub (Stall 3)
            MenuItem(id = 9, stallId = 3, name = "Spicy Chicken Donburi", description = "Tender chicken glazed in sweet-spicy teriyaki over hot jasmine rice with scallions.", price = 9.50, category = "Rice Bowls"),
            MenuItem(id = 10, stallId = 3, name = "Golden Fried Gyoza", description = "Five crispy pork dumplings served with savory soy-vinegar dipping sauce.", price = 5.50, category = "Appetizers"),

            // Dean's List Delights (Stall 4)
            MenuItem(id = 11, stallId = 4, name = "Veggie Wrap", description = "Spinach tortilla, hummus, cucumber, shredded carrots, bell peppers, baby spinach.", price = 8.25, category = "Wraps"),
            MenuItem(id = 12, stallId = 4, name = "Iced Matcha Latte", description = "Premium grade Uji matcha with oat milk and honey over ice.", price = 5.50, category = "Drinks")
        )
    )
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    // skrg & dulu orders
    private val _orders = MutableStateFlow<List<Order>>(
        listOf(
            Order(
                id = 1001,
                studentId = 1,
                stallId = 1,
                items = listOf(
                    OrderItem(id = 1, orderId = 1001, menuItemId = 1, menuItemName = "Dean's List Double", menuItemPrice = 11.50, quantity = 2, notes = ""),
                    OrderItem(id = 2, orderId = 1001, menuItemId = 8, menuItemName = "Hand-Cut Garlic Fries", menuItemPrice = 4.00, quantity = 1, notes = "NO SALT")
                ),
                status = OrderStatus.PENDING,
                pickupTime = "12:15 PM",
                totalPrice = 27.00,
                createdAt = currentDateString() + " 12:00:00"
            ),
            Order(
                id = 1002,
                studentId = 3,
                stallId = 4,
                items = listOf(
                    OrderItem(id = 3, orderId = 1002, menuItemId = 11, menuItemName = "Veggie Wrap", menuItemPrice = 8.25, quantity = 1, notes = "")
                ),
                status = OrderStatus.PENDING,
                pickupTime = "12:30 PM",
                totalPrice = 8.25,
                createdAt = currentDateString() + " 12:05:00"
            ),
            Order(
                id = 1003,
                studentId = 4,
                stallId = 1,
                items = listOf(
                    OrderItem(id = 4, orderId = 1003, menuItemId = 3, menuItemName = "The Thesis Defense", menuItemPrice = 12.50, quantity = 1, notes = "EXTRA SPICY"),
                    OrderItem(id = 5, orderId = 1003, menuItemId = 12, menuItemName = "Iced Matcha Latte", menuItemPrice = 5.50, quantity = 1, notes = "OAT MILK")
                ),
                status = OrderStatus.PREPARING,
                pickupTime = "12:45 PM",
                totalPrice = 18.00,
                createdAt = currentDateString() + " 12:10:00"
            ),
            Order(
                id = 1004,
                studentId = 2,
                stallId = 1,
                items = listOf(
                    OrderItem(id = 6, orderId = 1004, menuItemId = 2, menuItemName = "PhD Protein Bowl", menuItemPrice = 12.00, quantity = 2, notes = "DRESSING ON SIDE")
                ),
                status = OrderStatus.COMPLETED,
                pickupTime = "11:30 AM",
                totalPrice = 24.00,
                createdAt = currentDateString() + " 11:15:00"
            )
        )
    )
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

//    USER
    fun login(username: String, role: UserRole): User? {
        val normalizedUsername = username.trim()
        val existing = _users.value.firstOrNull {
            it.name.equals(normalizedUsername, ignoreCase = true) && it.role == role
        }
        if (existing != null) {
            if (existing.isBanned) return null // user kebanned
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

    fun warnUser(userId: Int): User? {
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

    fun toggleBanUser(userId: Int): User? {
        var updatedUser: User? = null
        _users.value = _users.value.map { user ->
            if (user.id == userId) {
                val isBanned = !user.isBanned
                // if unban, also reset warning jd mrk g keban again
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

//STALL

    fun addStall(name: String, description: String, location: String, ownerId: Int): Stall {
        val newId = (_stalls.value.maxOfOrNull { it.id } ?: 0) + 1
        val newStall = Stall(
            id = newId,
            ownerId = ownerId,
            name = name,
            description = description,
            location = location,
            isActive = true
        )
        _stalls.value = _stalls.value + newStall
        return newStall
    }

    @Suppress("unused")
    fun updateStall(stallId: Int, name: String, description: String, location: String): Stall? {
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

    fun toggleStallActive(stallId: Int): Stall? {
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

//menu item

    fun addMenuItem(stallId: Int, name: String, description: String, price: Double, category: String): MenuItem {
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

    fun updateMenuItem(itemId: Int, name: String, description: String, price: Double, category: String): MenuItem? {
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

    fun toggleMenuItemAvailable(itemId: Int): MenuItem? {
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

    //ORDER

    fun placeOrder(studentId: Int, stallId: Int, cartItems: List<CartItem>, pickupTime: String): Order? {
        //CHEKC WARN STATS
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
        return newOrder
    }

    fun updateOrderStatus(orderId: Int, newStatus: OrderStatus, rejectionReason: String = ""): Boolean {
        var success = false
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val current = order.status
                val isTransitionAllowed = when (current) {
                    OrderStatus.PENDING -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.REJECTED || newStatus == OrderStatus.CANCELLED
                    OrderStatus.ACCEPTED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED
                    OrderStatus.PREPARING -> newStatus == OrderStatus.READY
                    OrderStatus.READY -> newStatus == OrderStatus.COMPLETED
                    else -> false // COMPLETED, REJECTED, CANCELLED R terminal
                }

                if (isTransitionAllowed) {
                    success = true
                    val updatedOrder = order.copy(
                        status = newStatus,
                        rejectionReason = if (newStatus == OrderStatus.REJECTED) rejectionReason else order.rejectionReason,
                        updatedAt = currentDateString() + " 12:00:00"
                    )


                    if (newStatus == OrderStatus.CANCELLED) {
                        incrementWarningCount(order.studentId)
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

    private fun incrementWarningCount(studentId: Int) {
        warnUser(studentId)
    }

    //SALES SUMMARY

    fun getSalesSummary(stallId: Int, filterDate: String): SalesSummary {
        val stallOrders = _orders.value.filter {
            it.stallId == stallId && it.createdAt.startsWith(filterDate)
        }

        val completed = stallOrders.filter { it.status == OrderStatus.COMPLETED }
        val cancelled = stallOrders.filter { it.status == OrderStatus.CANCELLED }
        val rejected = stallOrders.filter { it.status == OrderStatus.REJECTED }
        val totalRevenue = completed.sumOf { it.totalPrice }

        // buat topselling items
        val itemSalesMap = mutableMapOf<Int, Triple<String, Int, Double>>() // ID -> (Name, Qty, Revenue)
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
