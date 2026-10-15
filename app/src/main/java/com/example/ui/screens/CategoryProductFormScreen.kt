package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.Product
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
fun CategoryProductFormScreen(
    catalogItem: CatalogItem?,
    seller: Seller,
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val categoryId = catalogItem?.categoryId ?: "fashion_men"
    val subCategory = catalogItem?.subCategory ?: "General"

    var title by remember { mutableStateOf(catalogItem?.title ?: "") }
    var brand by remember { mutableStateOf(catalogItem?.brand ?: "") }
    var mrp by remember { mutableStateOf(catalogItem?.standardMrp?.toInt()?.toString() ?: "1999") }
    var sellingPrice by remember { mutableStateOf((catalogItem?.standardMrp?.times(0.85)?.toInt() ?: 1499).toString()) }
    var stockQuantity by remember { mutableStateOf("50") }
    var sku by remember { mutableStateOf("SKU-" + (1000..9999).random()) }
    var deliveryDays by remember { mutableStateOf("2") }

    // Strict 1-to-6 Images List (Enforced Max 6)
    val uploadedImages = remember {
        mutableStateListOf<String>().apply {
            if (!catalogItem?.imageUrl.isNullOrBlank()) {
                add(catalogItem!!.imageUrl)
            }
        }
    }
    var newImageUrlInput by remember { mutableStateOf("") }
    var imageError by remember { mutableStateOf<String?>(null) }

    // Dynamic Category-Specific Attributes
    var attrGender by remember { mutableStateOf("Men") }
    var attrFabric by remember { mutableStateOf("100% Cotton") }
    var attrFit by remember { mutableStateOf("Slim Fit") }
    var attrSize by remember { mutableStateOf("S, M, L, XL") }
    var attrColor by remember { mutableStateOf("Navy Blue, Black, White") }

    var attrShoeSole by remember { mutableStateOf("Anti-Slip Rubber Sole") }
    var attrShoeType by remember { mutableStateOf("Running / Walking") }

    var attrProcessor by remember { mutableStateOf("Octa-Core High Speed") }
    var attrRamStorage by remember { mutableStateOf("8GB RAM / 128GB Storage") }
    var attrWarranty by remember { mutableStateOf("1 Year Manufacturer Warranty") }

    var attrNetQuantity by remember { mutableStateOf("100 ml") }
    var attrShelfLife by remember { mutableStateOf("24 Months from MFG") }

    val calcMrp = mrp.toDoubleOrNull() ?: 1999.0
    val calcPrice = sellingPrice.toDoubleOrNull() ?: 1499.0
    val discountPercent = if (calcMrp > calcPrice) (((calcMrp - calcPrice) / calcMrp) * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Listing Details & Pricing", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("Category: ${categoryId.replace('_', ' ').uppercase()}", fontSize = 11.sp, color = DKartYellow)
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNeutral)
                .testTag("category_product_form_screen"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("CATALOG BASE ITEM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Text(text = title.ifBlank { "Custom Product Listing" }, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text(text = "Brand: $brand  •  Seller: ${seller.displayName}", fontSize = 12.sp, color = DKartBlue)
                    }
                }
            }

            // 1. Strict 6-Image Gallery Manager (Mandatory Max 6 Limit)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = DKartBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PRODUCT IMAGES (${uploadedImages.size}/6)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            }
                            Text("1st is Primary", fontSize = 11.sp, color = DKartGreen, fontWeight = FontWeight.Bold)
                        }

                        Text("Max 6 images allowed per Flipkart seller policy. Drag/reorder with arrows.", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))

                        Spacer(modifier = Modifier.height(10.dp))

                        // Images Preview Strip
                        if (uploadedImages.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                itemsIndexed(uploadedImages) { index, imgUrl ->
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(
                                                width = if (index == 0) 2.dp else 1.dp,
                                                color = if (index == 0) DKartBlue else Color.LightGray,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                    ) {
                                        AsyncImage(
                                            model = imgUrl,
                                            contentDescription = "Image $index",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        // Primary Tag
                                        if (index == 0) {
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .background(DKartBlue, RoundedCornerShape(bottomEnd = 4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text("Cover", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Delete Image Icon
                                        IconButton(
                                            onClick = {
                                                uploadedImages.removeAt(index)
                                                imageError = null
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(24.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }

                                        // Move to Front (Make Primary)
                                        if (index > 0) {
                                            IconButton(
                                                onClick = {
                                                    val moved = uploadedImages.removeAt(index)
                                                    uploadedImages.add(0, moved)
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .size(24.dp)
                                                    .background(DKartYellow, CircleShape)
                                            ) {
                                                Icon(Icons.Default.ArrowUpward, contentDescription = "Set Primary", tint = Color.Black, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add Image URL Input (Strictly checks limit < 6)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newImageUrlInput,
                                onValueChange = { newImageUrlInput = it },
                                placeholder = { Text("Paste image URL (https://...)", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                enabled = uploadedImages.size < 6
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (uploadedImages.size >= 6) {
                                        imageError = "Strict Limit: Cannot add more than 6 images per listing!"
                                    } else if (newImageUrlInput.isNotBlank()) {
                                        uploadedImages.add(newImageUrlInput.trim())
                                        newImageUrlInput = ""
                                        imageError = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DKartBlue),
                                shape = RoundedCornerShape(4.dp),
                                enabled = uploadedImages.size < 6
                            ) {
                                Text("+ Add")
                            }
                        }

                        if (imageError != null) {
                            Text(
                                text = imageError!!,
                                color = Color.Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // 2. Pricing & Inventory Management
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = DKartBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PRICING & STOCK", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = mrp, onValueChange = { mrp = it }, label = { Text("MRP (₹) *") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = sellingPrice, onValueChange = { sellingPrice = it }, label = { Text("Your Price (₹) *") }, singleLine = true, modifier = Modifier.weight(1f))
                        }

                        if (discountPercent > 0) {
                            Text("Discount to buyer: $discountPercent% Off", color = DKartGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = stockQuantity, onValueChange = { stockQuantity = it }, label = { Text("Available Stock *") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("Seller SKU Code") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // 3. Dynamic Category-Specific Product Form (Clothing vs Footwear vs Electronics vs Beauty)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Style, contentDescription = null, tint = DKartBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CATEGORY-SPECIFIC ATTRIBUTES", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        when {
                            categoryId.contains("fashion", ignoreCase = true) -> {
                                Text("Apparel & Fashion Specs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = attrGender, onValueChange = { attrGender = it }, label = { Text("Gender") }, modifier = Modifier.weight(1f), singleLine = true)
                                    OutlinedTextField(value = attrFabric, onValueChange = { attrFabric = it }, label = { Text("Fabric / Material") }, modifier = Modifier.weight(1f), singleLine = true)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = attrFit, onValueChange = { attrFit = it }, label = { Text("Fit & Pattern") }, modifier = Modifier.weight(1f), singleLine = true)
                                    OutlinedTextField(value = attrSize, onValueChange = { attrSize = it }, label = { Text("Available Sizes") }, modifier = Modifier.weight(1f), singleLine = true)
                                }
                            }
                            categoryId.contains("footwear", ignoreCase = true) -> {
                                Text("Footwear Specifications", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = attrShoeType, onValueChange = { attrShoeType = it }, label = { Text("Shoe Type") }, modifier = Modifier.weight(1f), singleLine = true)
                                    OutlinedTextField(value = attrShoeSole, onValueChange = { attrShoeSole = it }, label = { Text("Sole Material") }, modifier = Modifier.weight(1f), singleLine = true)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(value = attrSize, onValueChange = { attrSize = it }, label = { Text("UK Sizes (e.g. 7, 8, 9, 10)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            }
                            categoryId.contains("mobiles", ignoreCase = true) || categoryId.contains("electronics", ignoreCase = true) -> {
                                Text("Electronics & Tech Specifications", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = attrProcessor, onValueChange = { attrProcessor = it }, label = { Text("Processor / Chip") }, modifier = Modifier.weight(1f), singleLine = true)
                                    OutlinedTextField(value = attrRamStorage, onValueChange = { attrRamStorage = it }, label = { Text("RAM & Storage") }, modifier = Modifier.weight(1f), singleLine = true)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(value = attrWarranty, onValueChange = { attrWarranty = it }, label = { Text("Brand Warranty Details") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            }
                            else -> {
                                Text("General Specifications", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = attrNetQuantity, onValueChange = { attrNetQuantity = it }, label = { Text("Net Quantity / Weight") }, modifier = Modifier.weight(1f), singleLine = true)
                                    OutlinedTextField(value = attrShelfLife, onValueChange = { attrShelfLife = it }, label = { Text("Shelf Life / Care") }, modifier = Modifier.weight(1f), singleLine = true)
                                }
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        val images = uploadedImages.take(6)
                        val primaryImage = images.firstOrNull() ?: "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80"
                        val extraImages = images.drop(1).joinToString("|")

                        val newProduct = Product(
                            id = "prod_offer_" + System.currentTimeMillis().toString().takeLast(6),
                            catalogId = catalogItem?.id ?: "",
                            sellerId = seller.id,
                            sellerName = seller.displayName,
                            title = title.ifBlank { "DKart Marketplace Listing" },
                            brand = brand.ifBlank { "DKart Brand" },
                            categoryId = categoryId,
                            subCategory = subCategory,
                            mrp = calcMrp,
                            price = calcPrice,
                            discountPercent = discountPercent,
                            rating = 4.8f,
                            ratingCount = 1,
                            reviewCount = 0,
                            stock = stockQuantity.toIntOrNull() ?: 20,
                            imageUrl = primaryImage,
                            additionalImages = extraImages,
                            description = catalogItem?.description ?: "Quality product offered by ${seller.displayName} with fast delivery and DKart Assured guarantee.",
                            highlights = catalogItem?.standardHighlights ?: "100% Brand Certified;Fast Dispatch;Return Eligible",
                            availableSizes = attrSize,
                            availableColors = attrColor,
                            deliveryDays = deliveryDays.toIntOrNull() ?: 2,
                            isAssured = true
                        )

                        viewModel.saveProduct(newProduct)
                        viewModel.navigateTo(Screen.SellerDashboard)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_listing_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = DKartOrange),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("PUBLISH LISTING TO DKART", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
