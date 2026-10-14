package com.example.data.repository

import com.example.data.local.BharatBazaarDatabase
import com.example.data.local.InitialDataSeed
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BharatBazaarRepository(private val db: BharatBazaarDatabase) {

    private val productDao = db.productDao()
    private val categoryDao = db.categoryDao()
    private val cartDao = db.cartDao()
    private val wishlistDao = db.wishlistDao()
    private val orderDao = db.orderDao()
    private val addressDao = db.addressDao()
    private val couponDao = db.couponDao()
    private val reviewDao = db.reviewDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        if (categoryDao.getCategoryCount() == 0) {
            categoryDao.insertCategories(InitialDataSeed.categories)
        }
        if (productDao.getProductCount() == 0) {
            productDao.insertProducts(InitialDataSeed.products)
        }
        if (couponDao.getAllCoupons().firstOrNull()?.isEmpty() != false) {
            couponDao.insertCoupons(InitialDataSeed.coupons)
        }
        if (addressDao.getAddressCount() == 0) {
            InitialDataSeed.defaultAddresses.forEach { addressDao.insertAddress(it) }
        }
        if (reviewDao.getReviewsForProduct("prod_mob_01").firstOrNull()?.isEmpty() != false) {
            reviewDao.insertReviews(InitialDataSeed.sampleReviews)
        }
    }

    // Categories
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    // Products
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val dealsOfTheDay: Flow<List<Product>> = productDao.getDealsOfTheDay()
    val trendingProducts: Flow<List<Product>> = productDao.getTrendingProducts()
    val bestSellers: Flow<List<Product>> = productDao.getBestSellers()

    fun getProductsByCategory(categoryId: String): Flow<List<Product>> =
        productDao.getProductsByCategory(categoryId)

    fun getProductById(id: String): Flow<Product?> =
        productDao.getProductById(id)

    fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query)

    suspend fun saveProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    // Cart
    val cartWithProducts: Flow<List<CartItemWithProduct>> = cartDao.getCartWithProducts()
    val cartCount: Flow<Int> = cartDao.getCartCount()

    suspend fun addToCart(productId: String, size: String = "", color: String = "", quantity: Int = 1) {
        val currentCart = cartDao.getCartWithProducts().firstOrNull() ?: emptyList()
        val existing = currentCart.find {
            it.cartItem.productId == productId &&
            it.cartItem.selectedSize == size &&
            it.cartItem.selectedColor == color
        }
        if (existing != null) {
            cartDao.updateQuantity(existing.cartItem.cartItemId, existing.cartItem.quantity + quantity)
        } else {
            cartDao.insertCartItem(
                CartItem(
                    productId = productId,
                    quantity = quantity,
                    selectedSize = size,
                    selectedColor = color
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteCartItem(cartItemId)
        } else {
            cartDao.updateQuantity(cartItemId, newQuantity)
        }
    }

    suspend fun removeCartItem(cartItemId: Long) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Wishlist
    val wishlistWithProducts: Flow<List<WishlistItemWithProduct>> = wishlistDao.getWishlistWithProducts()
    val wishlistCount: Flow<Int> = wishlistDao.getWishlistCount()

    fun isWishlisted(productId: String): Flow<Boolean> = wishlistDao.isWishlisted(productId)

    suspend fun toggleWishlist(productId: String) {
        val isWish = wishlistDao.isWishlisted(productId).firstOrNull() ?: false
        if (isWish) {
            wishlistDao.removeFromWishlist(productId)
        } else {
            wishlistDao.addToWishlist(WishlistItem(productId = productId))
        }
    }

    // Addresses
    val allAddresses: Flow<List<Address>> = addressDao.getAllAddresses()
    val defaultAddress: Flow<Address?> = addressDao.getDefaultAddress()

    suspend fun addAddress(address: Address) {
        if (address.isDefault) {
            addressDao.clearDefaultFlags()
        }
        val id = addressDao.insertAddress(address)
        if (addressDao.getAddressCount() == 1) {
            addressDao.setDefaultAddress(id)
        }
    }

    suspend fun setDefaultAddress(id: Long) {
        addressDao.clearDefaultFlags()
        addressDao.setDefaultAddress(id)
    }

    suspend fun deleteAddress(id: Long) {
        addressDao.deleteAddress(id)
    }

    // Coupons
    val availableCoupons: Flow<List<Coupon>> = couponDao.getAllCoupons()

    suspend fun validateCoupon(code: String, subtotal: Double): Pair<Boolean, String> {
        val coupon = couponDao.getCouponByCode(code.trim().uppercase())
            ?: return Pair(false, "Invalid coupon code")
        if (subtotal < coupon.minOrderValue) {
            return Pair(false, "Min order value for ${coupon.code} is ₹${coupon.minOrderValue.toInt()}")
        }
        return Pair(true, "Coupon applied successfully!")
    }

    suspend fun calculateDiscount(code: String?, subtotal: Double): Double {
        if (code.isNullOrBlank()) return 0.0
        val coupon = couponDao.getCouponByCode(code.trim().uppercase()) ?: return 0.0
        if (subtotal < coupon.minOrderValue) return 0.0

        return if (coupon.discountType == "PERCENT") {
            val calc = (subtotal * coupon.discountValue) / 100.0
            calc.coerceAtMost(coupon.maxDiscount)
        } else {
            coupon.discountValue.coerceAtMost(coupon.maxDiscount)
        }
    }

    // Orders
    val allOrders: Flow<List<OrderWithItems>> = orderDao.getAllOrders()

    fun getOrderById(orderId: String): Flow<OrderWithItems?> = orderDao.getOrderById(orderId)

    suspend fun placeOrder(
        cartItems: List<CartItemWithProduct>,
        deliveryAddress: Address,
        paymentMethod: String,
        couponCode: String? = null
    ): String {
        val subtotal = cartItems.sumOf { (it.product?.price ?: 0.0) * it.cartItem.quantity }
        val discount = calculateDiscount(couponCode, subtotal)
        val deliveryFee = if (subtotal > 500.0) 0.0 else 40.0
        val total = (subtotal - discount + deliveryFee).coerceAtLeast(0.0)

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val estimatedDeliveryDate = dateFormat.format(Date(System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000L)))
        val orderId = "OD" + System.currentTimeMillis().toString().takeLast(8) + (10..99).random()

        val orderEntity = OrderEntity(
            orderId = orderId,
            placedAt = System.currentTimeMillis(),
            status = "CONFIRMED",
            subtotal = subtotal,
            discount = discount,
            deliveryFee = deliveryFee,
            totalAmount = total,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "COD") "PENDING" else "SUCCESS",
            deliveryPincode = deliveryAddress.pincode,
            shippingAddress = "${deliveryAddress.fullName}, ${deliveryAddress.addressLine}, ${deliveryAddress.city}, ${deliveryAddress.state} - ${deliveryAddress.pincode} (Ph: ${deliveryAddress.phoneNumber})",
            expectedDeliveryDate = estimatedDeliveryDate,
            couponCode = couponCode
        )

        val orderItems = cartItems.mapNotNull { item ->
            val prod = item.product ?: return@mapNotNull null
            // update stock
            productDao.updateStock(prod.id, (prod.stock - item.cartItem.quantity).coerceAtLeast(0))

            OrderItemEntity(
                orderId = orderId,
                productId = prod.id,
                title = prod.title,
                brand = prod.brand,
                imageUrl = prod.imageUrl,
                price = prod.price,
                quantity = item.cartItem.quantity,
                selectedSize = item.cartItem.selectedSize,
                selectedColor = item.cartItem.selectedColor
            )
        }

        orderDao.insertOrder(orderEntity)
        orderDao.insertOrderItems(orderItems)
        cartDao.clearCart()

        return orderId
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        orderDao.updateOrderStatus(orderId, newStatus)
    }

    // Reviews
    fun getProductReviews(productId: String): Flow<List<Review>> =
        reviewDao.getReviewsForProduct(productId)

    suspend fun addReview(review: Review) {
        reviewDao.insertReview(review)
    }

    // Pincode validation
    fun checkPincodeDelivery(pincode: String): PincodeCheckResult {
        val clean = pincode.trim()
        if (clean.length != 6 || !clean.all { it.isDigit() }) {
            return PincodeCheckResult(isValid = false, message = "Please enter a valid 6-digit Indian PIN code")
        }
        val city = when {
            clean.startsWith("40") -> "Mumbai & Thane"
            clean.startsWith("11") -> "Delhi NCR"
            clean.startsWith("56") -> "Bengaluru"
            clean.startsWith("50") -> "Hyderabad"
            clean.startsWith("60") -> "Chennai"
            clean.startsWith("70") -> "Kolkata"
            clean.startsWith("38") -> "Ahmedabad"
            clean.startsWith("30") -> "Jaipur"
            clean.startsWith("22") -> "Lucknow"
            clean.startsWith("41") -> "Pune"
            else -> "All-India Regional Hub"
        }
        return PincodeCheckResult(
            isValid = true,
            city = city,
            pincode = clean,
            deliveryDays = if (clean.startsWith("40") || clean.startsWith("11") || clean.startsWith("56")) 1 else 2,
            codAvailable = true,
            message = "Delivery available to $city ($clean) in 1-2 days. Cash on Delivery eligible."
        )
    }
}

data class PincodeCheckResult(
    val isValid: Boolean,
    val city: String = "",
    val pincode: String = "",
    val deliveryDays: Int = 2,
    val codAvailable: Boolean = true,
    val message: String = ""
)
