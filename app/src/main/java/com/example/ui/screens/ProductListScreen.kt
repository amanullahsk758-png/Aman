package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.ui.components.HorizontalProductCard
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    category: Category?,
    query: String?,
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val allProducts by viewModel.allProducts.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val wishlistedIds = wishlistItems.map { it.wishlistItem.productId }.toSet()

    var selectedSort by remember { mutableStateOf(SortOption.RELEVANCE) }
    var onlyAssured by remember { mutableStateOf(false) }
    var inStockOnly by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Filter products
    val filteredProducts = allProducts.filter { product ->
        val matchesCategory = category == null || product.categoryId == category.id
        val matchesQuery = query.isNullOrBlank() ||
                product.title.contains(query, ignoreCase = true) ||
                product.brand.contains(query, ignoreCase = true) ||
                product.subCategory.contains(query, ignoreCase = true) ||
                (query.equals("Deals", ignoreCase = true) && product.isDealOfTheDay) ||
                (query.equals("Trending", ignoreCase = true) && product.isTrending) ||
                (query.equals("Best", ignoreCase = true) && product.isBestSeller) ||
                (query.equals("Festival", ignoreCase = true) && product.discountPercent >= 20)

        val matchesAssured = !onlyAssured || product.isAssured
        val matchesStock = !inStockOnly || product.stock > 0

        matchesCategory && matchesQuery && matchesAssured && matchesStock
    }.let { list ->
        when (selectedSort) {
            SortOption.RELEVANCE -> list
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.DISCOUNT -> list.sortedByDescending { it.discountPercent }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = category?.name ?: (if (!query.isNullOrBlank()) "Results for '$query'" else "All Products"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${filteredProducts.size} Products Found",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FlipkartBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNeutral)
        ) {
            // Filter and Sort Chips Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sort Chip with Menu
                    Box {
                        FilterChip(
                            selected = selectedSort != SortOption.RELEVANCE,
                            onClick = { showSortMenu = true },
                            label = { Text("Sort: ${selectedSort.label}", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FlipkartBlue.copy(alpha = 0.15f),
                                selectedLabelColor = FlipkartBlue
                            )
                        )
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        selectedSort = option
                                        showSortMenu = false
                                    },
                                    trailingIcon = if (selectedSort == option) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = FlipkartBlue) }
                                    } else null
                                )
                            }
                        }
                    }

                    // Assured Filter
                    FilterChip(
                        selected = onlyAssured,
                        onClick = { onlyAssured = !onlyAssured },
                        label = { Text("✓ Assured", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FlipkartBlue,
                            selectedLabelColor = Color.White
                        )
                    )

                    // In Stock Filter
                    FilterChip(
                        selected = inStockOnly,
                        onClick = { inStockOnly = !inStockOnly },
                        label = { Text("In Stock", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FlipkartBlue.copy(alpha = 0.15f),
                            selectedLabelColor = FlipkartBlue
                        )
                    )
                }
            }

            if (filteredProducts.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching products found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Try clearing filters or search for another term",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts) { product ->
                        HorizontalProductCard(
                            product = product,
                            isWishlisted = wishlistedIds.contains(product.id),
                            onProductClick = { viewModel.navigateTo(Screen.ProductDetail(it)) },
                            onWishlistToggle = { viewModel.toggleWishlist(it) },
                            onAddToCart = { viewModel.addToCart(it) }
                        )
                    }
                }
            }
        }
    }
}
