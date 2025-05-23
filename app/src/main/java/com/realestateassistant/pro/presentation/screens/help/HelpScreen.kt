package com.realestateassistant.pro.presentation.screens.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Экран помощи пользователю
 */
@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Помощь",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        HelpCard(
            title = "Добавление объекта",
            content = "Для добавления нового объекта нажмите на кнопку '+' на главном экране. " +
                    "Заполните необходимые поля и нажмите 'Сохранить'."
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HelpCard(
            title = "Управление клиентами",
            content = "Во вкладке 'Клиенты' вы можете добавлять новых клиентов, " +
                    "просматривать информацию о существующих и связывать их с объектами недвижимости."
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HelpCard(
            title = "Планирование встреч",
            content = "Запланировать встречу с клиентом можно во вкладке 'Встречи'. " +
                    "Укажите дату, время, клиента и объект недвижимости."
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HelpCard(
            title = "Настройки профиля",
            content = "В разделе 'Профиль' вы можете изменить личные данные, " +
                    "настроить уведомления и управлять аккаунтом."
        )
    }
}

/**
 * Карточка с информацией по разделу помощи
 */
@Composable
private fun HelpCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 