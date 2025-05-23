package com.realestateassistant.pro.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.domain.usecase.ImageUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageUseCases: ImageUseCases
) : ViewModel() {
    
    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error
    
    // Последнее загруженное изображение
    private val _loadedImage = MutableStateFlow<Bitmap?>(null)
    val loadedImage: StateFlow<Bitmap?> get() = _loadedImage
    
    /**
     * Сохраняет изображение из URI
     * @param uri URI изображения
     * @param compress Нужно ли сжимать
     * @param quality Качество сжатия (0-100)
     * @return Путь к сохраненному изображению
     */
    suspend fun saveImage(uri: Uri, compress: Boolean = true, quality: Int = 80): Result<String> {
        _isLoading.value = true
        _error.value = null
        
        val result = imageUseCases.saveImage(uri, compress, quality)
        
        _isLoading.value = false
        if (result.isFailure) {
            _error.value = result.exceptionOrNull()?.message ?: "Ошибка сохранения изображения"
        }
        
        return result
    }
    
    /**
     * Сохраняет список изображений
     * @param uris Список URI изображений
     * @param compress Нужно ли сжимать
     * @param quality Качество сжатия (0-100)
     * @return Список путей к сохраненным изображениям
     */
    suspend fun saveImages(uris: List<Uri>, compress: Boolean = true, quality: Int = 80): Result<List<String>> {
        _isLoading.value = true
        _error.value = null
        
        val result = imageUseCases.saveImages(uris, compress, quality)
        
        _isLoading.value = false
        if (result.isFailure) {
            _error.value = result.exceptionOrNull()?.message ?: "Ошибка сохранения изображений"
        }
        
        return result
    }
    
    /**
     * Загружает изображение для предпросмотра
     * @param path Путь к изображению
     */
    fun loadImage(path: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = imageUseCases.loadImage(path)
            
            _isLoading.value = false
            result.onSuccess { bitmap ->
                _loadedImage.value = bitmap
            }.onFailure { exception ->
                _error.value = exception.message ?: "Ошибка загрузки изображения"
                _loadedImage.value = null
            }
        }
    }
    
    /**
     * Удаляет изображение по указанному пути
     * @param path Путь к изображению
     */
    fun deleteImage(path: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = imageUseCases.deleteImage(path)
            
            _isLoading.value = false
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Ошибка удаления изображения"
            }
        }
    }
    
    /**
     * Проверяет валидность URI изображения
     * @param uri URI для проверки
     * @return true, если URI валидный
     */
    suspend fun isValidImageUri(uri: Uri): Boolean {
        return imageUseCases.validateImageUri(uri)
    }
    
    /**
     * Очищает кэш изображений
     */
    fun clearImageCache() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = imageUseCases.clearImageCache()
            
            _isLoading.value = false
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Ошибка очистки кэша"
            }
        }
    }
    
    /**
     * Очищает состояние ошибки
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Очищает загруженное изображение
     */
    fun clearLoadedImage() {
        _loadedImage.value = null
    }
} 