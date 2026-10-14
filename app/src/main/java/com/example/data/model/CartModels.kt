package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val cartItemId: Long = 0,
    val productId: String,
    val quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

data class CartItemWithProduct(
    @Embedded val cartItem: CartItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product?
)

data class WishlistItemWithProduct(
    @Embedded val wishlistItem: WishlistItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product?
)
