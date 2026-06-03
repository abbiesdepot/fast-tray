package com.abbie.fast_tray.models

data class OrderRequest(
    val studentId: Int,
    val stallId: Int,
    val pickupTime: String,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val menuItemId: Int,
    val quantity: Int,
    val notes: String? = ""
)