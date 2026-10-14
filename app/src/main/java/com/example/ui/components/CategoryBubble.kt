package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LaptopChromebook
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RollerSkating
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartOrange
import com.example.ui.theme.FlipkartYellow

fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "phone_android" -> Icons.Default.PhoneAndroid
        "checkroom" -> Icons.Default.Checkroom
        "woman" -> Icons.Default.Woman
        "laptop_chromebook" -> Icons.Default.LaptopChromebook
        "kitchen" -> Icons.Default.Kitchen
        "spa" -> Icons.Default.Spa
        "shopping_basket" -> Icons.Default.ShoppingBasket
        "tv" -> Icons.Default.Tv
        "fitness_center" -> Icons.Default.FitnessCenter
        "roller_skating" -> Icons.Default.RollerSkating
        else -> Icons.Default.ShoppingBag
    }
}

@Composable
fun CategoryBubble(
    category: Category,
    isSelected: Boolean = false,
    onClick: (Category) -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick(category) }
            .testTag("category_bubble_${category.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) FlipkartBlue
                    else Color(0xFFF0F4FF)
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) FlipkartYellow else Color(0xFFD6E4FF),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(category.iconName),
                contentDescription = category.name,
                tint = if (isSelected) Color.White else FlipkartBlue,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = category.name,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) FlipkartBlue else Color(0xFF212121),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (category.badge.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .background(Color(0xFFFFF3E0), RoundedCornerShape(2.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = category.badge,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlipkartOrange,
                    maxLines = 1
                )
            }
        }
    }
}
