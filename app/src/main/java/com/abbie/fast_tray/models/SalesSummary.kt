package com.abbie.fast_tray.models

import kotlinx.serialization.Serializable

@Serializable
data class SalesSummary(
    val stallId: Int,
    val date: String,
    val totalOrders: Int,
    val completedOrders: Int,
    val cancelledOrders: Int,
    val rejectedOrders: Int,
    val totalRevenue: Double,
    val topSellingItems: List<TopSellingItem>
)

@Serializable
data class TopSellingItem(
    val menuItemId: Int,
    val menuItemName: String,
    val quantitySold: Int,
    val revenue: Double
)