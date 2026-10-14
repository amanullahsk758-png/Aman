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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.data.model.Review
import com.example.ui.components.formatInr
import com.example.ui.theme.AssuredBlue
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val cartCount by viewModel.cartCount.collectAsState()
    val isWishlisted by viewModel.isWishlisted(product.id).collectAsState(initial = false)
    val activePincode by viewModel.activePincode.collectAsState()
    val reviews by viewModel.getProductReviews(product.id).collectAsState(initial = emptyList())

    // Variant selections
    val sizes = remember(product.availableSizes) {
        if (product.availableSizes.isNotBlank()) product.availableSizes.split(",") else emptyList()
    }
    var selectedSize by remember { mutableStateOf(sizes.firstOrNull() ?: "") }

    val colors = remember(product.availableColors) {
        if (product.availableColors.isNotBlank()) product.availableColors.split(",") else emptyList()
    }
    var selectedColor by remember { mutableStateOf(colors.firstOrNull() ?: "") }

    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableIntStateOf(5) }
    var reviewHeadline by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }
    var reviewerName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.brand,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWishlist(product) }) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color.Red else Color.White
                        )
                    }
                    IconButton(onClick = { viewModel.navigateTo(Screen.Cart) }) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(containerColor = FlipkartYellow, contentColor = Color.Black) {
                                        Text(cartCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FlipkartBlue)
            )
        },
        bottomBar = {
            // Sticky Buy Now & Add to Cart Action Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.addToCart(
                                product = product,
                                size = selectedSize,
                                color = selectedColor,
                                quantity = 1
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_add_to_cart_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FlipkartYellow,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ADD TO CART", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.addToCart(
                                product = product,
                                size = selectedSize,
                                color = selectedColor,
                                quantity = 1
                            )
                            viewModel.navigateTo(Screen.Checkout)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_buy_now_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FlipkartOrange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "BUY NOW", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNeutral)
        ) {
            // 1. Hero Image Box
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    if (product.isAssured) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .background(AssuredBlue, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "BharatBazaar Assured ✓",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Title, Ratings & Pricing Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = product.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating pill
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(FlipkartGreen, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format(Locale.US, "%.1f", product.rating),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${product.ratingCount} ratings & ${product.reviewCount} reviews",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Price
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = formatInr(product.price),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            if (product.mrp > product.price) {
                                Text(
                                    text = formatInr(product.mrp),
                                    fontSize = 15.sp,
                                    color = TextSecondary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                                Text(
                                    text = "${product.discountPercent}% off",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlipkartGreen
                                )
                            }
                        }

                        if (product.mrp > product.price) {
                            Text(
                                text = "You save ${formatInr(product.mrp - product.price)} on this order!",
                                color = FlipkartGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // 3. Variant Selector (Sizes & Colors)
            if (sizes.isNotEmpty() || colors.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (sizes.isNotEmpty()) {
                                Text(
                                    text = "SELECT VARIANT / SIZE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    sizes.forEach { size ->
                                        val isSel = selectedSize == size
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedSize = size },
                                            label = { Text(size, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = FlipkartBlue,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }

                            if (colors.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "SELECT COLOR",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    colors.forEach { color ->
                                        val isSel = selectedColor == color
                                        FilterChip(
                                            selected = isSel,
                                            onClick = { selectedColor = color },
                                            label = { Text(color, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = FlipkartBlue,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Delivery & Pincode Checker Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Delivery to $activePincode",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            TextButton(onClick = { viewModel.setShowPincodeDialog(true) }) {
                                Text("Change", color = FlipkartBlue, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "✓ FREE Delivery in ${product.deliveryDays} business days",
                            color = FlipkartGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "✓ Cash on Delivery (COD) available at this location",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // 5. Available Bank & Coupon Offers
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AVAILABLE OFFERS & PROMOTIONS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        listOf(
                            "Special Offer: Flat ₹500 off on orders above ₹2,499 with code BHARAT500",
                            "Bank Offer: 5% Unlimited Cashback on Flipkart / Axis Bank Credit Card",
                            "Festive Offer: Extra ₹300 off on UPI payments via PhonePe & Google Pay",
                            "No Cost EMI available on major credit cards starting ₹1,499/month"
                        ).forEach { offerText ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = FlipkartGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = offerText,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 6. Highlights & Description
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PRODUCT HIGHLIGHTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val highlightList = product.highlights.split(";").filter { it.isNotBlank() }
                        highlightList.forEach { point ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(FlipkartBlue, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = point.trim(), fontSize = 13.sp, color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "DESCRIPTION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.description,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 7. Trust & Policies
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Sync, contentDescription = null, tint = FlipkartBlue)
                            Text(text = product.returnPolicy, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.Security, contentDescription = null, tint = FlipkartBlue)
                            Text(text = product.warranty, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }

            // 8. Ratings & Reviews Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RATINGS & REVIEWS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Button(
                                onClick = { showReviewDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rate Product", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (reviews.isEmpty()) {
                            Text(
                                text = "Be the first to review this product!",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        } else {
                            reviews.forEach { rev ->
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .background(FlipkartGreen, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(rev.rating.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = rev.headline, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Text(text = rev.comment, fontSize = 12.sp, color = TextPrimary, modifier = Modifier.padding(top = 4.dp))
                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "${rev.userName}  •  ${rev.dateText}", fontSize = 11.sp, color = TextSecondary)
                                        if (rev.isVerified) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "✓ Certified Buyer", fontSize = 10.sp, color = FlipkartGreen, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Divider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFEEEEEE))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Write a Review", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select Rating:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { reviewRating = star }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star Stars",
                                    tint = if (star <= reviewRating) FlipkartYellow else Color.LightGray,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewHeadline,
                        onValueChange = { reviewHeadline = it },
                        label = { Text("Review Headline") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Detailed Review") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewHeadline.isNotBlank() && reviewComment.isNotBlank()) {
                            viewModel.submitReview(
                                productId = product.id,
                                rating = reviewRating,
                                headline = reviewHeadline,
                                comment = reviewComment,
                                userName = reviewerName
                            )
                            showReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue)
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
