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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CatalogItem
import com.example.data.model.Product
import com.example.ui.components.formatInr
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.DKartBlue
import com.example.ui.theme.DKartDarkBlue
import com.example.ui.theme.DKartGreen
import com.example.ui.theme.DKartOrange
import com.example.ui.theme.DKartYellow
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
    val sellers by viewModel.allSellers.collectAsState()
    val catalogItems by viewModel.catalogItems.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddCatalogDialog by remember { mutableStateOf(false) }

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

    // Add Catalog Item Form State
    var catTitle by remember { mutableStateOf("") }
    var catBrand by remember { mutableStateOf("") }
    var catModel by remember { mutableStateOf("") }
    var catGtin by remember { mutableStateOf("") }
    var catCategory by remember { mutableStateOf("mobiles") }
    var catSubCategory by remember { mutableStateOf("Smartphones") }
    var catMrp by remember { mutableStateOf("2999") }
    var catImageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80") }
    var catDescription by remember { mutableStateOf("Official Master Catalog Specification Item") }

    val totalRevenue = orders.sumOf { it.order.totalAmount }
    val pendingOrders = orders.filter { it.order.status != "DELIVERED" && it.order.status != "CANCELLED" }
    val lowStockProducts = products.filter { it.stock <= 5 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DKart Admin Console", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (selectedTabIndex == 1) {
                        IconButton(onClick = { showAddProductDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.White)
                        }
                    } else if (selectedTabIndex == 3) {
                        IconButton(onClick = { showAddCatalogDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Catalog Item", tint = Color.White)
                        }
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
                .testTag("admin_dashboard_screen")
        ) {
            // Scrollable Tab Selector for Complete Admin Functionality
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = DKartBlue,
                edgePadding = 8.dp
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
                    text = { Text("Sellers (${sellers.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    text = { Text("Catalog (${catalogItems.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 4,
                    onClick = { selectedTabIndex = 4 },
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
                            Text("DKART MARKETPLACE OVERVIEW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
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
                                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = DKartGreen)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Total Gross Merchandise", fontSize = 11.sp, color = TextSecondary)
                                        Text(formatInr(totalRevenue), fontSize = 16.sp, fontWeight = FontWeight.Black, color = DKartGreen)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = DKartBlue)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Marketplace Orders", fontSize = 11.sp, color = TextSecondary)
                                        Text(orders.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = DKartBlue)
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
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFFF57F17))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Active Sellers", fontSize = 11.sp, color = TextSecondary)
                                        Text(sellers.size.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFF57F17))
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
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("MARKETPLACE HEALTH & METRICS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DKartDarkBlue)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("• Total Master Catalog Items: ${catalogItems.size}", fontSize = 12.sp)
                                    Text("• Active Product Listings: ${products.size}", fontSize = 12.sp)
                                    Text("• Pending Fulfillment Orders: ${pendingOrders.size}", fontSize = 12.sp)
                                    Text("• Platform Commission Rate: 10.0% standard", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // TAB 1: PRODUCT LISTINGS & INVENTORY
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = prod.imageUrl,
                                        contentDescription = prod.title,
                                        modifier = Modifier.size(54.dp),
                                        contentScale = ContentScale.Fit
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(prod.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                        Text("Seller: ${prod.sellerName}", fontSize = 11.sp, color = TextSecondary)
                                        Text("Price: ${formatInr(prod.price)} | Stock: ${prod.stock}", fontSize = 12.sp, color = DKartBlue, fontWeight = FontWeight.SemiBold)
                                    }

                                    IconButton(onClick = { viewModel.deleteProduct(prod) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: MULTI-VENDOR SELLER MANAGEMENT
                2 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sellers) { seller ->
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
                                        Column {
                                            Text(seller.businessName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Owner: ${seller.fullName} (${seller.displayName})", fontSize = 11.sp, color = TextSecondary)
                                        }

                                        val statusColor = when (seller.status) {
                                            "APPROVED" -> DKartGreen
                                            "PENDING" -> DKartOrange
                                            else -> Color.Red
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(seller.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("GSTIN: ${seller.gstin} | PAN: ${seller.pan}", fontSize = 11.sp, color = TextSecondary)
                                    Text("Pickup: ${seller.pickupAddress}", fontSize = 11.sp, color = TextSecondary)
                                    Text("Commission: ${seller.commissionPercent}% | Rating: ${seller.rating}★", fontSize = 11.sp, color = DKartBlue, fontWeight = FontWeight.Medium)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color(0xFFF0F0F0))
                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (seller.status != "APPROVED") {
                                            Button(
                                                onClick = { viewModel.approveSeller(seller.id) },
                                                modifier = Modifier.weight(1f).height(34.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = DKartGreen),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        if (seller.status != "SUSPENDED") {
                                            OutlinedButton(
                                                onClick = { viewModel.suspendSeller(seller.id) },
                                                modifier = Modifier.weight(1f).height(34.dp),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text("Suspend", fontSize = 11.sp, color = Color.Red)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 3: MASTER CATALOG MANAGEMENT
                3 -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showAddCatalogDialog = true },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DKartBlue),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Master Catalog Product")
                            }
                        }

                        items(catalogItems) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(54.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                        Text("Brand: ${item.brand} | Model: ${item.modelNumber}", fontSize = 11.sp, color = TextSecondary)
                                        Text("GTIN/Barcode: ${item.gtin.ifBlank { "N/A" }} | MRP: ${formatInr(item.standardMrp)}", fontSize = 11.sp, color = DKartBlue)
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 4: MARKETPLACE ORDERS
                4 -> {
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
                                        Text("Order #${ord.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DKartBlue)
                                        Text(formatInr(ord.totalAmount), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                    }

                                    Text("Current Status: ${ord.status}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DKartGreen, modifier = Modifier.padding(top = 2.dp))
                                    Text("Items: ${orderWithItems.items.joinToString { it.title.take(16) }}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color(0xFFF5F5F5))
                                    Spacer(modifier = Modifier.height(8.dp))

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
                                                    contentColor = if (ord.status == st) DKartGreen else Color.DarkGray
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

    // Add Product Dialog
    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Add Direct Listing", fontWeight = FontWeight.Bold) },
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
                    colors = ButtonDefaults.buttonColors(containerColor = DKartBlue)
                ) {
                    Text("Save Listing")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Master Catalog Item Dialog
    if (showAddCatalogDialog) {
        AlertDialog(
            onDismissRequest = { showAddCatalogDialog = false },
            title = { Text("Add Master Catalog Product", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(value = catTitle, onValueChange = { catTitle = it }, label = { Text("Catalog Title") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = catBrand, onValueChange = { catBrand = it }, label = { Text("Brand Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = catModel, onValueChange = { catModel = it }, label = { Text("Model / Model Number") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = catGtin, onValueChange = { catGtin = it }, label = { Text("GTIN / EAN / UPC Barcode") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = catMrp, onValueChange = { catMrp = it }, label = { Text("Standard MRP (₹)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = catImageUrl, onValueChange = { catImageUrl = it }, label = { Text("Product Image URL") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newItem = CatalogItem(
                            id = "cat_" + System.currentTimeMillis().toString().takeLast(6),
                            title = catTitle.ifBlank { "Master Catalog Item" },
                            brand = catBrand.ifBlank { "Brand" },
                            modelNumber = catModel,
                            gtin = catGtin,
                            categoryId = catCategory,
                            subCategory = catSubCategory,
                            imageUrl = catImageUrl,
                            standardMrp = catMrp.toDoubleOrNull() ?: 1999.0,
                            description = catDescription,
                            standardHighlights = "Standard Catalog Specification;Brand Verified"
                        )
                        viewModel.createCatalogItem(newItem) {
                            showAddCatalogDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DKartBlue)
                ) {
                    Text("Create Master Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCatalogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
