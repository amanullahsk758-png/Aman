package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.local.DKartDatabase
import com.example.data.repository.BharatBazaarRepository
import com.example.ui.components.BottomNavBar
import com.example.ui.components.PincodeBottomSheet
import com.example.ui.components.TopSearchBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogSelectionScreen
import com.example.ui.screens.CategoryProductFormScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProductListScreen
import com.example.ui.screens.SellerDashboardScreen
import com.example.ui.screens.SellerRegistrationScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.DKartTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DKartDatabase.getInstance(applicationContext)
        val repository = BharatBazaarRepository(db)
        val viewModel = ShopViewModel(repository)

        setContent {
            DKartTheme {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: ShopViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val wishlistCount by viewModel.wishlistCount.collectAsState()
    val activePincode by viewModel.activePincode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showPincodeDialog by viewModel.showPincodeDialog.collectAsState()
    val pincodeCheckResult by viewModel.pincodeCheckResult.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val isTopTabScreen = currentScreen is Screen.Home ||
            currentScreen is Screen.Categories ||
            currentScreen is Screen.Wishlist ||
            currentScreen is Screen.Cart ||
            currentScreen is Screen.Account

    // Global back handling for secondary screens
    BackHandler(enabled = !isTopTabScreen) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (isTopTabScreen) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    TopSearchBar(
                        activePincode = activePincode,
                        searchQuery = searchQuery,
                        cartCount = cartCount,
                        currentLanguage = currentLanguage,
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onPincodeClick = { viewModel.setShowPincodeDialog(true) },
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onSearchSubmit = { query ->
                            if (query.isNotBlank()) {
                                viewModel.navigateTo(Screen.ProductList(query = query))
                            }
                        },
                        onCartClick = { viewModel.navigateToTab(Screen.Cart) },
                        onSellerHubClick = { viewModel.navigateTo(Screen.SellerDashboard) },
                        onAdminClick = { viewModel.navigateTo(Screen.AdminDashboard) }
                    )
                }
            }
        },
        bottomBar = {
            if (isTopTabScreen) {
                BottomNavBar(
                    currentScreen = currentScreen,
                    cartCount = cartCount,
                    wishlistCount = wishlistCount,
                    onTabSelected = { tab ->
                        viewModel.navigateToTab(tab)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> HomeScreen(viewModel = viewModel)
                is Screen.Categories -> CategoriesScreen(viewModel = viewModel)
                is Screen.Wishlist -> WishlistScreen(viewModel = viewModel)
                is Screen.Cart -> CartScreen(viewModel = viewModel)
                is Screen.Account -> AccountScreen(viewModel = viewModel)
                is Screen.ProductList -> ProductListScreen(
                    category = screen.category,
                    query = screen.query,
                    viewModel = viewModel
                )
                is Screen.ProductDetail -> ProductDetailScreen(
                    product = screen.product,
                    viewModel = viewModel
                )
                is Screen.Checkout -> CheckoutScreen(viewModel = viewModel)
                is Screen.OrderTracking -> OrderTrackingScreen(
                    orderId = screen.orderId,
                    viewModel = viewModel
                )
                is Screen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel)
                is Screen.SellerDashboard -> SellerDashboardScreen(viewModel = viewModel)
                is Screen.SellerRegistration -> SellerRegistrationScreen(viewModel = viewModel)
                is Screen.CatalogSelection -> CatalogSelectionScreen(
                    seller = screen.seller,
                    viewModel = viewModel
                )
                is Screen.CategoryProductForm -> CategoryProductFormScreen(
                    catalogItem = screen.catalogItem,
                    seller = screen.seller,
                    viewModel = viewModel
                )
            }
        }
    }

    // Pincode Bottom Sheet Dialog
    if (showPincodeDialog) {
        PincodeBottomSheet(
            currentPincode = activePincode,
            checkResult = pincodeCheckResult,
            onDismiss = { viewModel.setShowPincodeDialog(false) },
            onCheck = { code -> viewModel.checkPincode(code) }
        )
    }
}
