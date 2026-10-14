package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItemWithProduct
import com.example.ui.components.formatInr
import com.example.ui.theme.AssuredBlue
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val activePincode by viewModel.activePincode.collectAsState()
    val defaultAddress by viewModel.defaultAddress.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponDiscount by viewModel.couponDiscount.collectAsState()

    var couponInput by remember { mutableStateOf("") }

    val totalMrp = cartItems.sumOf { (it.product?.mrp ?: 0.0) * it.cartItem.quantity }
    val subtotal = cartItems.sumOf { (it.product?.price ?: 0.0) * it.cartItem.quantity }
    val productDiscount = (totalMrp - subtotal).coerceAtLeast(0.0)
    val deliveryFee = if (subtotal > 500.0 || cartItems.isEmpty()) 0.0 else 40.0
    val totalAmount = (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0.0)
    val totalSavings = productDiscount + couponDiscount

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundNeutral)
                .testTag("empty_cart_view"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Your Shopping Cart is Empty",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Explore top offers, deals and add items to your cart",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.navigateToTab(Screen.Home) },
                    colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Shop Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = formatInr(totalAmount),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "View Price Details",
                                fontSize = 11.sp,
                                color = FlipkartBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.Checkout) },
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("place_order_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = FlipkartOrange),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(text = "PLACE ORDER", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BackgroundNeutral),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // 1. Delivery Address Bar
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(0.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = FlipkartBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Deliver to: ${defaultAddress?.fullName ?: "Customer"}, $activePincode",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = defaultAddress?.addressLine ?: "Default Delivery Address",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { viewModel.setShowPincodeDialog(true) },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Change", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FlipkartBlue)
                            }
                        }
                    }
                }

                // 2. Cart Items
                items(cartItems) { item ->
                    val prod = item.product
                    if (prod != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cart_item_${item.cartItem.cartItemId}"),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row {
                                    // Product Image
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFF9F9F9))
                                    ) {
                                        AsyncImage(
                                            model = prod.imageUrl,
                                            contentDescription = prod.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Product Info
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prod.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (item.cartItem.selectedSize.isNotBlank() || item.cartItem.selectedColor.isNotBlank()) {
                                            Text(
                                                text = "Variant: ${item.cartItem.selectedSize} ${item.cartItem.selectedColor}".trim(),
                                                fontSize = 11.sp,
                                                color = TextSecondary,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = "Seller: ${prod.sellerName}",
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Price
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = formatInr(prod.price * item.cartItem.quantity),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            if (prod.mrp > prod.price) {
                                                Text(
                                                    text = formatInr(prod.mrp * item.cartItem.quantity),
                                                    fontSize = 11.sp,
                                                    color = TextSecondary,
                                                    textDecoration = TextDecoration.LineThrough
                                                )
                                                Text(
                                                    text = "${prod.discountPercent}% Off",
                                                    fontSize = 11.sp,
                                                    color = FlipkartGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = Color(0xFFF0F0F0))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Quantity Stepper & Remove Action
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .border(1.dp, Color(0xFFD6D6D6), RoundedCornerShape(4.dp))
                                            .padding(2.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                viewModel.updateCartQuantity(
                                                    item.cartItem.cartItemId,
                                                    item.cartItem.quantity - 1
                                                )
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (item.cartItem.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                                                contentDescription = "Decrease",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Text(
                                            text = item.cartItem.quantity.toString(),
                                            modifier = Modifier.padding(horizontal = 8.dp),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )

                                        IconButton(
                                            onClick = {
                                                viewModel.updateCartQuantity(
                                                    item.cartItem.cartItemId,
                                                    item.cartItem.quantity + 1
                                                )
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    TextButton(
                                        onClick = { viewModel.removeFromCart(item.cartItem.cartItemId) }
                                    ) {
                                        Text("Remove", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Coupon Promo Code Section
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("APPLY PROMO COUPON", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (appliedCoupon != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FlipkartGreen, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text("Coupon '$appliedCoupon' Applied!", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FlipkartGreen)
                                            Text("You saved ${formatInr(couponDiscount)}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                    TextButton(onClick = { viewModel.removeCoupon() }) {
                                        Text("Remove", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { couponInput = it.uppercase() },
                                        placeholder = { Text("Enter coupon code", fontSize = 12.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (couponInput.isNotBlank()) {
                                                viewModel.applyCoupon(couponInput, subtotal)
                                                couponInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = "Try: BHARAT500 (Flat ₹500 off) or WELCOME10 (10% off)",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }

                // 4. Price Breakdown Details Card
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "PRICE DETAILS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Price (${cartItems.size} items)", fontSize = 13.sp, color = TextPrimary)
                                Text(formatInr(totalMrp), fontSize = 13.sp, color = TextPrimary)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Discount on MRP", fontSize = 13.sp, color = TextPrimary)
                                Text("- ${formatInr(productDiscount)}", fontSize = 13.sp, color = FlipkartGreen, fontWeight = FontWeight.Bold)
                            }

                            if (couponDiscount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Coupon Discount ($appliedCoupon)", fontSize = 13.sp, color = TextPrimary)
                                    Text("- ${formatInr(couponDiscount)}", fontSize = 13.sp, color = FlipkartGreen, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Delivery Charges", fontSize = 13.sp, color = TextPrimary)
                                Text(
                                    if (deliveryFee == 0.0) "FREE" else formatInr(deliveryFee),
                                    fontSize = 13.sp,
                                    color = if (deliveryFee == 0.0) FlipkartGreen else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(formatInr(totalAmount), fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }

                            if (totalSavings > 0) {
                                Text(
                                    text = "You will save ${formatInr(totalSavings)} on this order",
                                    color = FlipkartGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 5. Safe & Secure Payment guarantee
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Safe and Secure Payments • 100% Authentic Products",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
