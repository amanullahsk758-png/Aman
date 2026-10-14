package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Address
import com.example.data.model.CartItem
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.Product
import com.example.data.model.Review
import com.example.data.model.WishlistItem

@Database(
    entities = [
        Category::class,
        Product::class,
        CartItem::class,
        WishlistItem::class,
        Address::class,
        OrderEntity::class,
        OrderItemEntity::class,
        Coupon::class,
        Review::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BharatBazaarDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
    abstract fun couponDao(): CouponDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: BharatBazaarDatabase? = null

        fun getInstance(context: Context): BharatBazaarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BharatBazaarDatabase::class.java,
                    "bharat_bazaar_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
