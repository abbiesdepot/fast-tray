package com.abbie.fast_tray.routes

sealed class Screen(val route: String) {
    object RoleSelection : Screen("role_selection")
    
    // buat student ui
    object StudentHome : Screen("student_home")
    object StallDetail : Screen("stall_detail/{stallId}") {
        fun createRoute(stallId: Int) = "stall_detail/$stallId"
    }
    object Cart : Screen("cart")
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: Int) = "order_tracking/$orderId"
    }
    object OrderHistory : Screen("order_history")
    
    // stall owner ui
    object OwnerDashboard : Screen("owner_dashboard")
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: Int) = "order_detail/$orderId"
    }
    object MenuManagement : Screen("menu_management")
    object AddEditFoodItem : Screen("add_edit_food_item?itemId={itemId}") {
        fun createRoute(itemId: Int? = null) = if (itemId != null) "add_edit_food_item?itemId=$itemId" else "add_edit_food_item"
    }
    object SalesSummary : Screen("sales_summary")
    
    // admin ui
    object AdminDashboard : Screen("admin_dashboard")
}
