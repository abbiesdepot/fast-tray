package com.abbie.fast_tray

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abbie.fast_tray.ui.theme.FasttrayTheme
import com.abbie.fast_tray.viewmodels.MainViewModel
import com.abbie.fast_tray.views.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = remember { MainViewModel() }
            val navController = rememberNavController()

            FasttrayTheme {
                NavHost(
                    navController = navController,
                    startDestination = "role_selection"
                ) {
                    // --- ROLE SELECTION ---
                    composable("role_selection") {
                        RoleSelectionScreen(
                            viewModel = viewModel,
                            onNavigateToStudent = { navController.navigate("student_home") },
                            onNavigateToOwner = { navController.navigate("owner_home") },
                            onNavigateToAdmin = { navController.navigate("admin_home") }
                        )
                    }

                    // --- STUDENT FLOW ---
                    composable("student_home") {
                        var currentTab by rememberSaveable { mutableStateOf("stalls") }

                        StudentScaffold(
                            viewModel = viewModel,
                            currentScreen = currentTab,
                            onNavigate = { currentTab = it },
                            onLogout = {
                                navController.navigate("role_selection") {
                                    popUpTo("student_home") { inclusive = true }
                                }
                            },
                            content = { padding ->
                                Box(modifier = Modifier.padding(padding)) {
                                    when (currentTab) {
                                        "stalls" -> BrowseStallsScreen(
                                            viewModel = viewModel,
                                            onNavigateToStall = { id ->
                                                navController.navigate("stall_details/$id")
                                            }
                                        )
                                        "cart" -> {
                                            // FIXED: Supplied the expected navigation parameter for tracking after checkout
                                            CartScreen(
                                                viewModel = viewModel,
                                                onNavigateToTracking = { orderId ->
                                                    navController.navigate("order_tracking/$orderId") {
                                                        // Clears checkout screens out of history so they can't double-back click
                                                        popUpTo("student_home") { inclusive = false }
                                                    }
                                                }
                                            )
                                        }
                                        "history" -> OrderHistoryScreen(
                                            viewModel = viewModel,
                                            onNavigateToTracking = { id ->
                                                navController.navigate("order_tracking/$id")
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }

                    composable("stall_details/{stallId}") { backStackEntry ->
                        val stallId = backStackEntry.arguments?.getString("stallId")?.toIntOrNull() ?: 0

                        StallDetailScreen(
                            stallId = stallId,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToCart = {
                                navController.navigate("student_home") {
                                    popUpTo("student_home") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable("order_tracking/{orderId}") { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull() ?: 0

                        OrderTrackingScreen(
                            orderId = orderId,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // --- STALL OWNER FLOW ---
                    composable("owner_home") {
                        var currentTab by rememberSaveable { mutableStateOf("queue") }

                        OwnerScaffold(
                            viewModel = viewModel,
                            currentScreen = currentTab,
                            onNavigate = { currentTab = it },
                            onLogout = {
                                navController.navigate("role_selection") {
                                    popUpTo("owner_home") { inclusive = true }
                                }
                            },
                            content = { padding ->
                                Box(modifier = Modifier.padding(padding)) {
                                    when (currentTab) {
                                        "queue" -> OrderQueueScreen(
                                            viewModel = viewModel,
                                            onNavigateToOrderDetails = { orderId ->
                                                navController.navigate("order_details/$orderId")
                                            }
                                        )
                                        "menu" -> {
                                            MenuManagementScreen(
                                                viewModel = viewModel,
                                                onNavigateToAddEdit = { itemId ->
                                                    if (itemId != null) {
                                                        navController.navigate("add_edit_menu_item/$itemId")
                                                    } else {
                                                        navController.navigate("add_edit_menu_item")
                                                    }
                                                }
                                            )
                                        }
                                        "summary" -> SalesSummaryScreen(viewModel = viewModel)
                                    }
                                }
                            }
                        )
                    }

                    composable("order_details/{orderId}") { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull() ?: 0

                        OrderDetailScreen(
                            orderId = orderId,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("add_edit_menu_item") {
                        AddEditFoodItemScreen(
                            itemId = null,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("add_edit_menu_item/{itemId}") { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
                        AddEditFoodItemScreen(
                            itemId = itemId,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // --- SYSTEM ADMIN FLOW ---
                    composable("admin_home") {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            onLogout = {
                                navController.navigate("role_selection") {
                                    popUpTo("admin_home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "$title Coming Soon")
    }
}