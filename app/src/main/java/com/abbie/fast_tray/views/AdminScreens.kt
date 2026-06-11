package com.abbie.fast_tray.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.abbie.fast_tray.models.*
import com.abbie.fast_tray.ui.theme.*
import com.abbie.fast_tray.viewmodels.MainViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    var currentTab by rememberSaveable { mutableStateOf("users") }

    AdminScaffold(
        viewModel = viewModel,
        currentTab = currentTab,
        onTabSelected = { currentTab = it },
        onLogout = onLogout,
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (currentTab) {
                    "users" -> AdminUserManagementScreen(viewModel = viewModel)
                    "stalls" -> AdminStallManagementScreen(viewModel = viewModel)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER", "DEPRECATION")
@Composable
fun AdminScaffold(
    viewModel: MainViewModel,
    currentTab: String,
    onTabSelected: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Admin Panel",
                            tint = OrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fast Tray Admin",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log Out",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateDark)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SlateDark,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = currentTab == "users",
                    onClick = { onTabSelected("users") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Users") },
                    label = { Text("Users") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        unselectedIconColor = SlateLight,
                        unselectedTextColor = SlateLight,
                        indicatorColor = SlateDark
                    )
                )
                NavigationBarItem(
                    selected = currentTab == "stalls",
                    onClick = { onTabSelected("stalls") },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Stalls") },
                    label = { Text("Stalls") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        unselectedIconColor = SlateLight,
                        unselectedTextColor = SlateLight,
                        indicatorColor = SlateDark
                    )
                )
            }
        },
        content = content
    )
}

// User Management Screen
@Composable
fun AdminUserManagementScreen(
    viewModel: MainViewModel
) {
    val users by viewModel.users.collectAsState()
    val context = LocalContext.current
    var showRegisterOwnerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "User Accounts",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMedium
                )
                Text(
                    text = "Warnings and suspension manager",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }
            
            Button(
                onClick = { showRegisterOwnerDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Owner", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Owner", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SlateMedium
                                )
                                Text(
                                    text = "${user.role.name}  •  ${user.email}",
                                    fontSize = 12.sp,
                                    color = SlateLight
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (user.isBanned) ColorDanger.copy(alpha = 0.15f) else ColorSuccess.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (user.isBanned) "BANNED" else "ACTIVE",
                                    color = if (user.isBanned) ColorDanger else ColorSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderColor)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Warnings: ${user.warningCount}/3",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (user.warningCount >= 2) ColorDanger else SlateMedium
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (user.role == UserRole.STUDENT) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.warnUserAccount(user.id)
                                            Toast.makeText(context, "Warning issued.", Toast.LENGTH_SHORT).show()
                                        },
                                        border = BorderStroke(1.dp, ColorWarning),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorWarning),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text(text = "Warn", fontSize = 11.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        viewModel.toggleBanUserAccount(user.id)
                                        val status = if (!user.isBanned) "banned" else "unbanned"
                                        Toast.makeText(context, "User $status.", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (user.isBanned) ColorSuccess else ColorDanger),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(
                                        text = if (user.isBanned) "Unban" else "Ban",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRegisterOwnerDialog) {
        var ownerName by remember { mutableStateOf("") }
        var ownerEmail by remember { mutableStateOf("") }
        var ownerPass by remember { mutableStateOf("") }
        var registerError by remember { mutableStateOf<String?>(null) }
        var isSubmitting by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showRegisterOwnerDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Register Stall Owner",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SlateMedium
                    )

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ownerEmail,
                        onValueChange = { ownerEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ownerPass,
                        onValueChange = { ownerPass = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (registerError != null) {
                        Text(text = registerError!!, color = ColorDanger, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRegisterOwnerDialog = false },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (ownerName.isBlank() || ownerEmail.isBlank() || ownerPass.isBlank()) {
                                    registerError = "Please fill in all details."
                                    return@Button
                                }
                                isSubmitting = true
                                viewModel.registerOwner(ownerName, ownerEmail, ownerPass) { success, msg ->
                                    isSubmitting = false
                                    if (success) {
                                        showRegisterOwnerDialog = false
                                        Toast.makeText(context, "Owner registered.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        registerError = msg ?: "Failed to register."
                                    }
                                }
                            },
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text("Register", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Stall Management Screen
@Composable
fun AdminStallManagementScreen(
    viewModel: MainViewModel
) {
    val stalls by viewModel.stalls.collectAsState()
    val users by viewModel.users.collectAsState()
    var showRegisterDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Food Stalls",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMedium
                )
                Text(
                    text = "Manage registered campus outlets",
                    fontSize = 12.sp,
                    color = SlateLight
                )
            }

            Button(
                onClick = { showRegisterDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Stall", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Register Stall", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f).fillMaxWidth() // FIX: Keeps full layout flexible dynamically
        ) {
            items(stalls) { stall ->
                val owner = users.firstOrNull { it.id == stall.ownerId }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stall.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SlateMedium
                                )
                                Text(
                                    text = "Location: ${stall.location}",
                                    fontSize = 12.sp,
                                    color = SlateLight
                                )
                                Text(
                                    text = "Owner: ${owner?.name ?: "No Owner Assigned"}",
                                    fontSize = 12.sp,
                                    color = SlateLight
                                )
                            }

                            Switch(
                                checked = stall.isActive,
                                onCheckedChange = { viewModel.toggleStallOpenClosed(stall.id) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = OrangePrimary,
                                    checkedTrackColor = OrangeLight
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stall.description ?: "",
                            fontSize = 12.sp,
                            color = SlateMedium,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }

    if (showRegisterDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var loc by remember { mutableStateOf("") }

        val owners = users.filter { it.role == UserRole.STALL_OWNER }
        var selectedOwner by remember { mutableStateOf(owners.firstOrNull()) }
        var ownerDropdownExpanded by remember { mutableStateOf(false) }

        var validationErr by remember { mutableStateOf(false) }
        val context = LocalContext.current

        Dialog(onDismissRequest = { showRegisterDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Register Food Stall",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SlateMedium
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Stall Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = loc,
                        onValueChange = { loc = it },
                        label = { Text("Location (e.g. Block C, Level 1)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { ownerDropdownExpanded = true },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Owner: ${selectedOwner?.name ?: "Select Owner"}", color = SlateMedium)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand", tint = SlateMedium)
                            }
                        }

                        DropdownMenu(
                            expanded = ownerDropdownExpanded,
                            onDismissRequest = { ownerDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f).align(Alignment.TopStart) // FIX: Stabilizes overlay alignment
                        ) {
                            owners.forEach { own ->
                                DropdownMenuItem(
                                    text = { Text(own.name) },
                                    onClick = {
                                        selectedOwner = own
                                        ownerDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (validationErr) {
                        Text(text = "Please fill in all details.", color = ColorDanger, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRegisterDialog = false },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (name.isNotBlank() && desc.isNotBlank() && loc.isNotBlank() && selectedOwner != null) {
                                    viewModel.registerNewStall(name, desc, loc, selectedOwner!!.id)
                                    showRegisterDialog = false
                                    Toast.makeText(context, "Stall registered.", Toast.LENGTH_SHORT).show()
                                } else {
                                    validationErr = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Register", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}