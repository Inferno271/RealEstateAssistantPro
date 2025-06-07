package com.realestateassistant.pro.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.RentalFilter
import com.realestateassistant.pro.domain.model.ClientFilter
import com.realestateassistant.pro.presentation.components.client.ClientCard
import com.realestateassistant.pro.presentation.components.client.ActiveClientFiltersRow
import com.realestateassistant.pro.presentation.components.client.ClientFilterPanel
import com.realestateassistant.pro.presentation.viewmodel.ClientViewModel
import com.realestateassistant.pro.presentation.viewmodel.OptionsViewModel
import com.realestateassistant.pro.presentation.components.common.SearchBar
import kotlinx.coroutines.launch

/**
 * Экран отображения списка клиентов.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ClientListScreen(
    onNavigateToAddClient: () -> Unit,
    onNavigateToClientDetail: (String) -> Unit,
    clientViewModel: ClientViewModel = hiltViewModel(),
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    drawerState: DrawerState? = null
) {
    val filteredClients by clientViewModel.filteredClients.collectAsState()
    val allClients by clientViewModel.clients.collectAsState() // Для отладки
    val currentFilter by clientViewModel.currentFilter.collectAsState()
    val clientFilter by clientViewModel.clientFilter.collectAsState()
    val scope = rememberCoroutineScope()
    
    var showSearchBar by remember { mutableStateOf(false) }
    var showFilterPanel by remember { mutableStateOf(false) }
    
    // Отладочная информация
    LaunchedEffect(Unit) {
        println("DEBUG: ClientListScreen - init, всего клиентов: ${allClients.size}")
        println("DEBUG: ClientListScreen - отфильтрованных клиентов: ${filteredClients.size}")
    }
    
    // Отладочная информация при изменении списков
    LaunchedEffect(allClients.size, filteredClients.size) {
        println("DEBUG: ClientListScreen - всего клиентов: ${allClients.size}")
        println("DEBUG: ClientListScreen - отфильтрованных клиентов: ${filteredClients.size}")
        println("DEBUG: ClientListScreen - текущий фильтр: $currentFilter")
    }
    
    // Создаем состояние пейджера, которое будет синхронизировано с текущим фильтром
    val pagerState = rememberPagerState(
        initialPage = currentFilter.ordinal,
        pageCount = { RentalFilter.values().size }
    )

    // Загружаем клиентов при каждом переходе на экран
    LaunchedEffect(Unit) {
        println("DEBUG: ClientListScreen - запрос на загрузку клиентов")
        clientViewModel.loadClients()
    }

    // Синхронизируем состояние пейджера с фильтром и наоборот
    LaunchedEffect(pagerState.currentPage) {
        if (currentFilter.ordinal != pagerState.currentPage) {
            clientViewModel.setFilter(RentalFilter.values()[pagerState.currentPage])
        }
    }

    LaunchedEffect(currentFilter) {
        if (pagerState.currentPage != currentFilter.ordinal) {
            scope.launch {
                pagerState.animateScrollToPage(currentFilter.ordinal)
            }
        }
    }
    
    // Отображаем фильтры, если активны
    if (showFilterPanel) {
        ClientFilterPanel(
            clientFilter = clientFilter,
            onFilterChange = { newFilter ->
                clientViewModel.updateClientFilter(newFilter)
            },
            onClearFilters = {
                clientViewModel.clearClientFilters()
            },
            onClose = { showFilterPanel = false },
            optionsViewModel = optionsViewModel
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Клиенты") },
                    navigationIcon = {
                        if (drawerState != null) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Открыть меню",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearchBar = !showSearchBar }) {
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
                
                // Строка с активными фильтрами
                if (clientFilter.hasActiveFilters()) {
                    ActiveClientFiltersRow(
                        clientFilter = clientFilter,
                        onFilterChange = { newFilter -> 
                            clientViewModel.updateClientFilter(newFilter)
                        },
                        onClearFilters = {
                            clientViewModel.clearClientFilters()
                        }
                    )
                }
                
                // Поисковая строка
                AnimatedVisibility(
                    visible = showSearchBar || clientFilter.searchQuery.isNotEmpty(),
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        SearchBar(
                            query = clientFilter.searchQuery,
                            onQueryChange = { query ->
                                clientViewModel.updateSearchQuery(query)
                            },
                            onSearch = { query ->
                                clientViewModel.updateSearchQuery(query)
                            },
                            onFilterClick = {
                                showFilterPanel = !showFilterPanel
                            },
                            placeholder = "Поиск по имени, телефону, району, профессии...",
                            showFilterButton = true
                        )
                        
                        // Отображаем выбранные фильтры, если есть
                        if (clientFilter.hasActiveFiltersBesideSearch() && !showFilterPanel) {
                            // Показываем индикатор активных фильтров
                            ActiveClientFiltersRow(
                                clientFilter = clientFilter,
                                onFilterChange = { newFilter -> 
                                    clientViewModel.updateClientFilter(newFilter)
                                },
                                onClearFilters = {
                                    // Сохраняем поисковый запрос при сбросе других фильтров
                                    clientViewModel.updateClientFilter(clientFilter.clearFiltersKeepSearch())
                                }
                            )
                        }
                        
                        // Отображаем панель фильтров, если открыта
                        AnimatedVisibility(
                            visible = showFilterPanel,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            ClientFilterPanel(
                                clientFilter = clientFilter,
                                onFilterChange = { newFilter ->
                                    clientViewModel.updateClientFilter(newFilter)
                                },
                                onClearFilters = {
                                    clientViewModel.clearClientFilters()
                                },
                                onClose = { showFilterPanel = false },
                                optionsViewModel = optionsViewModel
                            )
                        }
                    }
                }
                
                // Фильтры для типа аренды с возможностью свайпа
                ClientFilterTabs(
                    currentFilter = currentFilter,
                    pagerState = pagerState,
                    onFilterChanged = clientViewModel::setFilter
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddClient,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить клиента")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true,
            pageSpacing = 0.dp,
        ) { page ->
            // Отображаем список клиентов для текущего фильтра
            val pageFilter = RentalFilter.values()[page]
            ClientList(
                clients = filteredClients,
                filter = pageFilter,
                paddingValues = paddingValues,
                onClientClick = onNavigateToClientDetail
            )
        }
    }
}

@Composable
fun ClientList(
    clients: List<Client>,
    filter: RentalFilter,
    paddingValues: PaddingValues,
    onClientClick: (String) -> Unit = {}
) {
    if (clients.isEmpty()) {
        // Если список пустой, показываем сообщение
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
                    text = getEmptyClientListMessage(filter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Нажмите + чтобы добавить нового клиента",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        // Если есть клиенты, показываем список
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(clients) { client ->
                ClientCard(
                    client = client,
                    onClick = { onClientClick(client.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClientFilterTabs(
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
fun getEmptyClientListMessage(filter: RentalFilter): String {
    return when (filter) {
        RentalFilter.LONG_TERM -> "Нет клиентов для длительной аренды"
        RentalFilter.SHORT_TERM -> "Нет клиентов для посуточной аренды"
    }
} 