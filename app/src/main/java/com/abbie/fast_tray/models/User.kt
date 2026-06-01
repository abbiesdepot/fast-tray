package com.abbie.fast_tray.models
import kotlinx.serialization.Serializable

@Serializable

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val warningCount: Int = 0,
    val isBanned: Boolean = false,
    val createdAt: String = ""
)

enum class UserRole {
    STUDENT,
    STALL_OWNER,
    ADMIN
}