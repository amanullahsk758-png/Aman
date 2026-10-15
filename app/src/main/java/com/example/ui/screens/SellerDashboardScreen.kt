package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Seller
import com.example.ui.components.formatInr
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.DKartBlue
import com.example.ui.theme.DKartDarkBlue
import com.example.ui.theme.DKartGreen
import com.example.ui.theme.DKartOrange
import com.example.ui.theme.DKartYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateToTab(Screen.Home)
    }

    val sellers by viewModel.allSellers.collectAsState()
    val currentSellerState by viewModel.currentSeller.collectAsState()
    val activeSeller = currentSellerState ?: sellers.firstOrNull() ?: Seller(
        id = "seller_dkart_direct",
        fullName = "DKart Retail Official",
        businessName = "DKart Retail Official",
        displayName = "DKart Official Store",
        email = "retail@dkart.in",
        phone = "+91 98765 00001",
        pickupAddress = "DKart Fulfillment Center, Mumbai",
        status = "APPROVED"
    )

    val allProducts by viewModel.allProducts.collectAsState()
    val sellerProducts = allProducts.filter { it.sellerId == activeSeller.id }

    val allOrders by viewModel.orders.collectAsState()
    val sellerOrderItems = allOrders.flatMap { order ->
        order.items.filter { it.sellerId == activeSeller.id }.map { it to order.order }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val grossSales = sellerProducts.sumOf { it.price * (it.ratingCount % 20).coerceAtLeast(1) }
    val commissionDeduction = (grossSales * activeSeller.commissionPercent) / 100.0
    val netPayout = grossSales - commissionDeduction

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(activeSeller.displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("DKart Verified Merchant • ${activeSeller.status}", fontSize = 11.sp, color = DKartYellow)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateToTab(Screen.Home) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.CatalogSelection(activeSeller)) },
                        colors = ButtonDefaults.buttonColors(containerColor = DKartYellow, contentColor = DKartDarkBlue),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Listing", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DKartDarkBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNeutral)
                .testTag("seller_dashboard_screen")
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = DKartBlue
            ) {
                Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }, text = { Text("Overview", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
                Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }, text = { Text("Listings (${sellerProducts.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
                Tab(selected = selectedTabIndex == 2, onClick = { selectedTabIndex = 2 }, text = { Text("Orders (${sellerOrderItems.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
                Tab(selected = selectedTabIndex == 3, onClick = { selectedTabIndex = 3 }, text = { Text("Payouts", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
            }

            when (selectedTabIndex) {
                // TAB 0: OVERVIEW & GUIDED LISTING TRIGGER
                0 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            // Guided Listing Banner
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.navigateTo(Screen.CatalogSelection(activeSeller)) },
                                colors = CardDefaults.cardColors(containerColor = DKartBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = DKartYellow, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("List from DKart Master Catalog", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("Search matching smartphones, t-shirts, sarees, or shoes & list in 60 seconds with your price & stock!", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        item {
                            Text("SELLER METRICS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = DKartGreen)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Gross Sales", fontSize = 11.sp, color = TextSecondary)
                                        Text(formatInr(grossSales), fontSize = 16.sp, fontWeight = FontWeight.Black, color = DKartGreen)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Icon(Icons.Default.Inventory, contentDescription = null, tint = DKartBlue)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Active Listings", fontSize = 11.sp, color = TextSecondary)
                                        Text(sellerProducts.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = DKartBlue)
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = DKartOrange)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Order Items", fontSize = 11.sp, color = TextSecondary)
                                        Text(sellerOrderItems.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = DKartOrange)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF7B1FA2))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Seller Rating", fontSize = 11.sp, color = TextSecondary)
                                        Text("${activeSeller.rating} ★", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF7B1FA2))
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("STORE PROFILE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Business: ${activeSeller.businessName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Email: ${activeSeller.email}  •  Phone: ${activeSeller.phone}", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                                    Text("Pickup: ${activeSeller.pickupAddress}", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                                    Text("Marketplace Commission: ${activeSeller.commissionPercent}%", fontSize = 12.sp, color = DKartBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }

                // TAB 1: PRODUCT LISTINGS & INVENTORY
                1 -> {
                    if (sellerProducts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No products listed yet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.CatalogSelection(activeSeller)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DKartBlue)
                                ) {
                                    Text("Search Catalog to List Product")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sellerProducts) { prod ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFF9F9F9))) {
                                            AsyncImage(model = prod.imageUrl, contentDescription = prod.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = prod.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                            Text(text = "Price: ${formatInr(prod.price)} (MRP: ${formatInr(prod.mrp)})", fontSize = 12.sp, color = DKartBlue, fontWeight = FontWeight.SemiBold)
                                            Text(text = "Available Stock: ${prod.stock} units", fontSize = 11.sp, color = if (prod.stock <= 5) Color.Red else TextSecondary)
                                            Text(text = "Images: ${prod.getAllImages().size}/6", fontSize = 11.sp, color = TextSecondary)
                                        }

                                        // Quick Stock Stepper
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            OutlinedButton(
                                                onClick = { viewModel.saveProduct(prod.copy(stock = (prod.stock - 1).coerceAtLeast(0))) },
                                                modifier = Modifier.size(32.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("-", fontWeight = FontWeight.Bold)
                                            }
                                            Text("${prod.stock}", modifier = Modifier.padding(horizontal = 6.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            OutlinedButton(
                                                onClick = { viewModel.saveProduct(prod.copy(stock = prod.stock + 5)) },
                                                modifier = Modifier.size(32.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("+", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: SELLER ORDERS
                2 -> {
                    if (sellerOrderItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No orders received for your products yet", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sellerOrderItems) { (item, order) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DKartBlue)
                                            Text(formatInr(item.price * item.quantity), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Text(text = item.title, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                        Text(text = "Qty: ${item.quantity}  •  Status: ${order.status}", fontSize = 11.sp, color = DKartGreen, fontWeight = FontWeight.SemiBold)
                                        Text(text = "Deliver to: ${order.shippingAddress}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: PAYOUTS & SETTLEMENTS
                3 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("PAYOUT SUMMARY & SETTLEMENTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Gross Seller Sales", fontSize = 13.sp)
                                        Text(formatInr(grossSales), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("DKart Commission (${activeSeller.commissionPercent}%)", fontSize = 13.sp)
                                        Text("- ${formatInr(commissionDeduction)}", fontSize = 13.sp, color = Color.Red)
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Net Payout to Bank", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(formatInr(netPayout), fontSize = 16.sp, fontWeight = FontWeight.Black, color = DKartGreen)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Settlement Account: A/C *******${activeSeller.bankAccountNumber.takeLast(4)} (${activeSeller.bankIfsc})", fontSize = 11.sp, color = TextSecondary)
                                    Text("Payout Cycle: Direct NEFT / RTGS every Tuesday", fontSize = 11.sp, color = DKartBlue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
