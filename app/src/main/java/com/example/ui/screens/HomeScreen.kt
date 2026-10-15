package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.ui.components.BannerCarousel
import com.example.ui.components.CategoryBubble
import com.example.ui.components.VerticalProductCard
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.DKartBlue
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val dealsOfTheDay by viewModel.dealsOfTheDay.collectAsState()
    val trendingProducts by viewModel.trendingProducts.collectAsState()
    val bestSellers by viewModel.bestSellers.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()

    val wishlistedIds = wishlistItems.map { it.wishlistItem.productId }.toSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundNeutral)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 1. Categories Bubble Row
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryBubble(
                            category = category,
                            onClick = {
                                viewModel.navigateTo(Screen.ProductList(category = it))
                            }
                        )
                    }
                }
            }
        }

        // 2. Promotional Hero Banner Carousel
        item {
            Spacer(modifier = Modifier.height(8.dp))
            BannerCarousel(
                onBannerClick = { index ->
                    if (index == 0) {
                        viewModel.navigateTo(Screen.ProductList(query = "Festival"))
                    } else {
                        val mobileCategory = categories.find { it.id == "mobiles" }
                        viewModel.navigateTo(Screen.ProductList(category = mobileCategory))
                    }
                }
            )
        }

        // 3. Super Saver Offer Strip
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(FlipkartDarkBlue, FlipkartBlue, FlipkartOrange)
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = FlipkartYellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "BHARAT BIG SAVINGS DAYS",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Extra ₹500 Off with Coupon BHARAT500",
                                    color = FlipkartYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Shop Now",
                                color = FlipkartBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. Deals of the Day (Countdown section)
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp)
            ) {
                // Header with countdown
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(FlipkartOrange, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DEAL OF THE DAY",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "19h 42m left",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.ProductList(query = "Deals")) }
                    ) {
                        Text(
                            text = "View All",
                            color = FlipkartBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = FlipkartBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(dealsOfTheDay) { product ->
                        VerticalProductCard(
                            product = product,
                            isWishlisted = wishlistedIds.contains(product.id),
                            onProductClick = { viewModel.navigateTo(Screen.ProductDetail(it)) },
                            onWishlistToggle = { viewModel.toggleWishlist(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            modifier = Modifier.width(170.dp)
                        )
                    }
                }
            }
        }

        // 5. Best Sellers Carousel
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = FlipkartYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Best Sellers & Top Rated",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Highest rated by millions of Indian shoppers",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.ProductList(query = "Best")) }
                    ) {
                        Text(
                            text = "View All",
                            color = FlipkartBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(bestSellers) { product ->
                        VerticalProductCard(
                            product = product,
                            isWishlisted = wishlistedIds.contains(product.id),
                            onProductClick = { viewModel.navigateTo(Screen.ProductDetail(it)) },
                            onWishlistToggle = { viewModel.toggleWishlist(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            modifier = Modifier.width(170.dp)
                        )
                    }
                }
            }
        }

        // 6. Trending Section
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = FlipkartGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Trending Fashion & Gadgets",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.ProductList(query = "Trending")) }
                    ) {
                        Text(
                            text = "View All",
                            color = FlipkartBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(trendingProducts) { product ->
                        VerticalProductCard(
                            product = product,
                            isWishlisted = wishlistedIds.contains(product.id),
                            onProductClick = { viewModel.navigateTo(Screen.ProductDetail(it)) },
                            onWishlistToggle = { viewModel.toggleWishlist(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            modifier = Modifier.width(170.dp)
                        )
                    }
                }
            }
        }

        // 7. Trust & Security Badges (Flipkart style)
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✓ 100% Original", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FlipkartBlue)
                        Text(text = "Direct from Brands", fontSize = 9.sp, color = TextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "↺ 7 Days Return", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FlipkartBlue)
                        Text(text = "Instant Doorstep Pickup", fontSize = 9.sp, color = TextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⚡ DKart Assured", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DKartBlue)
                        Text(text = "Fast & Safe Delivery", fontSize = 9.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
