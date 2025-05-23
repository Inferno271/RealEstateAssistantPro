package com.realestateassistant.pro.util

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.core.content.ContextCompat

/**
 * Класс для работы с журналом звонков
 */
object CallLogHelper {
    
    /**
     * Проверяет, есть ли разрешение на доступ к журналу звонков
     */
    fun hasCallLogPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Получает список последних звонков
     */
    fun getRecentCalls(contentResolver: ContentResolver, limit: Int = 15): List<CallLogEntry> {
        val calls = mutableListOf<CallLogEntry>()
        
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE,
            CallLog.Calls.TYPE
        )
        
        val sortOrder = "${CallLog.Calls.DATE} DESC"
        
        try {
            contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder,
                null
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndex(CallLog.Calls._ID)
                val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                val nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
                val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
                
                var count = 0
                while (cursor.moveToNext() && count < limit) {
                    val id = cursor.getString(idIndex)
                    val rawNumber = cursor.getString(numberIndex)
                    val name = cursor.getString(nameIndex) ?: ""
                    val date = cursor.getLong(dateIndex)
                    val type = cursor.getInt(typeIndex)
                    
                    // Обработка номера в международном формате
                    val formattedNumber = PhoneUtils.normalizeInternationalPhoneNumber(rawNumber)
                    
                    if (formattedNumber.isNotEmpty()) {
                        calls.add(CallLogEntry(id, formattedNumber, name, date, type))
                        count++
                    }
                }
            }
        } catch (e: Exception) {
            // Добавляем логирование ошибки
            e.printStackTrace()
        }
        
        return calls
    }
    
    /**
     * Устаревший метод для обратной совместимости
     */
    fun normalizePhoneNumber(phoneNumber: String): String {
        return PhoneUtils.normalizePhoneNumber(phoneNumber)
    }
    
    /**
     * Форматирует номер телефона для отображения
     */
    fun buildFormattedPhoneNumber(input: String): String {
        return PhoneUtils.formatInternationalPhoneNumber(input)
    }
    
    /**
     * Возвращает отформатированное имя контакта для отображения
     */
    fun formatContactDisplay(entry: CallLogEntry): String {
        val name = if (entry.name.isNotEmpty()) entry.name else "Неизвестный"
        return "${name}: ${PhoneUtils.formatInternationalPhoneNumber(entry.number)}"
    }
}

/**
 * Модель записи журнала звонков
 */
data class CallLogEntry(
    val id: String,
    val number: String,
    val name: String,
    val date: Long,
    val type: Int
) 