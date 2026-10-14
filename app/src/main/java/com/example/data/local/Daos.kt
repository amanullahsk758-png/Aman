package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Address
import com.example.data.model.CartItem
import com.example.data.model.CartItemWithProduct
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderWithItems
import com.example.data.model.Product
import com.example.data.model.Review
import com.example.data.model.WishlistItem
import com.example.data.model.WishlistItemWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY isDealOfTheDay DESC, rating DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategory(categoryId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isDealOfTheDay = 1")
    fun getDealsOfTheDay(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isTrending = 1")
    fun getTrendingProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isBestSeller = 1")
    fun getBestSellers(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: String): Flow<Product?>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR subCategory LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: String, newStock: Int)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}

@Dao
interface CartDao {
    @Transaction
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getCartWithProducts(): Flow<List<CartItemWithProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE cartItemId = :cartItemId")
    suspend fun updateQuantity(cartItemId: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE cartItemId = :cartItemId")
    suspend fun deleteCartItem(cartItemId: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartCount(): Flow<Int>
}

@Dao
interface WishlistDao {
    @Transaction
    @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
    fun getWishlistWithProducts(): Flow<List<WishlistItemWithProduct>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isWishlisted(productId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItem)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: String)

    @Query("SELECT COUNT(*) FROM wishlist_items")
    fun getWishlistCount(): Flow<Int>
}

@Dao
interface OrderDao {
    @Transaction
    @Query("SELECT * FROM orders ORDER BY placedAt DESC")
    fun getAllOrders(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<OrderWithItems?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses ORDER BY isDefault DESC, id DESC")
    fun getAllAddresses(): Flow<List<Address>>

    @Query("SELECT * FROM addresses WHERE isDefault = 1 LIMIT 1")
    fun getDefaultAddress(): Flow<Address?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: Address): Long

    @Query("UPDATE addresses SET isDefault = 0")
    suspend fun clearDefaultFlags()

    @Query("UPDATE addresses SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultAddress(id: Long)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddress(id: Long)

    @Query("SELECT COUNT(*) FROM addresses")
    suspend fun getAddressCount(): Int
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons")
    fun getAllCoupons(): Flow<List<Coupon>>

    @Query("SELECT * FROM coupons WHERE code = :code LIMIT 1")
    suspend fun getCouponByCode(code: String): Coupon?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupons(coupons: List<Coupon>)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY id DESC")
    fun getReviewsForProduct(productId: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>)
}
