package com.realestateassistant.pro.presentation.components.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties

/**
 * Диалог для отображения сообщений об ошибках
 * 
 * @param message Сообщение об ошибке
 * @param onDismiss Действие при закрытии диалога
 * @param title Заголовок диалога (опционально)
 */
@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
    title: String = "Ошибка"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("ОК")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
} 