package com.abbie.fast_tray.views

//@file:Suppress("DEPRECATION")

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.abbie.fast_tray.models.*
import java.util.Locale
import com.abbie.fast_tray.ui.theme.*
import com.abbie.fast_tray.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScaffold(
    viewModel: MainViewModel,
    currentScreen: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = "Fast Tray Logo",
                            tint = OrangePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fast Tray",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
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
                    selected = currentScreen == "stalls",
                    onClick = { onNavigate("stalls") },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Stalls") },
                    label = { Text("Stalls") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        unselectedIconColor = SlateLight,
                        unselectedTextColor = SlateLight,
                        indicatorColor = SlateDark
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "cart",
                    onClick = { onNavigate("cart") },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge(containerColor = OrangePrimary) {
                                        Text(text = cartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        unselectedIconColor = SlateLight,
                        unselectedTextColor = SlateLight,
                        indicatorColor = SlateDark
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == "history",
                    onClick = { onNavigate("history") },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
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

//LIAT STALLS
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseStallsScreen(
    viewModel: MainViewModel,
    onNavigateToStall: (Int) -> Unit
) {
    val stalls by viewModel.stalls.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All Stalls") }

    val tags = listOf("All Stalls", "Halal", "Vegetarian", "Western", "Asian", "Drinks")

    val filteredStalls = stalls.filter { stall ->
        val matchesSearch = stall.name.contains(searchQuery, ignoreCase = true) ||
                (stall.description?.contains(searchQuery, ignoreCase = true) ?: false)
        val matchesTag = when (selectedTag) {
            "All Stalls" -> true
            "Halal" -> stall.name.contains("Grill", ignoreCase = true) || stall.name.contains("Asian", ignoreCase = true)
            "Vegetarian" -> stall.name.contains("Delights", ignoreCase = true) || stall.name.contains("Lounge", ignoreCase = true)
            "Western" -> stall.name.contains("Grill", ignoreCase = true) || stall.name.contains("Economics", ignoreCase = true)
            "Asian" -> stall.name.contains("Asian", ignoreCase = true)
            "Drinks" -> stall.name.contains("Espresso", ignoreCase = true) || stall.name.contains("Lounge", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesTag && stall.isActive
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Welcome, ${currentUser?.name ?: "Student"}!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Select a vendor to view the current menu and estimated waiting times for the Universitas Central dining hall.",
                fontSize = 13.sp,
                color = SlateLight
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search stalls or food...", color = SlateLight) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = SlateLight) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = WhiteSurface,
                    unfocusedContainerColor = WhiteSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tags) { tag ->
                    val isTagSelected = tag == selectedTag
                    val pillBg = if (isTagSelected) OrangePrimary else WhiteSurface
                    val pillText = if (isTagSelected) Color.White else SlateMedium
                    val pillBorder = if (isTagSelected) OrangePrimary else BorderColor

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(pillBg)
                            .border(1.dp, pillBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedTag = tag }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tag,
                            color = pillText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        items(filteredStalls) { stall ->
            val status = when (stall.id) {
                1 -> "OPEN"
                2 -> "OPEN"
                3 -> "BUSY"
                4 -> "OPEN"
                5 -> "OPEN"
                else -> "CLOSED"
            }

            val waitTime = when (stall.id) {
                1 -> "10 MINS"
                2 -> "12 MINS"
                3 -> "25 MINS"
                4 -> "5 MINS"
                5 -> "FASTEST QUEUE"
                else -> "N/A"
            }

            val statusColor = when (status) {
                "OPEN" -> ColorSuccess
                "BUSY" -> ColorWarning
                else -> SlateLight
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToStall(stall.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (stall.id == 1 || stall.id == 2) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(OrangeLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "RECOMMENDED",
                                        color = OrangePrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = status,
                                    color = statusColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = waitTime,
                            color = if (waitTime == "FASTEST QUEUE") ColorSuccess else SlateMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stall.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stall.description ?: "",
                        fontSize = 12.sp,
                        color = SlateLight,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = BorderColor)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = SlateLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stall.location,
                                fontSize = 12.sp,
                                color = SlateLight
                            )
                        }

                        Text(
                            text = "View Menu →",
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "CAMPUS DINING STATUS SUMMARY",
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    stalls.filter { it.isActive }.forEach { stall ->
                        val status = if (stall.id == 3) "BUSY" else "OPEN"
                        val wait = when (stall.id) {
                            1 -> "10 Mins"
                            2 -> "12 Mins"
                            3 -> "25 Mins"
                            4 -> "5 Mins"
                            5 -> "2 Mins"
                            else -> "--"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stall.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1.5f)
                            )
                            Text(
                                text = status,
                                color = if (status == "OPEN") ColorSuccess else ColorWarning,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = wait,
                                color = SlateLight,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "VIEW MENU",
                                color = OrangePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onNavigateToStall(stall.id) }
                                    .weight(1.2f),
                                textAlign = TextAlign.End
                            )
                        }
                        HorizontalDivider(color = SlateMedium.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

//STALL DETAIL + MENU
@Composable
fun StallDetailScreen(
    stallId: Int,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val stalls by viewModel.stalls.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()

    val stall = stalls.firstOrNull { it.id == stallId } ?: return
    val stallItems = menuItems.filter { it.stallId == stallId }

    var selectedItemForCart by remember { mutableStateOf<MenuItem?>(null) }

    LaunchedEffect(stallId) {
        viewModel.fetchMenuItemsForStall(stallId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateDark)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "EST. WAIT TIME: 12 MINS",
                        color = OrangePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ENGINEERING BLOCK STALL 03",
                    color = OrangePrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stall.name,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stall.description ?: "",
                    color = SlateLight,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        val categories = stallItems.map { it.category }.distinct()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            categories.forEach { category ->
                item {
                    Text(
                        text = category.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = SlateMedium,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                val itemsInCategory = stallItems.filter { it.category == category }
                items(itemsInCategory) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = SlateMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.description,
                                        fontSize = 12.sp,
                                        color = SlateLight,
                                        lineHeight = 16.sp
                                    )
                                }
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", item.price)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = OrangePrimary,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (item.isAvailable) {
                                Button(
                                    onClick = { selectedItemForCart = item },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "ADD TO CART", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BorderColor)
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "NOT AVAILABLE",
                                        color = SlateLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
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
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeLight),
                    border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DIETARY NOTICE",
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "All ingredients are locally sourced from university-approved sustainable farms. For detailed allergen information, please consult the printed notice board at the stall counter.",
                            fontSize = 11.sp,
                            color = SlateMedium,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "STALL HOURS", fontSize = 10.sp, color = SlateLight)
                                Text(text = "MON-FRI 06:00 - 20:00\nSAT 09:00 - 15:00", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateMedium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "EST. WAIT", fontSize = 10.sp, color = SlateLight)
                                Text(text = "12 MINUTES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (selectedItemForCart != null) {
        AddToCartDialog(
            menuItem = selectedItemForCart!!,
            onDismiss = { selectedItemForCart = null },
            onConfirm = { qty, notes ->
                viewModel.addToCart(selectedItemForCart!!, qty, notes)
                selectedItemForCart = null
            }
        )
    }
}

@Composable
fun AddToCartDialog(
    menuItem: MenuItem,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add to Cart",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = SlateMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = menuItem.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OrangePrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = menuItem.description,
                    fontSize = 12.sp,
                    color = SlateLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.background(OrangeLight, CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = OrangePrimary)
                    }
                    Text(
                        text = quantity.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMedium
                    )
                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.background(OrangeLight, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = OrangePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special notes (e.g. no onions, extra spicy)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = BorderColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateMedium),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onConfirm(quantity, notes) },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text("Add - $${String.format(Locale.US, "%.2f", menuItem.price * quantity)}", color = Color.White)
                    }
                }
            }
        }
    }
}

// CART SCREEN
@Composable
fun CartScreen(
    viewModel: MainViewModel,
    onNavigateToTracking: (Int) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val stalls by viewModel.stalls.collectAsState()
    val cartStallId by viewModel.cartStallId.collectAsState()

    val stall = stalls.firstOrNull { it.id == cartStallId }
    var pickupTime by remember { mutableStateOf("12:30 PM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Your Cart",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMedium
        )

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Empty Cart",
                        modifier = Modifier.size(72.dp),
                        tint = SlateLight
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cart is empty.",
                        fontWeight = FontWeight.Bold,
                        color = SlateMedium
                    )
                    Text(
                        text = "Head to the Stalls tab to add food!",
                        color = SlateLight,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ordering from: ${stall?.name ?: "Stall"}",
                color = OrangePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Cart Items List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
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
                                    text = item.menuItem.name,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMedium
                                )
                                if (item.notes.isNotBlank()) {
                                    Text(
                                        text = "Note: ${item.notes}",
                                        fontSize = 11.sp,
                                        color = SlateLight
                                    )
                                }
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", item.menuItem.price)} each",
                                    color = OrangePrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.menuItem.id, item.quantity - 1) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(OrangeLight, CircleShape)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = item.quantity.toString(),
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMedium
                                )
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.menuItem.id, item.quantity + 1) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(OrangeLight, CircleShape)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // pickup
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Pickup Settings",
                                fontWeight = FontWeight.Bold,
                                color = SlateMedium,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // pickup time options
                            var timeExpanded by remember { mutableStateOf(false) }
                            val timeOptions = listOf("12:00 PM", "12:15 PM", "12:30 PM", "12:45 PM", "01:00 PM", "01:15 PM", "01:30 PM")

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { timeExpanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, BorderColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Requested Pickup Time: $pickupTime", color = SlateMedium)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand", tint = SlateMedium)
                                    }
                                }

                                DropdownMenu(
                                    expanded = timeExpanded,
                                    onDismissRequest = { timeExpanded = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    timeOptions.forEach { time ->
                                        DropdownMenuItem(
                                            text = { Text(time) },
                                            onClick = {
                                                pickupTime = time
                                                timeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Payment is strictly cash on pickup. Check the order tracking screen once placed.",
                                fontSize = 11.sp,
                                color = SlateLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val total = cartItems.sumOf { it.menuItem.price * it.quantity }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Price:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SlateMedium
                )
                Text(
                    text = "$${String.format(Locale.US, "%.2f", total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = OrangePrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.clearCart() },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ColorDanger),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorDanger),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear Cart")
                }

                Button(
                    onClick = {
                        viewModel.checkout(pickupTime) { orderId ->
                            onNavigateToTracking(orderId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(2f)
                ) {
                    Text("Place Order", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

//ORDER TRACK
@Composable
fun OrderTrackingScreen(
    orderId: Int,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val stalls by viewModel.stalls.collectAsState()

    val order = orders.firstOrNull { it.id == orderId }
    val stall = stalls.firstOrNull { it.id == order?.stallId }

    val coroutineScope = rememberCoroutineScope()
    var simState by remember { mutableStateOf("Ready to Simulate") }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Order not found.", color = SlateMedium)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SlateMedium)
            }
            Text(
                text = "Order Status",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // order info card
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                        Text(text = "Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SlateMedium)
                        Text(text = "From: ${stall?.name ?: "Stall"}", color = SlateLight, fontSize = 13.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (order.status) {
                                    OrderStatus.PENDING -> ColorWarning.copy(alpha = 0.15f)
                                    OrderStatus.PREPARING -> ColorWarning.copy(alpha = 0.15f)
                                    OrderStatus.READY -> ColorSuccess.copy(alpha = 0.15f)
                                    OrderStatus.COMPLETED -> ColorSuccess.copy(alpha = 0.15f)
                                    else -> ColorDanger.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = order.status.name,
                            color = when (order.status) {
                                OrderStatus.PENDING -> ColorWarning
                                OrderStatus.PREPARING -> ColorWarning
                                OrderStatus.READY -> ColorSuccess
                                OrderStatus.COMPLETED -> ColorSuccess
                                else -> ColorDanger
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(12.dp))

                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}x ${item.menuItemName}",
                            color = SlateMedium,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", item.subtotal)}",
                            color = SlateMedium,
                            fontSize = 14.sp
                        )
                    }
                    if (item.notes.isNotBlank()) {
                        Text(
                            text = "  Note: ${item.notes}",
                            fontSize = 11.sp,
                            color = SlateLight,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Requested Pickup Time:", color = SlateLight, fontSize = 12.sp)
                    Text(text = order.pickupTime, color = SlateMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total Price:", color = SlateMedium, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", order.totalPrice)}",
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                if (order.status == OrderStatus.REJECTED && order.rejectionReason.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ColorDanger.copy(alpha = 0.1f))
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "Rejection Reason: ${order.rejectionReason}",
                            color = ColorDanger,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Track Order Progress", fontWeight = FontWeight.Bold, color = SlateMedium, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            TimelineStep(
                title = "Order Submitted",
                subtitle = "Sent to stall. Waiting for stall owner to accept.",
                isActive = true,
                isCompleted = order.status != OrderStatus.PENDING && order.status != OrderStatus.REJECTED && order.status != OrderStatus.CANCELLED,
                isTerminalState = order.status == OrderStatus.REJECTED || order.status == OrderStatus.CANCELLED
            )
            TimelineStep(
                title = "Preparing Food",
                subtitle = "The chef has accepted your order and is cooking.",
                isActive = order.status == OrderStatus.PREPARING || order.status == OrderStatus.READY || order.status == OrderStatus.COMPLETED,
                isCompleted = order.status == OrderStatus.READY || order.status == OrderStatus.COMPLETED,
                isTerminalState = false
            )
            TimelineStep(
                title = "Ready for Collection",
                subtitle = "Bring cash and collect your food at the counter.",
                isActive = order.status == OrderStatus.READY || order.status == OrderStatus.COMPLETED,
                isCompleted = order.status == OrderStatus.COMPLETED,
                isTerminalState = false
            )
            TimelineStep(
                title = "Order Completed",
                subtitle = "Enjoy your fresh meal!",
                isActive = order.status == OrderStatus.COMPLETED,
                isCompleted = order.status == OrderStatus.COMPLETED,
                isLastStep = true
            )
        }

        if (order.status == OrderStatus.PENDING) {
            Button(
                onClick = { viewModel.cancelOrder(order.id, order.stallId) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorDanger),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancel Order (PENDING only)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else if (order.status == OrderStatus.READY) {
            Button(
                onClick = { viewModel.completeOrder(order.id, order.stallId) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Mark as Collected", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (order.status != OrderStatus.COMPLETED && order.status != OrderStatus.REJECTED && order.status != OrderStatus.CANCELLED) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Presentation Helper:\nSimulate stall preparation",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                simState = "Simulating..."
                                if (order.status == OrderStatus.PENDING) {
                                    viewModel.acceptOrder(order.id, order.stallId)
                                    delay(4000)
                                }
                                if (orders.firstOrNull { it.id == orderId }?.status == OrderStatus.PREPARING) {
                                    viewModel.markOrderAsReady(order.id, order.stallId)
                                }
                                simState = "Ready to Simulate"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = simState, fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineStep(
    title: String,
    subtitle: String,
    isActive: Boolean,
    isCompleted: Boolean,
    isTerminalState: Boolean = false,
    isLastStep: Boolean = false
) {
    val indicatorColor = when {
        isTerminalState -> ColorDanger
        isCompleted -> ColorSuccess
        isActive -> ColorWarning
        else -> SlateLight.copy(alpha = 0.5f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(indicatorColor, CircleShape)
            )
            if (!isLastStep) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                        .background(if (isCompleted) ColorSuccess else SlateLight.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isActive || isCompleted) SlateMedium else SlateLight
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = SlateLight,
                lineHeight = 15.sp
            )
        }
    }
}

//ORDER HISTORY
@Composable
fun OrderHistoryScreen(
    viewModel: MainViewModel,
    onNavigateToTracking: (Int) -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val stalls by viewModel.stalls.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val studentOrders = orders.filter { it.studentId == currentUser?.id }

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
                text = "Order History",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMedium
            )
            if ((currentUser?.warningCount ?: 0) > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorDanger.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Warnings: ${currentUser?.warningCount}/3",
                        color = ColorDanger,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (studentOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "You haven't placed any orders yet.", color = SlateLight, fontWeight = FontWeight.Medium)
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(studentOrders) { order ->
                    val stall = stalls.firstOrNull { it.id == order.stallId }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToTracking(order.id) },
                        colors = CardDefaults.cardColors(containerColor = WhiteSurface),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stall?.name ?: "Stall",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SlateMedium
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (order.status) {
                                                OrderStatus.COMPLETED -> ColorSuccess.copy(alpha = 0.15f)
                                                OrderStatus.READY -> ColorSuccess.copy(alpha = 0.15f)
                                                OrderStatus.PENDING -> ColorWarning.copy(alpha = 0.15f)
                                                OrderStatus.PREPARING -> ColorWarning.copy(alpha = 0.15f)
                                                else -> ColorDanger.copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = order.status.name,
                                        color = when (order.status) {
                                            OrderStatus.COMPLETED -> ColorSuccess
                                            OrderStatus.READY -> ColorSuccess
                                            OrderStatus.PENDING -> ColorWarning
                                            OrderStatus.PREPARING -> ColorWarning
                                            else -> ColorDanger
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            order.items.forEach { item ->
                                Text(
                                    text = "${item.quantity}x ${item.menuItemName}",
                                    color = SlateLight,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderColor)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.id}  •  ${order.pickupTime}",
                                    fontSize = 11.sp,
                                    color = SlateLight
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", order.totalPrice)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = OrangePrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
