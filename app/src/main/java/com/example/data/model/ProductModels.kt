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

@Entity(tableName = "sellers")
data class Seller(
    @PrimaryKey val id: String,
    val fullName: String,
    val businessName: String,
    val displayName: String,
    val email: String,
    val phone: String,
    val pickupAddress: String,
    val bankAccountNumber: String = "",
    val bankIfsc: String = "",
    val gstin: String = "",
    val pan: String = "",
    val status: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED", "SUSPENDED"
    val joinedAt: Long = System.currentTimeMillis(),
    val rating: Float = 4.6f,
    val totalOrders: Int = 0,
    val commissionPercent: Double = 10.0 // Marketplace commission rate
)

@Entity(tableName = "catalog_items")
data class CatalogItem(
    @PrimaryKey val id: String,
    val title: String,
    val brand: String,
    val modelNumber: String = "",
    val gtin: String = "", // GTIN/EAN/UPC barcode
    val categoryId: String,
    val subCategory: String,
    val imageUrl: String,
    val standardMrp: Double,
    val description: String,
    val standardHighlights: String,
    val standardSizes: String = "",
    val standardColors: String = ""
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val catalogId: String = "",
    val sellerId: String = "seller_dkart_direct",
    val sellerName: String = "DKart Retail Official",
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
    val imageUrl: String, // Primary image (image 1)
    val additionalImages: String = "", // Up to 5 additional images separated by pipe '|' (Max 6 total)
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
    val categorySpecificAttributes: String = "", // Dynamic JSON / key-value specs
    val listingStatus: String = "ACTIVE" // "ACTIVE", "PENDING_APPROVAL", "OUT_OF_STOCK"
) {
    // Helper to get all images strictly capped at 6
    fun getAllImages(): List<String> {
        val list = mutableListOf<String>()
        if (imageUrl.isNotBlank()) list.add(imageUrl)
        if (additionalImages.isNotBlank()) {
            val extras = additionalImages.split("|").filter { it.isNotBlank() }
            list.addAll(extras)
        }
        return list.take(6)
    }
}
