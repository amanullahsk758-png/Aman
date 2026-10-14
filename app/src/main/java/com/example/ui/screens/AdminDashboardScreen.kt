package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.formatInr
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    // Add Product Form State
    var pTitle by remember { mutableStateOf("") }
    var pBrand by remember { mutableStateOf("") }
    var pCategory by remember { mutableStateOf("mobiles") }
    var pSubCategory by remember { mutableStateOf("Smartphones") }
    var pMrp by remember { mutableStateOf("1999") }
    var pPrice by remember { mutableStateOf("999") }
    var pStock by remember { mutableStateOf("50") }
    var pImageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80") }
    var pDescription by remember { mutableStateOf("Authentic quality product backed by brand warranty.") }

    val totalRevenue = orders.sumOf { it.order.totalAmount }
    val pendingOrders = orders.filter { it.order.status != "DELIVERED" && it.order.status != "CANCELLED" }
    val lowStockProducts = products.filter { it.stock <= 5 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BharatBazaar Admin Panel", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddProductDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A8A))
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNeutral)
                .testTag("admin_dashboard_screen")
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = FlipkartBlue
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Overview", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Products (${products.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Orders (${orders.size})", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTabIndex) {
                // TAB 0: OVERVIEW & ANALYTICS
                0 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("BUSINESS OVERVIEW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
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
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = FlipkartGreen)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Total Revenue", fontSize = 11.sp, color = TextSecondary)
                                        Text(formatInr(totalRevenue), fontSize = 16.sp, fontWeight = FontWeight.Black, color = FlipkartGreen)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = FlipkartBlue)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Total Orders", fontSize = 11.sp, color = TextSecondary)
                                        Text(orders.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = FlipkartBlue)
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
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = FlipkartOrange)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Pending Shipments", fontSize = 11.sp, color = TextSecondary)
                                        Text(pendingOrders.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = FlipkartOrange)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Low Stock Alerts", fontSize = 11.sp, color = TextSecondary)
                                        Text(lowStockProducts.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Red)
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showAddProductDialog = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ADD NEW PRODUCT TO CATALOG", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // TAB 1: PRODUCT MANAGEMENT
                1 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products) { prod ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = prod.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                        Text(text = "${prod.brand} • ${prod.subCategory}", fontSize = 11.sp, color = TextSecondary)
                                        Text(
                                            text = "Price: ${formatInr(prod.price)} (MRP: ${formatInr(prod.mrp)})  •  Stock: ${prod.stock}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (prod.stock <= 5) Color.Red else FlipkartBlue
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteProduct(prod) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: ORDER PROCESSING & STATUS UPDATER
                2 -> {
                    if (orders.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No customer orders placed yet", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(orders) { orderWithItems ->
                                val ord = orderWithItems.order
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Order #${ord.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FlipkartBlue)
                                            Text(formatInr(ord.totalAmount), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        }

                                        Text("Current Status: ${ord.status}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FlipkartGreen, modifier = Modifier.padding(top = 2.dp))
                                        Text("Items: ${orderWithItems.items.joinToString { it.title.take(16) }}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider(color = Color(0xFFF5F5F5))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Status Change Action Buttons
                                        Text("UPDATE ORDER STATUS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf("CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED").forEach { st ->
                                                OutlinedButton(
                                                    onClick = { viewModel.updateOrderStatus(ord.orderId, st) },
                                                    modifier = Modifier.weight(1f).height(32.dp),
                                                    shape = RoundedCornerShape(4.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = if (ord.status == st) FlipkartGreen else Color.DarkGray
                                                    )
                                                ) {
                                                    Text(st.take(4), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Add New Product", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(value = pTitle, onValueChange = { pTitle = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = pBrand, onValueChange = { pBrand = it }, label = { Text("Brand Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = pSubCategory, onValueChange = { pSubCategory = it }, label = { Text("Subcategory") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = pPrice, onValueChange = { pPrice = it }, label = { Text("Selling Price (₹)") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = pMrp, onValueChange = { pMrp = it }, label = { Text("MRP (₹)") }, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = pStock, onValueChange = { pStock = it }, label = { Text("Available Stock") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = pImageUrl, onValueChange = { pImageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceVal = pPrice.toDoubleOrNull() ?: 499.0
                        val mrpVal = pMrp.toDoubleOrNull() ?: priceVal
                        val stockVal = pStock.toIntOrNull() ?: 20
                        val discount = if (mrpVal > priceVal) (((mrpVal - priceVal) / mrpVal) * 100).toInt() else 0

                        val newProd = Product(
                            id = "prod_custom_" + System.currentTimeMillis().toString().takeLast(6),
                            title = pTitle.ifBlank { "New Product" },
                            brand = pBrand.ifBlank { "Brand" },
                            categoryId = pCategory,
                            subCategory = pSubCategory,
                            mrp = mrpVal,
                            price = priceVal,
                            discountPercent = discount,
                            rating = 4.5f,
                            ratingCount = 1,
                            reviewCount = 0,
                            stock = stockVal,
                            imageUrl = pImageUrl,
                            description = pDescription,
                            highlights = "100% Brand Certified;Fast Dispatch;Return Eligible"
                        )
                        viewModel.saveProduct(newProd)
                        showAddProductDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue)
                ) {
                    Text("Save to Catalog")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
