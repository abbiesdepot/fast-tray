package com.abbie.fast_tray.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abbie.fast_tray.models.UserRole
import com.abbie.fast_tray.ui.theme.*
import com.abbie.fast_tray.viewmodels.MainViewModel

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onNavigateToStudent: () -> Unit,
    onNavigateToOwner: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    var isLoginTab by remember { mutableStateOf(true) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDark),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "Fast Tray Logo",
                    tint = OrangePrimary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fast Tray",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Campus Food Court",
                    color = SlateLight,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TabRow(
            selectedTabIndex = if (isLoginTab) 0 else 1,
            containerColor = Color.Transparent,
            contentColor = OrangePrimary
        ) {
            Tab(
                selected = isLoginTab,
                onClick = {
                    isLoginTab = true
                    errorMessage = null
                },
                text = { Text("Log In", color = if (isLoginTab) OrangePrimary else SlateMedium, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = !isLoginTab,
                onClick = {
                    isLoginTab = false
                    errorMessage = null
                },
                text = { Text("Register", color = if (!isLoginTab) OrangePrimary else SlateMedium, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!isLoginTab) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = ColorDanger,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank() || (!isLoginTab && name.isBlank())) {
                    errorMessage = "Please fill in all fields"
                    return@Button
                }
                
                isLoading = true
                errorMessage = null

                if (isLoginTab) {
                    viewModel.loginWithPassword(email, password) { success, msg ->
                        isLoading = false
                        if (success) {
                            when (viewModel.currentRole.value) {
                                UserRole.STUDENT -> onNavigateToStudent()
                                UserRole.STALL_OWNER -> onNavigateToOwner()
                                UserRole.ADMIN -> onNavigateToAdmin()
                                null -> errorMessage = "Unknown role"
                            }
                        } else {
                            errorMessage = msg ?: "Login failed"
                        }
                    }
                } else {
                    viewModel.registerStudent(name, email, password) { success, msg ->
                        isLoading = false
                        if (success) {
                            onNavigateToStudent()
                        } else {
                            errorMessage = msg ?: "Registration failed"
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (isLoginTab) "Log in" else "Register as Student",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
