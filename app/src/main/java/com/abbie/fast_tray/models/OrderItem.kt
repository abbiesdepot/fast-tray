package com.abbie.fast_tray.models
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: Int,
    val orderId: Int,
    val menuItemId: Int,
    val menuItemName: String,
    val menuItemPrice: Double,
    val quantity: Int,
    val notes: String = "",
    val subtotal: Double = menuItemPrice * quantity
)
