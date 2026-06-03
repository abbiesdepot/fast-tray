package com.abbie.fast_tray

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
                    composable("role_selection") {
                        RoleSelectionScreen(
                            viewModel = viewModel,
                            onNavigateToStudent = { navController.navigate("student_home") },
                            onNavigateToOwner = { navController.navigate("owner_home") },
                            onNavigateToAdmin = { navController.navigate("admin_home") }
                        )
                    }

                    composable("student_home") {
                        var currentTab by rememberSaveable { mutableStateOf("stalls") }

                        StudentScaffold(
                            viewModel = viewModel,
                            currentScreen = currentTab,
                            onNavigate = { currentTab = it },
                            onLogout = { navController.popBackStack("role_selection", false) },
                            content = { padding ->
                                when (currentTab) {
                                    "stalls" -> BrowseStallsScreen(
                                        viewModel = viewModel,
                                        onNavigateToStall = { id -> navController.navigate("stall_details/$id") }
                                    )

                                    "cart" -> Text("Cart Screen Placeholder") // Replace with CartScreen(viewModel)???
                                    "history" -> Text("History Screen Placeholder")
                                }
                            }
                        )
                    }

                    composable("owner_home") {
                        var currentTab by remember { mutableStateOf("queue") }

                        OwnerScaffold(
                            viewModel = viewModel,
                            currentScreen = currentTab,
                            onNavigate = { currentTab = it },
                            onLogout = { navController.popBackStack("role_selection", false) },
                            content = { padding ->
                                when (currentTab) {
                                    "queue" -> OrderQueueScreen(
                                        viewModel = viewModel,
                                        onNavigateToOrderDetails = { /* ?????? */ }
                                    )

                                    "menu" -> Text("Menu Management Placeholder")
                                    "summary" -> Text("Sales Summary Placeholder")
                                }
                            }
                        )
                    }

                    composable("admin_home") {
                        var currentTab by rememberSaveable { mutableStateOf("users") }

                        AdminScaffold(
                            viewModel = viewModel,
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it },
                            onLogout = { navController.popBackStack("role_selection", false) },
                            content = { padding ->
                                when (currentTab) {
                                    "users" -> AdminUserManagementScreen(viewModel = viewModel)
                                    "stalls" -> AdminStallManagementScreen(viewModel = viewModel)
                                }
                            }
                        )
                    }

                    composable("stall_details/{stallId}") { backStackEntry ->
                        val stallId =
                            backStackEntry.arguments?.getString("stallId")?.toIntOrNull() ?: 0

                        StallDetailScreen(
                            stallId = stallId,
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToCart = {
                                navController.navigate("student_home") {
                                    popUpTo("student_home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}