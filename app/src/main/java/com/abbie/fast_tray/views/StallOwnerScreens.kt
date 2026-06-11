package com.abbie.fast_tray.views

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.abbie.fast_tray.models.*
import com.abbie.fast_tray.ui.theme.*
import com.abbie.fast_tray.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerScaffold(
    viewModel: MainViewModel,
    currentScreen: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val stalls by viewModel.stalls.collectAsState()
    val activeStallId by viewModel.ownerActiveStallId.collectAsState()
    val stall = stalls.firstOrNull { it.id == activeStallId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Fast Tray Owner",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = stall?.name ?: "Stall Owner Panel",
                            color = OrangePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
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
                    selected = currentScreen == "queue",
                    onClick = { onNavigate("queue") },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Queue") },
                    label = { Text("Queue") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        unselectedIconColor = SlateLight,
                        unselectedTextColor = SlateLight,
                        indicatorColor = SlateDark
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "menu",
                    onClick = { onNavigate("menu") },
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = "Menu") },
                    label = { Text("Menu") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        unselectedIconColor = SlateLight,
                        unselectedTextColor = SlateLight,
                        indicatorColor = SlateDark
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "summary",
                    onClick = { onNavigate("summary") },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Summary") },
                    label = { Text("Summary") },
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

// ORDER QUEUE SCREEN
@Composable
fun OrderQueueScreen(
    viewModel: MainViewModel,
    onNavigateToOrderDetails: (Int) -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val activeStallId by viewModel.ownerActiveStallId.collectAsState()
    val users by viewModel.users.collectAsState()

    val activeStallOrders = orders.filter { it.stallId == activeStallId }
    val pendingOrders = activeStallOrders.filter { it.status == OrderStatus.PENDING }
    val preparingOrders = activeStallOrders.filter { it.status == OrderStatus.PREPARING || it.status == OrderStatus.READY }

    var selectedOrderForRejection by remember { mutableStateOf<Order?>(null) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Order Queue",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMedium
                )
                Text(
                    text = "REAL-TIME MANAGEMENT SYSTEM — STATION 0$activeStallId",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    letterSpacing = 1.sp
                )
            }
        }

        // PENDING ORDERS SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PENDING",
                    fontWeight = FontWeight.Bold,
                    color = SlateMedium,
                    fontSize = 14.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ColorDanger)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${pendingOrders.size} NEW",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (pendingOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No pending orders.", color = SlateLight, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(pendingOrders) { order ->
                val student = users.firstOrNull { it.id == order.studentId }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToOrderDetails(order.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "ORDER ID: #${order.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SlateLight
                                )
                                Text(
                                    text = student?.name ?: "Student",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SlateMedium
                                )
                            }
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", order.totalPrice)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = OrangePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        order.items.forEach { item ->
                            Text(
                                text = "${item.quantity}x ${item.menuItemName}",
                                fontSize = 13.sp,
                                color = SlateMedium
                            )
                            if (item.notes.isNotBlank()) {
                                Text(
                                    text = "   NOTE: ${item.notes}",
                                    fontSize = 11.sp,
                                    color = ColorDanger,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.acceptOrder(order.id, activeStallId) },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "ACCEPT", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { selectedOrderForRejection = order },
                                border = BorderStroke(1.dp, BorderColor),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateMedium),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "REJECT")
                            }
                        }
                    }
                }
            }
        }

        // PREPARING / READY
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IN PROGRESS",
                    fontWeight = FontWeight.Bold,
                    color = SlateMedium,
                    fontSize = 14.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SlateMedium)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${preparingOrders.size} ACTIVE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (preparingOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No in-progress orders.", color = SlateLight, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(preparingOrders) { order ->
                val student = users.firstOrNull { it.id == order.studentId }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToOrderDetails(order.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "ORDER ID: #${order.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = SlateLight
                                )
                                Text(
                                    text = student?.name ?: "Student",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SlateMedium
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", order.totalPrice)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = OrangePrimary
                                )
                                Text(
                                    text = "PICKUP: ${order.pickupTime}",
                                    fontSize = 10.sp,
                                    color = SlateLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        order.items.forEach { item ->
                            Text(
                                text = "${item.quantity}x ${item.menuItemName}",
                                fontSize = 13.sp,
                                color = SlateMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (order.status == OrderStatus.PREPARING) {
                            Button(
                                onClick = { viewModel.markOrderAsReady(order.id, activeStallId) },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "MARK AS READY", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else if (order.status == OrderStatus.READY) {
                            Button(
                                onClick = { viewModel.completeOrder(order.id, activeStallId) },
                                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "MARK AS COLLECTED (COMPLETED)", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUICK METRICS",
                        fontWeight = FontWeight.Bold,
                        color = SlateMedium,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "AVG. WAIT TIME", fontSize = 10.sp, color = SlateLight)
                            Text(text = "12.4 MN", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateMedium)
                        }
                        Text(text = "-2.1% DECREASE", color = ColorSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "TOTAL ORDERS TODAY", fontSize = 10.sp, color = SlateLight)
                            Text(text = "142", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateMedium)
                        }
                        Text(text = "+12% RISING", color = ColorWarning, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "COMPLETION RATE", fontSize = 10.sp, color = SlateLight)
                            Text(text = "88.7%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateMedium)
                        }
                        Text(text = "STABLE", color = SlateLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (selectedOrderForRejection != null) {
        var reasonText by remember { mutableStateOf("") }
        var showErr by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { selectedOrderForRejection = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Reject Order #${selectedOrderForRejection!!.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SlateMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Please provide a reason. Rejections count as warnings towards student accounts to discourage fake bookings.",
                        fontSize = 12.sp,
                        color = SlateLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = {
                            reasonText = it
                            showErr = false
                        },
                        label = { Text("Reason for Rejection") },
                        singleLine = true,
                        isError = showErr,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = BorderColor
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showErr) {
                        Text(text = "Reason is required.", color = ColorDanger, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedOrderForRejection = null },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateMedium),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (reasonText.trim().isEmpty()) {
                                    showErr = true
                                } else {
                                    viewModel.rejectOrder(selectedOrderForRejection!!.id, activeStallId, reasonText)
                                    selectedOrderForRejection = null
                                    Toast.makeText(context, "Order rejected.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorDanger),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Reject Order", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ORDER DETAIL SCREEN
@Composable
fun OrderDetailScreen(
    orderId: Int,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val users by viewModel.users.collectAsState()
    val order = orders.firstOrNull { it.id == orderId }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Order not found", color = SlateMedium)
        }
        return
    }

    val student = users.firstOrNull { it.id == order.studentId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SlateMedium)
            }
            Text(text = "Order Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SlateMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Customer Info",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = OrangePrimary
                )
                Text(text = "Name: ${student?.name ?: "Unknown"}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateMedium)
                Text(text = "Email: ${student?.email ?: "Unknown"}", fontSize = 12.sp, color = SlateLight)
                if (student?.warningCount ?: 0 > 0) {
                    Text(text = "Warnings Tracker: ${student?.warningCount} warnings", color = ColorDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = BorderColor)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Order Items",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = OrangePrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${item.quantity}x ${item.menuItemName}", color = SlateMedium)
                        Text(text = "$${String.format(Locale.US, "%.2f", item.subtotal)}", color = SlateMedium)
                    }
                    if (item.notes.isNotBlank()) {
                        Text(text = "  Notes: ${item.notes}", color = ColorDanger, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = BorderColor)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Pickup Time Requested:", color = SlateLight)
                    Text(text = order.pickupTime, fontWeight = FontWeight.Bold, color = SlateMedium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Price:", color = SlateMedium, fontWeight = FontWeight.Bold)
                    Text(text = "$${String.format(Locale.US, "%.2f", order.totalPrice)}", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Current Status:", color = SlateLight)
                    Text(text = order.status.name, fontWeight = FontWeight.Bold, color = OrangePrimary)
                }

                if (order.rejectionReason.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Rejection Reason: ${order.rejectionReason}", color = ColorDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// MENU MANAGEMET
@Composable
fun MenuManagementScreen(
    viewModel: MainViewModel,
    onNavigateToAddEdit: (Int?) -> Unit
) {
    val activeStallId by viewModel.ownerActiveStallId.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()

    val stallItems = menuItems.filter { it.stallId == activeStallId }

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
            Text(
                text = "Menu Management",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMedium
            )
            Button(
                onClick = { onNavigateToAddEdit(null) },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Item", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (stallItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No items in menu.", color = SlateLight)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(stallItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SlateMedium
                                )
                                Text(
                                    text = item.category ?: "Uncategorized",
                                    color = OrangePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", item.price)}",
                                    color = SlateLight,
                                    fontSize = 13.sp
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Edit button
                                IconButton(onClick = { onNavigateToAddEdit(item.id) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Item", tint = SlateLight)
                                }

                                Switch(
                                    checked = item.isAvailable,
                                    onCheckedChange = { viewModel.toggleMenuItemAvailability(item.id) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = OrangePrimary,
                                        checkedTrackColor = OrangeLight
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// CU SCREEN
@Composable
fun AddEditFoodItemScreen(
    itemId: Int?,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val menuItems by viewModel.menuItems.collectAsState()
    val currentItem = menuItems.firstOrNull { it.id == itemId }

    var name by remember { mutableStateOf(currentItem?.name ?: "") }
    var description by remember { mutableStateOf(currentItem?.description ?: "") }
    var priceStr by remember { mutableStateOf(currentItem?.price?.toString() ?: "") }
    var category by remember { mutableStateOf(currentItem?.category ?: "Popular Choices") }
    var showErr by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SlateMedium)
            }
            Text(
                text = if (itemId != null) "Edit Menu Item" else "Add Menu Item",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                var categoryExpanded by remember { mutableStateOf(false) }
                val categoryOptions = listOf("Food", "Drinks")

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Category: $category", color = SlateMedium)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand", tint = SlateMedium)
                        }
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        categoryOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    category = opt
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                if (showErr) {
                    Text(text = "Please fill in all details with valid price.", color = ColorDanger, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val price = priceStr.toDoubleOrNull()
                        if (name.isNotBlank() && description.isNotBlank() && price != null) {
                            viewModel.saveMenuItem(itemId, name, description, price, category)
                            onNavigateBack()
                        } else {
                            showErr = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save Menu Item", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// SALES SUMMARY
@Composable
fun SalesSummaryScreen(
    viewModel: MainViewModel
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val summary by viewModel.salesSummary.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchActiveSalesSummary()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Sales Summary",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateMedium
                )
                Text(
                    text = "PERFORMANCE DASHBOARD — DATE: $dateStr",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    letterSpacing = 1.sp
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "TOTAL REVENUE", color = OrangePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", summary?.totalRevenue ?: 0.0)}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "+12.4% vs last period", color = ColorSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "TOTAL ORDERS", color = SlateLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = (summary?.totalOrders ?: 0).toString(), color = SlateMedium, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Frequency: High", color = SlateLight, fontSize = 9.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "COMPLETED", color = ColorSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = (summary?.completedOrders ?: 0).toString(), color = SlateMedium, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            val rate = if ((summary?.totalOrders ?: 0) > 0) ((summary?.completedOrders ?: 0) * 100 / (summary?.totalOrders ?: 1)) else 100
                            Text(text = "Rate: $rate%", color = ColorSuccess, fontSize = 9.sp)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "CANCEL/REJECT", color = ColorDanger, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = ((summary?.cancelledOrders ?: 0) + (summary?.rejectedOrders ?: 0)).toString(), color = SlateMedium, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            val loss = ((summary?.cancelledOrders ?: 0) + (summary?.rejectedOrders ?: 0)) * 12.0
                            Text(text = "Loss: -$${String.format(Locale.US, "%.0f", loss)}", color = ColorDanger, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // TOP SELLING
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TOP SELLING ITEMS",
                        fontWeight = FontWeight.Bold,
                        color = SlateMedium,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "NAME", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = SlateLight, modifier = Modifier.weight(1.5f))
                        Text(text = "QTY SOLD", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = SlateLight, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(text = "REVENUE", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = SlateLight, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Divider(color = BorderColor)

                    if (summary?.topSellingItems?.isEmpty() != false) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(text = "No sales data yet.", color = SlateLight, fontSize = 12.sp)
                        }
                    } else {
                        summary!!.topSellingItems.forEachIndexed { idx, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "0${idx + 1}  ${item.menuItemName}", fontSize = 12.sp, color = SlateMedium, modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Medium)
                                Text(text = item.quantitySold.toString(), fontSize = 12.sp, color = SlateMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                                Text(text = "$${String.format(Locale.US, "%.2f", item.revenue)}", fontSize = 12.sp, color = OrangePrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = BorderColor.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { Toast.makeText(context, "CSV exported to Downloads folder.", Toast.LENGTH_SHORT).show() },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateMedium),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "EXPORT CSV")
                }
                Button(
                    onClick = { Toast.makeText(context, "Sending document to wireless printer...", Toast.LENGTH_SHORT).show() },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "PRINT REPORT", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
