package com.abbie.fast_tray.models
import kotlinx.serialization.Serializable

@Serializable
data class Stall(
    val id: Int,
    val ownerId: Int,
    val name: String,
    val description: String,
    val imageUrl: String = "",
    val location: String,
    val isActive: Boolean = true,
    val openingHour: String = "06:00",
    val closingHour: String = "21:00",
    val createdAt: String = ""
)
