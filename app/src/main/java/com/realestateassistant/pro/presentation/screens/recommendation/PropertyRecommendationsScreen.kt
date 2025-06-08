package com.realestateassistant.pro.presentation.screens.recommendation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.PropertyFilter
import com.realestateassistant.pro.presentation.components.common.ErrorView
import com.realestateassistant.pro.presentation.components.common.LoadingView
import com.realestateassistant.pro.presentation.components.property.PropertyCard
import com.realestateassistant.pro.presentation.components.property.PropertyFilterDialog
import com.realestateassistant.pro.presentation.model.PropertyRecommendation
import com.realestateassistant.pro.presentation.viewmodel.PropertyRecommendationsViewModel

/**
 * Экран рекомендаций объектов недвижимости для клиента.
 *
 * @param clientId ID клиента, для которого отображаются рекомендации
 * @param onNavigateBack Функция для возврата на предыдущий экран
 * @param onNavigateToPropertyDetail Функция для перехода к деталям объекта недвижимости
 * @param viewModel ViewModel для управления рекомендациями
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRecommendationsScreen(
    clientId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    viewModel: PropertyRecommendationsViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val client by viewModel.client.collectAsState()
    val recommendations by viewModel.filteredRecommendations.collectAsState()
    val filter by viewModel.filter.collectAsState()
    
    var showFilterDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Загрузка рекомендаций при первом отображении экрана
    LaunchedEffect(clientId) {
        viewModel.loadRecommendationsForClient(clientId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Рекомендации для клиента",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Фильтры"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> LoadingView(message = "Подбираем рекомендации...")
                error != null -> ErrorView(
                    errorMessage = error ?: "Неизвестная ошибка",
                    onRetry = { viewModel.loadRecommendationsForClient(clientId) }
                )
                recommendations.isEmpty() -> EmptyRecommendationsView()
                else -> RecommendationsContent(
                    client = client,
                    recommendations = recommendations,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { 
                        searchQuery = it
                        viewModel.updateSearchQuery(it)
                    },
                    onPropertyClick = onNavigateToPropertyDetail
                )
            }
        }
    }
    
    // Диалог фильтров
    if (showFilterDialog) {
        PropertyFilterDialog(
            currentFilter = filter,
            onFilterApplied = { newFilter ->
                viewModel.updateFilter(newFilter)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

/**
 * Содержимое экрана рекомендаций.
 */
@Composable
fun RecommendationsContent(
    client: Client?,
    recommendations: List<PropertyRecommendation>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPropertyClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Информация о клиенте
        client?.let {
            ClientInfoCard(client = it)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Поисковая строка
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск по адресу, району...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Поиск"
                )
            },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Счетчик результатов
        Text(
            text = "Найдено рекомендаций: ${recommendations.size}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Список рекомендаций
        LazyColumn {
            items(recommendations) { recommendation ->
                RecommendationItem(
                    recommendation = recommendation,
                    onClick = { onPropertyClick(recommendation.property.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Карточка с информацией о клиенте.
 */
@Composable
fun ClientInfoCard(client: Client) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = client.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Тип аренды
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Тип аренды: ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = when (client.rentalType.lowercase()) {
                        "длительная" -> "Длительная"
                        "посуточная" -> "Посуточная"
                        "оба_варианта" -> "Длительная и посуточная"
                        else -> client.rentalType
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Бюджет
            if (client.rentalType.lowercase() == "длительная" || client.rentalType.lowercase() == "оба_варианта") {
                if (client.longTermBudgetMin != null || client.longTermBudgetMax != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Бюджет (длит.): ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = when {
                                client.longTermBudgetMin != null && client.longTermBudgetMax != null ->
                                    "${client.longTermBudgetMin.toInt()} - ${client.longTermBudgetMax.toInt()} ₽/мес."
                                client.longTermBudgetMin != null ->
                                    "от ${client.longTermBudgetMin.toInt()} ₽/мес."
                                client.longTermBudgetMax != null ->
                                    "до ${client.longTermBudgetMax.toInt()} ₽/мес."
                                else -> "не указан"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            if (client.rentalType.lowercase() == "посуточная" || client.rentalType.lowercase() == "оба_варианта") {
                if (client.shortTermBudgetMin != null || client.shortTermBudgetMax != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Бюджет (посут.): ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = when {
                                client.shortTermBudgetMin != null && client.shortTermBudgetMax != null ->
                                    "${client.shortTermBudgetMin.toInt()} - ${client.shortTermBudgetMax.toInt()} ₽/сутки"
                                client.shortTermBudgetMin != null ->
                                    "от ${client.shortTermBudgetMin.toInt()} ₽/сутки"
                                client.shortTermBudgetMax != null ->
                                    "до ${client.shortTermBudgetMax.toInt()} ₽/сутки"
                                else -> "не указан"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // Желаемый тип недвижимости
            if (!client.desiredPropertyType.isNullOrEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Тип недвижимости: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = client.desiredPropertyType,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Количество комнат
            if (client.desiredRoomsCount != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Количество комнат: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = client.desiredRoomsCount.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Район
            if (!client.preferredDistrict.isNullOrEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Район: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = client.preferredDistrict,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Элемент списка рекомендаций.
 */
@Composable
fun RecommendationItem(
    recommendation: PropertyRecommendation,
    onClick: () -> Unit
) {
    val property = recommendation.property
    val matchPercent = recommendation.matchPercent
    val matchDescription = recommendation.matchDescription
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Индикатор соответствия
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(getMatchColor(matchPercent))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$matchDescription (${matchPercent}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Карточка объекта
            PropertyCard(
                property = property,
                onClick = onClick
            )
        }
    }
}

/**
 * Возвращает цвет для индикатора соответствия.
 */
@Composable
fun getMatchColor(matchPercent: Int): Color {
    return when {
        matchPercent >= 90 -> MaterialTheme.colorScheme.primary
        matchPercent >= 80 -> MaterialTheme.colorScheme.secondary
        matchPercent >= 70 -> MaterialTheme.colorScheme.tertiary
        matchPercent >= 60 -> Color(0xFF4CAF50) // Green
        matchPercent >= 50 -> Color(0xFF8BC34A) // Light Green
        matchPercent >= 40 -> Color(0xFFCDDC39) // Lime
        matchPercent >= 30 -> Color(0xFFFFC107) // Amber
        else -> Color(0xFFFF9800) // Orange
    }
}

/**
 * Отображается, когда список рекомендаций пуст.
 */
@Composable
fun EmptyRecommendationsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Рекомендации не найдены",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "К сожалению, мы не смогли найти подходящие объекты недвижимости для этого клиента. " +
                   "Попробуйте изменить критерии поиска или добавить новые объекты.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
} 