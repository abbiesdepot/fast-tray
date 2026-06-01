package com.abbie.fast_tray.models
import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int,
    val notes: String = ""
)