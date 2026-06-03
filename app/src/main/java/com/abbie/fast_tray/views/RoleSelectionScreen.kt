package com.abbie.fast_tray.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abbie.fast_tray.models.User
import com.abbie.fast_tray.models.UserRole
import com.abbie.fast_tray.ui.theme.*
import com.abbie.fast_tray.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    viewModel: MainViewModel,
    onNavigateToStudent: () -> Unit,
    onNavigateToOwner: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val users by viewModel.users.collectAsState()
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var customName by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }

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
                    text = "Campus Food Court Ordering System",
                    color = SlateLight,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Select Your Role",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // role cards rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoleCard(
                roleName = "Student",
                icon = Icons.Default.Person,
                isSelected = selectedRole == UserRole.STUDENT,
                modifier = Modifier.weight(1f)
            ) {
                selectedRole = UserRole.STUDENT
                selectedUser = null
                customName = ""
                loginError = null
            }
            RoleCard(
                roleName = "Owner",
                icon = Icons.Default.Fastfood,
                isSelected = selectedRole == UserRole.STALL_OWNER,
                modifier = Modifier.weight(1f)
            ) {
                selectedRole = UserRole.STALL_OWNER
                selectedUser = null
                customName = ""
                loginError = null
            }
            RoleCard(
                roleName = "Admin",
                icon = Icons.Default.AdminPanelSettings,
                isSelected = selectedRole == UserRole.ADMIN,
                modifier = Modifier.weight(1f)
            ) {
                selectedRole = UserRole.ADMIN
                selectedUser = null
                customName = ""
                loginError = null
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedRole != null) {
            Text(
                text = "Choose Demo Account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val filteredUsers = users.filter { it.role == selectedRole }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(WhiteSurface)
                    .padding(8.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredUsers) { user ->
                        val isUserSelected = selectedUser?.id == user.id
                        val userCardColor = if (isUserSelected) OrangeLight else WhiteSurface
                        val userBorderColor = if (isUserSelected) OrangePrimary else BorderColor

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (user.isBanned) {
                                        loginError = "This account is banned due to warning policy."
                                    } else {
                                        selectedUser = user
                                        loginError = null
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = userCardColor),
                            border = BorderStroke(1.dp, userBorderColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = user.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUserSelected) OrangePrimary else SlateMedium
                                    )
                                    Text(
                                        text = user.email,
                                        fontSize = 12.sp,
                                        color = SlateLight
                                    )
                                }
                                if (user.warningCount > 0) {
                                    Badge(
                                        containerColor = if (user.warningCount >= 3) ColorDanger else ColorWarning,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "${user.warningCount} Warnings")
                                    }
                                }
                            }
                        }
                    }

                    if (selectedRole == UserRole.STUDENT) {
                        item {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "Or Create New Student Account",
                                fontSize = 12.sp,
                                color = SlateLight,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customName,
                                onValueChange = {
                                    customName = it
                                    selectedUser = null
                                    loginError = null
                                },
                                label = { Text("Full Name") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangePrimary,
                                    focusedLabelColor = OrangePrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // kl error
            if (loginError != null) {
                Text(
                    text = loginError ?: "",
                    color = ColorDanger,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // launch
            Button(
                onClick = {
                    if (selectedUser != null) {
                        viewModel.selectRoleAndUser(selectedRole!!, selectedUser!!)
                        when (selectedRole) {
                            UserRole.STUDENT -> onNavigateToStudent()
                            UserRole.STALL_OWNER -> onNavigateToOwner()
                            UserRole.ADMIN -> onNavigateToAdmin()
                            null -> {}
                        }
                    } else if (selectedRole == UserRole.STUDENT && customName.isNotBlank()) {
                        val success = viewModel.loginDemoUser(customName, UserRole.STUDENT)
                        if (success) {
                            onNavigateToStudent()
                        } else {
                            loginError = "Failed to create student account."
                        }
                    } else {
                        loginError = "Please select or create an account."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(
                    text = "Log in",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Fast Tray demo",
                color = SlateLight,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun RoleCard(
    roleName: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) OrangePrimary else WhiteSurface
    val contentColor = if (isSelected) Color.White else SlateMedium
    val borderColor = if (isSelected) OrangePrimary else BorderColor

    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = roleName,
                tint = if (isSelected) Color.White else OrangePrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = roleName,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    RoleSelectionScreen(
        viewModel = MainViewModel(), 
        onNavigateToStudent = {},
        onNavigateToOwner = {},
        onNavigateToAdmin = {}
    )
}