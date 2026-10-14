package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun CategoriesScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    val currentCategory = categories.find { it.id == selectedCategoryId } ?: categories.firstOrNull()

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundNeutral)
            .testTag("categories_screen")
    ) {
        // Left Column: Category Side Navigation Rail
        LazyColumn(
            modifier = Modifier
                .width(96.dp)
                .fillMaxHeight()
                .background(Color(0xFFE8EEF5)),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category.id == currentCategory?.id
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCategoryId = category.id }
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) FlipkartBlue else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(category.iconName),
                            contentDescription = category.name,
                            tint = if (isSelected) Color.White else FlipkartBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.name,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) FlipkartBlue else TextPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Right Column: Subcategories & Catalog Preview
        if (currentCategory != null) {
            val categoryProducts = allProducts.filter { it.categoryId == currentCategory.id }
            val subCategories = categoryProducts.map { it.subCategory }.distinct()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White)
                    .padding(14.dp)
            ) {
                // Category Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentCategory.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${categoryProducts.size} Items Available",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        TextButton(
                            onClick = { viewModel.navigateTo(Screen.ProductList(category = currentCategory)) }
                        ) {
                            Text("See All", color = FlipkartBlue, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Subcategories Grid
                item {
                    Text(
                        text = "POPULAR IN ${currentCategory.name.uppercase()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(subCategories) { subCat ->
                    val subProducts = categoryProducts.filter { it.subCategory == subCat }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.navigateTo(Screen.ProductList(category = currentCategory, query = subCat))
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = subCat,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Starting from ₹${subProducts.minOfOrNull { it.price }?.toInt() ?: 499}",
                                    fontSize = 12.sp,
                                    color = FlipkartOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "TOP PRODUCTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(categoryProducts) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.navigateTo(Screen.ProductDetail(product)) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "₹${product.price.toInt()}  •  ${product.discountPercent}% Off",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlipkartBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
