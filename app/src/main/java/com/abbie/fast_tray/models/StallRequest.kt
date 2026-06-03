package com.abbie.fast_tray.models

data class StallRequest(
    val ownerId: Int,
    val name: String,
    val location: String, // Di Zod wajib (min 1, max 100)
    val description: String? = null,
    val imageUrl: String? = null,
    val isActive: Boolean? = null,
    val openingHour: String = "06:00", // Default dari Zod
    val closingHour: String = "21:00"  // Default dari Zod
)