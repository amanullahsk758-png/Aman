package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Address
import com.example.data.model.CartItemWithProduct
import com.example.data.model.Category
import com.example.data.model.OrderWithItems
import com.example.data.model.Product
import com.example.data.model.Review
import com.example.data.model.WishlistItemWithProduct
import com.example.data.repository.BharatBazaarRepository
import com.example.data.repository.PincodeCheckResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    RELEVANCE("Relevance"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING("Customer Rating"),
    DISCOUNT("Discount: High to Low")
}

sealed class Screen {
    object Home : Screen()
    object Categories : Screen()
    data class ProductList(val category: Category? = null, val query: String? = null) : Screen()
    data class ProductDetail(val product: Product) : Screen()
    object Wishlist : Screen()
    object Cart : Screen()
    object Checkout : Screen()
    data class OrderTracking(val orderId: String) : Screen()
    object Account : Screen()
    object AdminDashboard : Screen()
}

class ShopViewModel(private val repository: BharatBazaarRepository) : ViewModel() {

    // Current navigation state (MVVM stack)
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _screenStack
        .combine(MutableStateFlow(Unit)) { stack, _ -> stack.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.Home)

    fun navigateTo(screen: Screen) {
        val current = _screenStack.value
        _screenStack.value = current + screen
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        return if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    fun navigateToTab(screen: Screen) {
        _screenStack.value = listOf(screen)
    }

    // Active Delivery Pincode
    private val _activePincode = MutableStateFlow("400001")
    val activePincode: StateFlow<String> = _activePincode.asStateFlow()

    private val _pincodeCheckResult = MutableStateFlow<PincodeCheckResult?>(null)
    val pincodeCheckResult: StateFlow<PincodeCheckResult?> = _pincodeCheckResult.asStateFlow()

    private val _showPincodeDialog = MutableStateFlow(false)
    val showPincodeDialog: StateFlow<Boolean> = _showPincodeDialog.asStateFlow()

    fun setShowPincodeDialog(show: Boolean) {
        _showPincodeDialog.value = show
    }

    fun checkPincode(pincode: String) {
        val result = repository.checkPincodeDelivery(pincode)
        _pincodeCheckResult.value = result
        if (result.isValid) {
            _activePincode.value = pincode
        }
    }

    // Snackbar notifications
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    fun showToast(message: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }

    // Categories
    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Products
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dealsOfTheDay: StateFlow<List<Product>> = repository.dealsOfTheDay
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingProducts: StateFlow<List<Product>> = repository.trendingProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bestSellers: StateFlow<List<Product>> = repository.bestSellers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart
    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartWithProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = repository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addToCart(product: Product, size: String = "", color: String = "", quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product.id, size, color, quantity)
            showToast("Added '${product.title.take(24)}...' to Cart")
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeFromCart(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
            showToast("Item removed from Cart")
        }
    }

    // Wishlist
    val wishlistItems: StateFlow<List<WishlistItemWithProduct>> = repository.wishlistWithProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistCount: StateFlow<Int> = repository.wishlistCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            repository.toggleWishlist(product.id)
            showToast("Updated Wishlist")
        }
    }

    fun isWishlisted(productId: String) = repository.isWishlisted(productId)

    // Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _selectedSort = MutableStateFlow(SortOption.RELEVANCE)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    fun setSortOption(option: SortOption) {
        _selectedSort.value = option
    }

    // Applied Coupon
    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    private val _couponDiscount = MutableStateFlow(0.0)
    val couponDiscount: StateFlow<Double> = _couponDiscount.asStateFlow()

    val availableCoupons = repository.availableCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun applyCoupon(code: String, subtotal: Double) {
        viewModelScope.launch {
            val (isValid, msg) = repository.validateCoupon(code, subtotal)
            if (isValid) {
                _appliedCoupon.value = code.uppercase()
                _couponDiscount.value = repository.calculateDiscount(code, subtotal)
                showToast("Coupon applied: $code!")
            } else {
                showToast(msg)
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponDiscount.value = 0.0
        showToast("Coupon removed")
    }

    // Addresses
    val addresses: StateFlow<List<Address>> = repository.allAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val defaultAddress: StateFlow<Address?> = repository.defaultAddress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveAddress(address: Address) {
        viewModelScope.launch {
            repository.addAddress(address)
            showToast("Address saved successfully")
        }
    }

    fun setDefaultAddress(id: Long) {
        viewModelScope.launch {
            repository.setDefaultAddress(id)
            showToast("Default address updated")
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteAddress(id)
            showToast("Address removed")
        }
    }

    // Orders
    val orders: StateFlow<List<OrderWithItems>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getOrder(orderId: String) = repository.getOrderById(orderId)

    fun placeOrder(
        cartItems: List<CartItemWithProduct>,
        deliveryAddress: Address,
        paymentMethod: String,
        onSuccess: (orderId: String) -> Unit
    ) {
        viewModelScope.launch {
            val orderId = repository.placeOrder(
                cartItems = cartItems,
                deliveryAddress = deliveryAddress,
                paymentMethod = paymentMethod,
                couponCode = _appliedCoupon.value
            )
            _appliedCoupon.value = null
            _couponDiscount.value = 0.0
            showToast("Order placed successfully! Order ID: $orderId")
            onSuccess(orderId)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            showToast("Order $orderId updated to $newStatus")
        }
    }

    // Admin Operations
    fun saveProduct(product: Product) {
        viewModelScope.launch {
            repository.saveProduct(product)
            showToast("Product '${product.title.take(20)}' saved")
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            showToast("Product deleted")
        }
    }

    // Reviews
    fun getProductReviews(productId: String) = repository.getProductReviews(productId)

    fun submitReview(productId: String, rating: Int, headline: String, comment: String, userName: String) {
        viewModelScope.launch {
            val review = Review(
                productId = productId,
                userName = if (userName.isBlank()) "Verified Buyer" else userName,
                rating = rating,
                headline = headline,
                comment = comment,
                dateText = "Today",
                isVerified = true
            )
            repository.addReview(review)
            showToast("Thank you for your rating & review!")
        }
    }
}
