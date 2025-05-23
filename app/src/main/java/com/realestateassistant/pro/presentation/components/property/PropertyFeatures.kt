package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property

@Composable
fun PropertyFeatures(property: Property) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeatureItem(
                icon = Icons.Outlined.Apartment,
                value = if (property.totalFloors > 0) "${property.floor}/${property.totalFloors}" else "${property.floor}",
                label = "Этаж"
            )
            
            HorizontalDivider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )
            
            FeatureItem(
                icon = Icons.Outlined.SquareFoot, 
                value = "${property.area} м²",
                label = "Площадь"
            )
            
            HorizontalDivider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )
            
            FeatureItem(
                icon = Icons.Outlined.Bed,
                value = "${property.roomsCount}",
                label = "Комнаты"
            )
        }
    }
} 