package com.realestateassistant.pro.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Сервис для геокодирования адресов с использованием API Яндекс.Карт
 */
object YandexGeocoderService {
    private const val API_KEY = "7c70103b-2059-4cb3-b1af-afc45c6ab63f" // API-ключ для геокодера
    private const val BASE_URL = "https://geocode-maps.yandex.ru/1.x/"
    private const val TAG = "YandexGeocoderService"
    
    /**
     * Получает координаты (широта, долгота) по адресу
     * 
     * @param address адрес для геокодирования
     * @return пара значений (широта, долгота) или null, если геокодирование не удалось
     */
    suspend fun getCoordinates(address: String): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        if (address.isEmpty()) return@withContext null
        
        try {
            Log.d(TAG, "Начинается геокодирование адреса: $address")
            val encodedAddress = URLEncoder.encode(address, "UTF-8")
            val urlString = "$BASE_URL?apikey=$API_KEY&format=json&geocode=$encodedAddress"
            
            Log.d(TAG, "URL запроса: $urlString")
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // 5 секунд на подключение
            connection.readTimeout = 10000 // 10 секунд на чтение
            
            try {
                val responseCode = connection.responseCode
                Log.d(TAG, "Код ответа: $responseCode")
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "Получен ответ: ${response.take(200)}...") // Логгируем начало ответа
                    
                    val coordinates = parseCoordinates(response)
                    if (coordinates != null) {
                        Log.d(TAG, "Координаты успешно получены: ${coordinates.first}, ${coordinates.second}")
                        return@withContext coordinates
                    } else {
                        Log.e(TAG, "Не удалось извлечь координаты из ответа")
                        return@withContext null
                    }
                } else {
                    val errorMessage = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Неизвестная ошибка"
                    Log.e(TAG, "HTTP ошибка: $responseCode, $errorMessage")
                    return@withContext null
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при геокодировании адреса", e)
            return@withContext null
        }
    }
    
    /**
     * Парсит JSON-ответ от API геокодирования и извлекает координаты
     */
    private fun parseCoordinates(jsonResponse: String): Pair<Double, Double>? {
        try {
            val jsonObject = JSONObject(jsonResponse)
            val response = jsonObject.getJSONObject("response")
            val geoObjectCollection = response.getJSONObject("GeoObjectCollection")
            val featureMember = geoObjectCollection.getJSONArray("featureMember")
            
            if (featureMember.length() > 0) {
                val geoObject = featureMember.getJSONObject(0).getJSONObject("GeoObject")
                val point = geoObject.getJSONObject("Point")
                val pos = point.getString("pos")
                
                Log.d(TAG, "Позиция из ответа: $pos")
                
                // Координаты в формате "долгота широта"
                val coordinates = pos.split(" ")
                if (coordinates.size == 2) {
                    val longitude = coordinates[0].toDouble()
                    val latitude = coordinates[1].toDouble()
                    return Pair(latitude, longitude)
                }
            } else {
                Log.e(TAG, "Массив featureMember пуст")
            }
            
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при парсинге JSON", e)
            return null
        }
    }
} 