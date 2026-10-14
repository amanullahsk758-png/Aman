package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val badge: String = "",
    val displayOrder: Int = 0
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val title: String,
    val brand: String,
    val categoryId: String,
    val subCategory: String,
    val mrp: Double,
    val price: Double,
    val discountPercent: Int,
    val rating: Float,
    val ratingCount: Int,
    val reviewCount: Int,
    val stock: Int,
    val imageUrl: String,
    val description: String,
    val highlights: String,
    val availableSizes: String = "",
    val availableColors: String = "",
    val warranty: String = "1 Year Brand Warranty",
    val returnPolicy: String = "7 Days Replacement Policy",
    val deliveryDays: Int = 2,
    val isAssured: Boolean = true,
    val isDealOfTheDay: Boolean = false,
    val isTrending: Boolean = false,
    val isBestSeller: Boolean = false,
    val sellerName: String = "BharatBazaar Retail Pvt Ltd"
)
