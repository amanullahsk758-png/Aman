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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.model.CatalogItem
import com.example.data.model.Seller
import com.example.ui.components.formatInr
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.DKartBlue
import com.example.ui.theme.DKartDarkBlue
import com.example.ui.theme.DKartGreen
import com.example.ui.theme.DKartYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogSelectionScreen(
    seller: Seller,
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val catalogItems by viewModel.catalogItems.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showCreateCatalogDialog by remember { mutableStateOf(false) }

    // Create New Catalog Form state (Step B)
    var newTitle by remember { mutableStateOf("") }
    var newBrand by remember { mutableStateOf("") }
    var newModelNumber by remember { mutableStateOf("") }
    var newGtin by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("fashion_men") }
    var newSubCategory by remember { mutableStateOf("T-Shirts") }
    var newMrp by remember { mutableStateOf("1499") }
    var newImageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=600&q=80") }
    var newHighlights by remember { mutableStateOf("100% Cotton;Bio Washed;Regular Fit") }

    val filteredCatalog = catalogItems.filter { item ->
        searchQuery.isBlank() ||
        item.title.contains(searchQuery, ignoreCase = true) ||
        item.brand.contains(searchQuery, ignoreCase = true) ||
        item.modelNumber.contains(searchQuery, ignoreCase = true) ||
        item.gtin.contains(searchQuery, ignoreCase = true) ||
        item.subCategory.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Search Master Catalog", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("Step 1 of 2: Select Brand & Product", fontSize = 11.sp, color = DKartYellow)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .testTag("catalog_selection_screen")
        ) {
            // Search Bar Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by Product Name, Brand, Model, GTIN/EAN...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Found ${filteredCatalog.size} catalog items", fontSize = 11.sp, color = TextSecondary)
                        TextButton(onClick = { showCreateCatalogDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = DKartBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Create New Catalog Product", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DKartBlue)
                        }
                    }
                }
            }

            // Catalog Items List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCatalog) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Proceed to Step C (Category-Specific Listing Form)
                                viewModel.navigateTo(Screen.CategoryProductForm(catalogItem = item, seller = seller))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFF9F9F9))) {
                                AsyncImage(model = item.imageUrl, contentDescription = item.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.brand.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Text(text = item.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, color = TextPrimary)
                                if (item.modelNumber.isNotBlank()) {
                                    Text(text = "Model: ${item.modelNumber}  •  GTIN: ${item.gtin}", fontSize = 10.sp, color = TextSecondary)
                                }
                                Text(text = "Standard MRP: ${formatInr(item.standardMrp)}", fontSize = 11.sp, color = DKartBlue, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.navigateTo(Screen.CategoryProductForm(catalogItem = item, seller = seller))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DKartBlue),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Sell This", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // If not found, show button to create
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Cannot find your product in the master catalog?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("Create a brand-new catalog product with its brand, specifications & images.", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showCreateCatalogDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DKartDarkBlue),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Create New Catalog Product", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Step B Dialog: Create New Catalog Product
    if (showCreateCatalogDialog) {
        AlertDialog(
            onDismissRequest = { showCreateCatalogDialog = false },
            title = { Text("Create New Catalog Item", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Product Title *") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = newBrand, onValueChange = { newBrand = it }, label = { Text("Brand *") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = newModelNumber, onValueChange = { newModelNumber = it }, label = { Text("Model #") }, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(value = newGtin, onValueChange = { newGtin = it }, label = { Text("GTIN/EAN/Barcode") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = newMrp, onValueChange = { newMrp = it }, label = { Text("Standard MRP (₹)") }, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = newImageUrl, onValueChange = { newImageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = newHighlights, onValueChange = { newHighlights = it }, label = { Text("Highlights (separated by ';')") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newBrand.isNotBlank()) {
                            val newItem = CatalogItem(
                                id = "CAT_" + System.currentTimeMillis().toString().takeLast(6),
                                title = newTitle,
                                brand = newBrand,
                                modelNumber = newModelNumber,
                                gtin = newGtin,
                                categoryId = newCategory,
                                subCategory = newSubCategory,
                                imageUrl = newImageUrl,
                                standardMrp = newMrp.toDoubleOrNull() ?: 999.0,
                                description = "Verified DKart Catalog item for $newBrand $newTitle.",
                                standardHighlights = newHighlights
                            )
                            viewModel.createCatalogItem(newItem) {
                                showCreateCatalogDialog = false
                                viewModel.navigateTo(Screen.CategoryProductForm(catalogItem = newItem, seller = seller))
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DKartBlue)
                ) {
                    Text("Save & Proceed to List")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCatalogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
