package com.realestateassistant.pro.presentation.components.property

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.realestateassistant.pro.domain.model.RentalFilter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropertyFilterTabs(
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

object PropertyFilterTabs {
    // Функция для получения сообщения при пустом списке
    fun getEmptyListMessage(filter: RentalFilter): String {
        return when (filter) {
            RentalFilter.LONG_TERM -> "Нет объектов для длительной аренды"
            RentalFilter.SHORT_TERM -> "Нет объектов для посуточной аренды"
        }
    }
} 