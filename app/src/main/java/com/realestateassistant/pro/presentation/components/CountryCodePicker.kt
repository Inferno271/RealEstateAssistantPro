package com.realestateassistant.pro.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Компонент для выбора кода страны
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodePicker(
    selectedCountryCode: String,
    onCountryCodeSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Отображение выбранного кода страны
    OutlinedCard(
        modifier = modifier
            .wrapContentSize()
            .clickable { expanded = true },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCountryCode,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Выбрать код страны",
                modifier = Modifier.size(24.dp)
            )
        }
    }
    
    // Диалог выбора кода страны
    if (expanded) {
        CountryCodeDialog(
            onDismissRequest = { expanded = false },
            onCountrySelected = { code, name ->
                onCountryCodeSelected(code, name)
                expanded = false
            },
            initialCode = selectedCountryCode
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodeDialog(
    onDismissRequest: () -> Unit,
    onCountrySelected: (String, String) -> Unit,
    initialCode: String
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            popularCountries + otherCountries
        } else {
            val query = searchQuery.lowercase()
            (popularCountries + otherCountries).filter {
                it.name.lowercase().contains(query) ||
                it.code.lowercase().contains(query) ||
                it.dialCode.lowercase().contains(query)
            }
        }
    }
    
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Выберите код страны",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Поле поиска
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск страны или кода") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск"
                        )
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Список стран
                if (filteredCountries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Нет результатов")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredCountries) { country ->
                            CountryItem(
                                country = country,
                                isSelected = country.dialCode == initialCode,
                                onClick = { onCountrySelected(country.dialCode, country.name) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Кнопка закрытия
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Закрыть")
                }
            }
        }
    }
}

@Composable
fun CountryItem(
    country: Country,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${country.flag} ${country.name}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        
        Text(
            text = country.dialCode,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Модель данных для страны
data class Country(
    val code: String,
    val name: String,
    val dialCode: String,
    val flag: String
)

// Список популярных стран
val popularCountries = listOf(
    Country("RU", "Россия", "+7", "🇷🇺"),
    Country("KZ", "Казахстан", "+7", "🇰🇿"),
    Country("BY", "Беларусь", "+375", "🇧🇾"),
    Country("UA", "Украина", "+380", "🇺🇦"),
    Country("US", "США", "+1", "🇺🇸")
)

// Список остальных стран
val otherCountries = listOf(
    Country("AZ", "Азербайджан", "+994", "🇦🇿"),
    Country("AM", "Армения", "+374", "🇦🇲"),
    Country("GB", "Великобритания", "+44", "🇬🇧"),
    Country("DE", "Германия", "+49", "🇩🇪"),
    Country("GE", "Грузия", "+995", "🇬🇪"),
    Country("IL", "Израиль", "+972", "🇮🇱"),
    Country("IT", "Италия", "+39", "🇮🇹"),
    Country("KG", "Киргизия", "+996", "🇰🇬"),
    Country("CN", "Китай", "+86", "🇨🇳"),
    Country("LV", "Латвия", "+371", "🇱🇻"),
    Country("LT", "Литва", "+370", "🇱🇹"),
    Country("MD", "Молдова", "+373", "🇲🇩"),
    Country("PL", "Польша", "+48", "🇵🇱"),
    Country("TJ", "Таджикистан", "+992", "🇹🇯"),
    Country("TM", "Туркменистан", "+993", "🇹🇲"),
    Country("UZ", "Узбекистан", "+998", "🇺🇿"),
    Country("FR", "Франция", "+33", "🇫🇷"),
    Country("EE", "Эстония", "+372", "🇪🇪")
) 