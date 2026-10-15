package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Seller
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.DKartBlue
import com.example.ui.theme.DKartDarkBlue
import com.example.ui.theme.DKartGreen
import com.example.ui.theme.DKartYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerRegistrationScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    var fullName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var bankAccount by remember { mutableStateOf("") }
    var bankIfsc by remember { mutableStateOf("") }
    var gstin by remember { mutableStateOf("") }
    var pan by remember { mutableStateOf("") }
    var agreedToPolicy by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register as DKart Seller", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DKartBlue)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundNeutral)
                .testTag("seller_registration_screen"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DKartDarkBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = DKartYellow, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Sell to Millions on DKart", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Lowest commission rates • 24x7 Seller Support", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Section 1: Business & Contact Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = DKartBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1. BUSINESS & CONTACT DETAILS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Proprietor / Full Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = businessName, onValueChange = { businessName = it }, label = { Text("Registered Business / Firm Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = displayName, onValueChange = { displayName = it }, label = { Text("Store Display Name (Shown to buyers) *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number *") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Business Email *") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = pickupAddress, onValueChange = { pickupAddress = it }, label = { Text("Warehouse / Pickup Address (with PIN) *") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // Section 2: Payout & Tax Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = DKartBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("2. BANK ACCOUNT & TAX DETAILS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = bankAccount, onValueChange = { bankAccount = it }, label = { Text("Bank Account Number") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = bankIfsc, onValueChange = { bankIfsc = it }, label = { Text("IFSC Code") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = gstin, onValueChange = { gstin = it.uppercase() }, label = { Text("GSTIN Number") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = pan, onValueChange = { pan = it.uppercase() }, label = { Text("PAN Number") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Agreement checkbox
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = agreedToPolicy, onCheckedChange = { agreedToPolicy = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "I agree to DKart Marketplace Seller Policies, fee schedule & terms of service.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (fullName.isNotBlank() && businessName.isNotBlank() && phone.isNotBlank()) {
                            val newSeller = Seller(
                                id = "seller_" + System.currentTimeMillis().toString().takeLast(6),
                                fullName = fullName,
                                businessName = businessName,
                                displayName = displayName.ifBlank { businessName },
                                email = email,
                                phone = phone,
                                pickupAddress = pickupAddress,
                                bankAccountNumber = bankAccount,
                                bankIfsc = bankIfsc,
                                gstin = gstin,
                                pan = pan,
                                status = "APPROVED", // Auto-approved for frictionless demo & testing
                                rating = 5.0f,
                                totalOrders = 0,
                                commissionPercent = 10.0
                            )
                            viewModel.registerSeller(newSeller) {
                                viewModel.switchActiveSeller(newSeller)
                                viewModel.navigateTo(Screen.SellerDashboard)
                            }
                        } else {
                            viewModel.showToast("Please fill in full name, business name, and mobile number!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_seller_reg_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = DKartBlue),
                    shape = RoundedCornerShape(6.dp),
                    enabled = agreedToPolicy
                ) {
                    Text("REGISTER & OPEN SELLER HUB", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
