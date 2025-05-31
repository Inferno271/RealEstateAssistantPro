package com.realestateassistant.pro.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.presentation.components.client.ClientContactInfo
import com.realestateassistant.pro.presentation.components.client.ClientInfoCard
import com.realestateassistant.pro.presentation.components.client.ClientRentalPreferences
import com.realestateassistant.pro.presentation.components.client.ClientLongTermPreferences
import com.realestateassistant.pro.presentation.components.client.ClientShortTermPreferences
import com.realestateassistant.pro.presentation.components.client.ClientHousingPreferences
import com.realestateassistant.pro.presentation.components.client.ClientAmenitiesPreferences
import com.realestateassistant.pro.presentation.components.client.ClientSpecificPropertyPreferences
import com.realestateassistant.pro.presentation.components.client.ClientLegalPreferences
import com.realestateassistant.pro.presentation.viewmodel.ClientViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экран детальной информации о клиенте.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    clientId: String?,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    clientViewModel: ClientViewModel = hiltViewModel()
) {
    val clients by clientViewModel.clients.collectAsState()
    val client = remember(clients, clientId) {
        clients.find { it.id == clientId } ?: Client()
    }
    
    val context = LocalContext.current
    
    // Диалог для подтверждения удаления
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        client.fullName.ifEmpty { "Детали клиента" },
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
                    IconButton(
                        onClick = { onNavigateToEdit(clientId ?: "") }
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Кнопка удаления
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        ClientDetailContent(
            client = client,
            modifier = Modifier.padding(paddingValues)
        )
        
        // Диалог подтверждения удаления
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Удаление клиента") },
                text = { Text("Вы уверены, что хотите удалить этого клиента?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            clientViewModel.deleteClient(client.id)
                            Toast.makeText(context, "Клиент удален", Toast.LENGTH_SHORT).show()
                            showDeleteDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

/**
 * Содержимое экрана детальной информации о клиенте.
 */
@Composable
fun ClientDetailContent(
    client: Client,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Контактная информация
        ClientContactInfo(client = client)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Информация о клиенте
        ClientInfoCard(client = client)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Общие предпочтения по аренде
        ClientRentalPreferences(client = client)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        // Предпочтения по жилищным характеристикам
        ClientHousingPreferences(client = client)
        if (!client.preferredRepairState.isNullOrEmpty() || 
            client.preferredFloorMin != null ||
            client.preferredFloorMax != null ||
            client.needsElevator ||
            client.preferredBalconiesCount != null ||
            client.preferredBathroomsCount != null ||
            !client.preferredBathroomType.isNullOrEmpty() ||
            !client.preferredHeatingType.isNullOrEmpty() ||
            client.needsParking ||
            !client.preferredParkingType.isNullOrEmpty()) {
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Предпочтения по удобствам и окружению
        ClientAmenitiesPreferences(client = client)
        if (client.preferredAmenities.isNotEmpty() || 
            client.preferredViews.isNotEmpty() || 
            client.preferredNearbyObjects.isNotEmpty()) {
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Специфические предпочтения по недвижимости
        ClientSpecificPropertyPreferences(client = client)
        if (client.needsYard || 
            client.preferredYardArea != null ||
            client.needsGarage || 
            client.preferredGarageSpaces != null ||
            client.needsBathhouse || 
            client.needsPool) {
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Юридические предпочтения
        ClientLegalPreferences(client = client)
        if (client.needsOfficialAgreement || !client.preferredTaxOption.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Информация о долгосрочной аренде
        if (client.rentalType == "длительная" || client.rentalType == "оба_варианта") {
            ClientLongTermPreferences(client = client)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Информация о краткосрочной аренде
        if (client.rentalType == "посуточная" || client.rentalType == "оба_варианта") {
            ClientShortTermPreferences(client = client)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Служебная информация
        ServiceInfoCard(client = client)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Карточка со служебной информацией.
 */
@Composable
fun ServiceInfoCard(client: Client) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Служебная информация",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            
            Text(
                text = "Создано: ${dateFormat.format(Date(client.createdAt))}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Обновлено: ${dateFormat.format(Date(client.updatedAt))}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 