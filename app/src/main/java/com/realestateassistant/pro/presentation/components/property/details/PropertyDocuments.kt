package com.realestateassistant.pro.presentation.components.property.details

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.presentation.components.common.ErrorDialog
import com.realestateassistant.pro.presentation.components.property.PropertySectionCard
import com.realestateassistant.pro.presentation.viewmodels.DocumentViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PropertyDocuments(
    property: Property,
    documentViewModel: DocumentViewModel = hiltViewModel()
) {
    if (property.documents.isEmpty()) return
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    PropertySectionCard(title = "Документы") {
        property.documents.forEachIndexed { index, documentPath ->
            DocumentItem(
                documentPath = documentPath,
                index = index + 1,
                onClick = {
                    scope.launch {
                        documentViewModel.openDocument(documentPath)
                            .onSuccess { (uri, mimeType) ->
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, mimeType ?: "application/octet-stream")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                
                                try {
                                    context.startActivity(Intent.createChooser(
                                        intent,
                                        "Выберите приложение для просмотра документа"
                                    ))
                                } catch (e: Exception) {
                                    errorMessage = "Не найдено приложение для просмотра данного типа документа"
                                }
                            }
                            .onFailure { exception ->
                                errorMessage = exception.message ?: "Ошибка при открытии документа"
                            }
                    }
                }
            )
            
            if (index < property.documents.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    
    // Диалог ошибки
    if (errorMessage != null) {
        ErrorDialog(
            message = errorMessage ?: "Ошибка при открытии документа",
            onDismiss = { errorMessage = null }
        )
    }
}

@Composable
fun DocumentItem(
    documentPath: String,
    index: Int,
    onClick: () -> Unit
) {
    val fileName = remember(documentPath) {
        if (documentPath.isNotEmpty()) {
            File(documentPath).name
        } else {
            "Документ $index"
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Description,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Нажмите для просмотра документа",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 