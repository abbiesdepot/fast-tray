package com.abbie.fast_tray.models
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int,
    val studentId: Int,
    val stallId: Int,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val pickupTime: String,
    val totalPrice: Double,
    val rejectionReason: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

enum class OrderStatus {
    PENDING,       // Just submitted by student, waiting for stall owner
    ACCEPTED,      // Stall owner accepted, now preparing
    PREPARING,     // Food is being made
    READY,         // Food is ready for pickup
    COMPLETED,     // Student picked up
    CANCELLED,     // Cancelled by student
    REJECTED       // Rejected by stall owner
}
