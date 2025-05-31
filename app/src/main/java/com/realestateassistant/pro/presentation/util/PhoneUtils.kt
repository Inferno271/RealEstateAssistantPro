package com.realestateassistant.pro.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Утилитарный класс для работы с телефонными номерами
 */
object PhoneUtils {

    /**
     * Форматирует телефонный номер для отображения
     * 
     * @param phone Телефонный номер для форматирования
     * @return Отформатированный номер телефона
     */
    fun formatPhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        
        if (digits.length == 11 && (digits.startsWith("7") || digits.startsWith("8"))) {
            return buildString {
                append("+7 (")
                append(digits.substring(1, 4))
                append(") ")
                append(digits.substring(4, 7))
                append("-")
                append(digits.substring(7, 9))
                append("-")
                append(digits.substring(9, 11))
            }
        }
        
        // Для других случаев просто возвращаем исходный номер
        return phone
    }

    /**
     * Открывает приложение для звонка с указанным номером телефона
     * 
     * @param context Контекст приложения
     * @param phone Номер телефона для звонка
     */
    fun dialPhoneNumber(context: Context, phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phone.filter { it.isDigit() }}")
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
    
    /**
     * Открывает приложение для отправки SMS на указанный номер телефона
     * 
     * @param context Контекст приложения
     * @param phone Номер телефона для отправки SMS
     * @param message Текст сообщения (опционально)
     */
    fun sendSMS(context: Context, phone: String, message: String = "") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${phone.filter { it.isDigit() }}")
            if (message.isNotEmpty()) {
                putExtra("sms_body", message)
            }
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
    
    /**
     * Открывает WhatsApp с указанным номером телефона
     * 
     * @param context Контекст приложения
     * @param phone Номер телефона для WhatsApp
     * @param message Текст сообщения (опционально)
     */
    fun openWhatsApp(context: Context, phone: String, message: String = "") {
        val formattedPhone = phone.filter { it.isDigit() }
        val uri = if (message.isEmpty()) {
            Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone")
        } else {
            Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}")
        }
        
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
    
    /**
     * Открывает Telegram с указанным номером телефона
     * 
     * @param context Контекст приложения
     * @param phone Номер телефона для Telegram
     */
    fun openTelegram(context: Context, phone: String) {
        val formattedPhone = phone.filter { it.isDigit() }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?phone=$formattedPhone"))
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Если приложение Telegram не установлено, открываем веб-версию
            val webIntent = Intent(Intent.ACTION_VIEW, 
                Uri.parse("https://t.me/+$formattedPhone"))
            context.startActivity(webIntent)
        }
    }

    /**
     * Открывает WhatsApp с указанным номером телефона.
     */
    fun openWhatsApp(context: Context, phoneNumber: String) {
        val formattedNumber = formatPhoneForWhatsApp(phoneNumber)
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$formattedNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Если WhatsApp не установлен или произошла другая ошибка
            Toast.makeText(
                context,
                "Не удалось открыть WhatsApp",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Форматирует номер телефона для использования с WhatsApp.
     * Удаляет все нецифровые символы и добавляет код страны если его нет.
     */
    private fun formatPhoneForWhatsApp(phoneNumber: String): String {
        val digits = phoneNumber.filter { it.isDigit() }
        return when {
            digits.startsWith("7") -> digits
            digits.startsWith("8") -> "7${digits.substring(1)}"
            else -> "7$digits" // По умолчанию используем код России
        }
    }
} 