package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "addresses")
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val phoneNumber: String,
    val pincode: String,
    val addressLine: String,
    val city: String,
    val state: String,
    val addressType: String = "HOME", // "HOME" or "WORK"
    val isDefault: Boolean = false
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val placedAt: Long = System.currentTimeMillis(),
    val status: String, // "CONFIRMED", "PROCESSING", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"
    val subtotal: Double,
    val discount: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val paymentMethod: String, // "COD", "RAZORPAY_UPI", "CARD"
    val paymentStatus: String, // "SUCCESS", "PENDING"
    val deliveryPincode: String,
    val shippingAddress: String,
    val expectedDeliveryDate: String,
    val couponCode: String? = null
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: String,
    val productId: String,
    val sellerId: String = "seller_dkart_direct",
    val sellerName: String = "DKart Retail Official",
    val title: String,
    val brand: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val selectedSize: String = "",
    val selectedColor: String = ""
)

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)

@Entity(tableName = "coupons")
data class Coupon(
    @PrimaryKey val code: String,
    val description: String,
    val discountType: String, // "PERCENT" or "FLAT"
    val discountValue: Double,
    val minOrderValue: Double,
    val maxDiscount: Double
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val userName: String,
    val rating: Int,
    val headline: String,
    val comment: String,
    val dateText: String,
    val isVerified: Boolean = true
)
