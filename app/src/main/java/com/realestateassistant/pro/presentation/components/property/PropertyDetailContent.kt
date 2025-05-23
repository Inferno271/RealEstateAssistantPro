package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realestateassistant.pro.domain.model.Property

@Composable
fun PropertyDetailContent(property: Property) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Галерея фотографий
        if (property.photos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            PhotoGallery(photos = property.photos)
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Заголовок и тип недвижимости
        PropertyHeader(property)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Цена и условия
        PropertyPriceInfo(property)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Адрес и карта
        PropertyAddressSection(property)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Секция с вкладками для характеристик и деталей
        PropertyTabSection(property)
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Контактная информация
        PropertyContactInfo(property)
        Spacer(modifier = Modifier.height(32.dp))
    }
} 