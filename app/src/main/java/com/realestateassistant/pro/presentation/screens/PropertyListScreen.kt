package com.realestateassistant.pro.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.R
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.RentalFilter
import com.realestateassistant.pro.presentation.viewmodel.PropertyViewModel
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.getRoomsText
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.formatPrice
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.formatPhoneForDisplay
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.formatUpdatedDate
import com.realestateassistant.pro.presentation.utils.PropertyDisplayUtils.getLevelsText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PropertyListScreen(
    onNavigateToAddProperty: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    propertyViewModel: PropertyViewModel = hiltViewModel()
) {
    val filteredProperties by propertyViewModel.filteredProperties.collectAsState()
    val currentFilter by propertyViewModel.currentFilter.collectAsState()
    
    // Создаем состояние пейджера, которое будет синхронизировано с текущим фильтром
    val pagerState = rememberPagerState(
        initialPage = currentFilter.ordinal,
        pageCount = { RentalFilter.values().size }
    )

    // Эффект для синхронизации пейджера с фильтром только при изменении currentFilter
    LaunchedEffect(currentFilter) {
        if (pagerState.currentPage != currentFilter.ordinal) {
            pagerState.animateScrollToPage(currentFilter.ordinal)
        }
    }

    // Эффект для обновления фильтра при смене страницы пейджера
    LaunchedEffect(pagerState.currentPage) {
        val pageFilter = RentalFilter.values()[pagerState.currentPage]
        if (currentFilter != pageFilter) {
            propertyViewModel.setFilter(pageFilter)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        Text(
                            "Объекты недвижимости",
                            style = MaterialTheme.typography.titleLarge
                        ) 
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Реализовать поиск */ }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Поиск",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                
                // Фильтры для типа аренды с возможностью свайпа
                FilterTabs(
                    currentFilter = currentFilter,
                    pagerState = pagerState,
                    onFilterChanged = propertyViewModel::setFilter
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProperty,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить объект")
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true,
            pageSpacing = 0.dp,
        ) { page ->
            // Отображаем список объектов для текущего фильтра
            val pageFilter = RentalFilter.values()[page]
            PropertyList(
                properties = filteredProperties,
                filter = pageFilter,
                paddingValues = paddingValues,
                onPropertyClick = onNavigateToPropertyDetail
            )
        }
    }
}

@Composable
fun PropertyList(
    properties: List<Property>,
    filter: RentalFilter,
    paddingValues: PaddingValues,
    onPropertyClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (properties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = getEmptyListMessage(filter),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Нажмите + чтобы добавить новый объект",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(properties) { property ->
                    PropertyItem(
                        property = property,
                        onClick = { onPropertyClick(property.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilterTabs(
    currentFilter: RentalFilter,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onFilterChanged: (RentalFilter) -> Unit
) {
    TabRow(
        selectedTabIndex = currentFilter.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = { /* Удаляем разделитель для более современного вида */ }
    ) {
        RentalFilter.values().forEachIndexed { index, filter ->
            Tab(
                selected = currentFilter == filter,
                onClick = { onFilterChanged(filter) },
                text = { 
                    Text(
                        text = when(filter) {
                            RentalFilter.LONG_TERM -> "Длительно"
                            RentalFilter.SHORT_TERM -> "Посуточно"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    ) 
                }
            )
        }
    }
}

// Функция для получения сообщения при пустом списке
fun getEmptyListMessage(filter: RentalFilter): String {
    return when (filter) {
        RentalFilter.LONG_TERM -> "Нет объектов для длительной аренды"
        RentalFilter.SHORT_TERM -> "Нет объектов для посуточной аренды"
    }
}

@Composable
fun PropertyItem(
    property: Property,
    onClick: () -> Unit = {}
) {
    com.realestateassistant.pro.presentation.components.property.PropertyCard(
        property = property,
        onClick = onClick
    )
}

@Composable
fun PropertyThumbnail(property: Property) {
    com.realestateassistant.pro.presentation.components.property.PropertyThumbnail(property)
}

@Composable
fun PropertyFeatureChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
} 