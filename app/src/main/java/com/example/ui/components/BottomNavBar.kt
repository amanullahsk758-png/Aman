package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.ui.viewmodel.Screen

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    cartCount: Int,
    wishlistCount: Int,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("bottom_nav_bar"),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val isHome = currentScreen is Screen.Home
        NavigationBarItem(
            selected = isHome,
            onClick = { onTabSelected(Screen.Home) },
            icon = {
                Icon(
                    imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = if (isHome) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FlipkartBlue,
                selectedTextColor = FlipkartBlue,
                indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        val isCategories = currentScreen is Screen.Categories
        NavigationBarItem(
            selected = isCategories,
            onClick = { onTabSelected(Screen.Categories) },
            icon = {
                Icon(
                    imageVector = if (isCategories) Icons.Filled.Category else Icons.Outlined.Category,
                    contentDescription = "Categories"
                )
            },
            label = {
                Text(
                    text = "Categories",
                    fontSize = 11.sp,
                    fontWeight = if (isCategories) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FlipkartBlue,
                selectedTextColor = FlipkartBlue,
                indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_categories")
        )

        val isWishlist = currentScreen is Screen.Wishlist
        NavigationBarItem(
            selected = isWishlist,
            onClick = { onTabSelected(Screen.Wishlist) },
            icon = {
                BadgedBox(
                    badge = {
                        if (wishlistCount > 0) {
                            Badge(containerColor = FlipkartBlue, contentColor = Color.White) {
                                Text(wishlistCount.toString(), fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist"
                    )
                }
            },
            label = {
                Text(
                    text = "Wishlist",
                    fontSize = 11.sp,
                    fontWeight = if (isWishlist) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FlipkartBlue,
                selectedTextColor = FlipkartBlue,
                indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_wishlist")
        )

        val isCart = currentScreen is Screen.Cart
        NavigationBarItem(
            selected = isCart,
            onClick = { onTabSelected(Screen.Cart) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = FlipkartYellow, contentColor = Color.Black) {
                                Text(cartCount.toString(), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isCart) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            },
            label = {
                Text(
                    text = "Cart",
                    fontSize = 11.sp,
                    fontWeight = if (isCart) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FlipkartBlue,
                selectedTextColor = FlipkartBlue,
                indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_cart")
        )

        val isAccount = currentScreen is Screen.Account
        NavigationBarItem(
            selected = isAccount,
            onClick = { onTabSelected(Screen.Account) },
            icon = {
                Icon(
                    imageVector = if (isAccount) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Account"
                )
            },
            label = {
                Text(
                    text = "Account",
                    fontSize = 11.sp,
                    fontWeight = if (isAccount) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FlipkartBlue,
                selectedTextColor = FlipkartBlue,
                indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_item_account")
        )
    }
}
