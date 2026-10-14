package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PincodeCheckResult
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PincodeBottomSheet(
    currentPincode: String,
    checkResult: PincodeCheckResult?,
    onDismiss: () -> Unit,
    onCheck: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var inputPincode by remember { mutableStateOf(currentPincode) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("pincode_bottom_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = FlipkartBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Select Delivery Location",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "Enter your 6-digit Indian PIN code to view product availability and accurate delivery times.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputPincode,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            inputPincode = it
                        }
                    },
                    label = { Text("PIN Code (e.g. 400001, 110001)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pincode_input_field")
                )
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { onCheck(inputPincode) },
                    modifier = Modifier
                        .height(56.dp)
                        .testTag("check_pincode_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Check", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Result display
            if (checkResult != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (checkResult.isValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = if (checkResult.isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (checkResult.isValid) FlipkartGreen else Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (checkResult.isValid) "Deliverable to ${checkResult.city}" else "Unavailable",
                                fontWeight = FontWeight.Bold,
                                color = if (checkResult.isValid) FlipkartGreen else Color.Red,
                                fontSize = 14.sp
                            )
                            Text(
                                text = checkResult.message,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Quick Metro Pincodes
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Popular Metro Hubs",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = TextSecondary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "400001" to "Mumbai",
                    "110001" to "Delhi",
                    "560001" to "Bengaluru",
                    "700001" to "Kolkata"
                ).forEach { (code, city) ->
                    Button(
                        onClick = {
                            inputPincode = code
                            onCheck(code)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1F3F6),
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = city, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = code, fontSize = 9.sp, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
