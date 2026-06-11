package com.abbie.fast_tray.models
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
    val id: Int,
    val stallId: Int,
    val name: String,
    val description: String? = "",
    val price: Double,
    val imageUrl: String? = "",
    val category: String? = "",
    val isAvailable: Boolean = true
)

