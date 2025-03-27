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
                    val number = cursor.getString(numberIndex)
                    val name = cursor.getString(nameIndex) ?: ""
                    val date = cursor.getLong(dateIndex)
                    val type = cursor.getInt(typeIndex)
                    
                    // Нормализуем номер телефона, оставляя только 10 последних цифр
                    val normalizedNumber = normalizePhoneNumber(number)
                    
                    // Добавляем в список только если номер корректный и имеет нужную длину
                    if (normalizedNumber.isNotEmpty()) {
                        calls.add(CallLogEntry(id, normalizedNumber, name, date, type))
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
     * Нормализует номер телефона, оставляя только 10 последних цифр
     */
    fun normalizePhoneNumber(phoneNumber: String): String {
        val digitsOnly = phoneNumber.filter { it.isDigit() }
        // Для российских номеров оставляем только 10 цифр (без кода страны)
        return if (digitsOnly.length > 10) {
            digitsOnly.takeLast(10)
        } else {
            digitsOnly
        }
    }
    
    /**
     * Форматирует номер телефона для отображения в формате +7 (XXX) XXX-XX-XX
     */
    fun buildFormattedPhoneNumber(input: String): String {
        val builder = StringBuilder("+7 (")
        
        input.forEachIndexed { index, char ->
            builder.append(char)
            when (index) {
                2 -> builder.append(") ") // После третьей цифры добавляем ) и пробел
                5 -> builder.append("-") // После шестой цифры добавляем дефис
                7 -> builder.append("-") // После восьмой цифры добавляем дефис
            }
        }
        
        // Дополняем шаблон, если он не полностью заполнен
        return when (input.length) {
            0 -> "+7 ("
            1 -> "+7 (${input[0]}"
            2 -> "+7 (${input[0]}${input[1]}"
            3 -> "+7 (${input.substring(0, 3)})"
            4 -> "+7 (${input.substring(0, 3)}) ${input[3]}"
            5 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 5)}"
            6 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}"
            7 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}-${input[6]}"
            8 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}-${input.substring(6, 8)}"
            9 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}-${input.substring(6, 8)}-${input[8]}"
            10 -> "+7 (${input.substring(0, 3)}) ${input.substring(3, 6)}-${input.substring(6, 8)}-${input.substring(8, 10)}"
            else -> builder.toString()
        }
    }
    
    /**
     * Возвращает отформатированное имя контакта для отображения
     */
    fun formatContactDisplay(entry: CallLogEntry): String {
        val name = if (entry.name.isNotEmpty()) entry.name else "Неизвестный"
        return "${name}: ${buildFormattedPhoneNumber(entry.number)}"
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