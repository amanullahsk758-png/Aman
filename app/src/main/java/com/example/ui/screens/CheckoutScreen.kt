package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Address
import com.example.ui.components.formatInr
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val cartItems by viewModel.cartItems.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val defaultAddress by viewModel.defaultAddress.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponDiscount by viewModel.couponDiscount.collectAsState()

    var selectedAddressId by remember(defaultAddress, addresses) {
        mutableStateOf(defaultAddress?.id ?: addresses.firstOrNull()?.id ?: 0L)
    }

    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    var showAddAddressDialog by remember { mutableStateOf(false) }

    // Add address form state
    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newPincode by remember { mutableStateOf("") }
    var newAddressLine by remember { mutableStateOf("") }
    var newCity by remember { mutableStateOf("") }
    var newState by remember { mutableStateOf("") }

    val subtotal = cartItems.sumOf { (it.product?.price ?: 0.0) * it.cartItem.quantity }
    val totalMrp = cartItems.sumOf { (it.product?.mrp ?: 0.0) * it.cartItem.quantity }
    val deliveryFee = if (subtotal > 500.0) 0.0 else 40.0
    val totalAmount = (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0.0)

    val currentSelectedAddress = addresses.find { it.id == selectedAddressId } ?: defaultAddress ?: addresses.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order Checkout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FlipkartBlue)
            )
        },
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
                            text = "View Price Breakdown",
                            fontSize = 11.sp,
                            color = FlipkartBlue
                        )
                    }

                    Button(
                        onClick = {
                            val addr = currentSelectedAddress ?: Address(
                                fullName = "Customer",
                                phoneNumber = "9876543210",
                                pincode = "400001",
                                addressLine = "Street 1",
                                city = "Mumbai",
                                state = "Maharashtra",
                                isDefault = true
                            )
                            viewModel.placeOrder(
                                cartItems = cartItems,
                                deliveryAddress = addr,
                                paymentMethod = selectedPaymentMethod,
                                onSuccess = { orderId ->
                                    viewModel.navigateTo(Screen.OrderTracking(orderId))
                                }
                            )
                        },
                        modifier = Modifier
                            .height(46.dp)
                            .testTag("confirm_order_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FlipkartOrange),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "CONFIRM & PAY", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Delivery Address Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(FlipkartBlue, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DELIVERY ADDRESS",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }

                            TextButton(onClick = { showAddAddressDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Add New", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FlipkartBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        addresses.forEach { addr ->
                            val isSelected = addr.id == selectedAddressId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color(0xFFF0F5FF) else Color.Transparent)
                                    .clickable { selectedAddressId = addr.id }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedAddressId = addr.id },
                                    colors = RadioButtonDefaults.colors(selectedColor = FlipkartBlue)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = addr.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFEEEEEE), RoundedCornerShape(2.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(text = addr.addressType, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                        }
                                    }
                                    Text(
                                        text = "${addr.addressLine}, ${addr.city}, ${addr.state} - ${addr.pincode}",
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Text(
                                        text = "Phone: ${addr.phoneNumber}",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Order Items Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(FlipkartBlue, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("2", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ORDER ITEMS (${cartItems.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        cartItems.forEach { item ->
                            val p = item.product
                            if (p != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = p.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                        Text(text = "Qty: ${item.cartItem.quantity}  •  ${p.brand}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Text(
                                        text = formatInr(p.price * item.cartItem.quantity),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Payment Method Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(FlipkartBlue, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("3", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PAYMENT OPTIONS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        listOf(
                            Triple("COD", "Cash on Delivery", "Pay cash/UPI at delivery doorstep"),
                            Triple("UPI", "UPI (Google Pay, PhonePe, Paytm)", "Fast, zero transaction charge"),
                            Triple("CARD", "Credit / Debit Card (Razorpay Test)", "Visa, MasterCard, RuPay supported"),
                            Triple("NET_BANKING", "Net Banking", "All major Indian banks supported")
                        ).forEach { (key, title, subtitle) ->
                            val isSel = selectedPaymentMethod == key
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Color(0xFFF0F5FF) else Color.Transparent)
                                    .clickable { selectedPaymentMethod = key }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSel,
                                    onClick = { selectedPaymentMethod = key },
                                    colors = RadioButtonDefaults.colors(selectedColor = FlipkartBlue)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Price Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("PRICE BREAKDOWN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total MRP", fontSize = 12.sp)
                            Text(formatInr(totalMrp), fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount on MRP", fontSize = 12.sp)
                            Text("- ${formatInr(totalMrp - subtotal)}", fontSize = 12.sp, color = FlipkartGreen, fontWeight = FontWeight.Bold)
                        }
                        if (couponDiscount > 0) {
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Coupon ($appliedCoupon)", fontSize = 12.sp)
                                Text("- ${formatInr(couponDiscount)}", fontSize = 12.sp, color = FlipkartGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Fee", fontSize = 12.sp)
                            Text(if (deliveryFee == 0.0) "FREE" else formatInr(deliveryFee), fontSize = 12.sp, color = if (deliveryFee == 0.0) FlipkartGreen else TextPrimary)
                        }
                        Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFEEEEEE))
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Payable", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(formatInr(totalAmount), fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("Mobile Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newPincode, onValueChange = { newPincode = it }, label = { Text("PIN Code (6 digits)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = newAddressLine, onValueChange = { newAddressLine = it }, label = { Text("House/Flat No., Street, Area") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = newCity, onValueChange = { newCity = it }, label = { Text("City") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = newState, onValueChange = { newState = it }, label = { Text("State") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && newPincode.isNotBlank()) {
                            viewModel.saveAddress(
                                Address(
                                    fullName = newName,
                                    phoneNumber = newPhone,
                                    pincode = newPincode,
                                    addressLine = newAddressLine,
                                    city = newCity,
                                    state = newState,
                                    isDefault = false
                                )
                            )
                            showAddAddressDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
