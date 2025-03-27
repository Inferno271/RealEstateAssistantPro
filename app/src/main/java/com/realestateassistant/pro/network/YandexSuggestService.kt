package com.realestateassistant.pro.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.net.UnknownHostException

/**
 * Сервис для получения подсказок по адресу от Яндекс геосаджеста
 */
object YandexSuggestService {
    private const val API_KEY = "e2565955-c794-4019-8b24-5805c9eecab3"
    private const val BASE_URL = "https://suggest-maps.yandex.ru/v1/suggest"
    private const val TAG = "YandexSuggestService"

    /**
     * Модель подсказки адреса
     */
    data class AddressSuggestion(
        val title: String,
        val fullAddress: String,
        val geoId: String? = null
    )

    /**
     * Получает подсказки адресов по введенному тексту
     * @param query текст запроса (часть адреса)
     * @return список подсказок адресов
     */
    suspend fun getSuggestions(query: String, context: Context? = null): List<AddressSuggestion> = withContext(Dispatchers.IO) {
        if (query.length < 3) return@withContext emptyList<AddressSuggestion>()
        
        // Проверяем подключение к интернету, если контекст доступен
        if (context != null && !isNetworkAvailable(context)) {
            throw NoInternetException("Отсутствует подключение к интернету")
        }

        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val urlString = "$BASE_URL?apikey=$API_KEY&text=$encodedQuery&types=geo&lang=ru_RU"
            
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // 5 секунд на подключение
            connection.readTimeout = 10000 // 10 секунд на чтение
            
            try {
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    
                    return@withContext parseSuggestions(response.toString())
                } else {
                    Log.e(TAG, "HTTP error: $responseCode")
                    throw ApiException("Ошибка API: код $responseCode")
                }
            } finally {
                connection.disconnect()
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Host not found", e)
            throw NoInternetException("Не удалось подключиться к серверу. Проверьте подключение к интернету.")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching suggestions", e)
            throw e
        }
    }

    /**
     * Парсит JSON-ответ от API и преобразует в список подсказок
     */
    private fun parseSuggestions(jsonResponse: String): List<AddressSuggestion> {
        val suggestions = mutableListOf<AddressSuggestion>()
        try {
            val jsonObject = JSONObject(jsonResponse)
            val results = jsonObject.getJSONArray("results")
            
            for (i in 0 until results.length()) {
                val item = results.getJSONObject(i)
                
                // Получаем заголовок (название улицы или места)
                val titleObj = item.getJSONObject("title")
                val title = titleObj.getString("text")
                
                // Получаем подзаголовок (город или регион), если есть
                var subtitle = ""
                if (item.has("subtitle") && !item.isNull("subtitle")) {
                    val subtitleObj = item.getJSONObject("subtitle")
                    subtitle = subtitleObj.getString("text")
                }
                
                // Формируем полный адрес
                val fullAddress = if (subtitle.isNotEmpty()) {
                    "$title, $subtitle"
                } else {
                    title
                }
                
                // URI объекта (может использоваться как идентификатор)
                val geoId = if (item.has("uri") && !item.isNull("uri")) {
                    item.getString("uri")
                } else {
                    null
                }
                
                if (title.isNotEmpty()) {
                    suggestions.add(AddressSuggestion(title, fullAddress, geoId))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON", e)
            throw ParsingException("Ошибка при обработке ответа сервера")
        }
        return suggestions
    }
    
    /**
     * Проверяет доступность интернет-соединения
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                  capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
    
    // Классы исключений для обработки различных ошибок
    class NoInternetException(message: String) : Exception(message)
    class ApiException(message: String) : Exception(message)
    class ParsingException(message: String) : Exception(message)
} 