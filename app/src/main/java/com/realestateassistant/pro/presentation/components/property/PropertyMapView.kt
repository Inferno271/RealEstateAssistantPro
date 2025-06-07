package com.realestateassistant.pro.presentation.components.property

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.graphics.Bitmap
import android.graphics.Canvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

private var isMapKitInitialized = false
private const val TAG = "PropertyMapView"

/**
 * Компонент для отображения карты с местоположением объекта недвижимости
 * 
 * @param address Адрес объекта недвижимости
 * @param latitude Широта местоположения объекта
 * @param longitude Долгота местоположения объекта
 * @param markerColor Цвет метки на карте
 * @param modifier Модификатор для настройки внешнего вида компонента
 */
@Composable
fun PropertyMapView(
    address: String,
    latitude: Double,
    longitude: Double,
    markerColor: Color = Color(0xFF2196F3), // Синий цвет по умолчанию
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    
    // Создаем изображение маркера заранее в фоновом потоке
    val coloredMarker = remember(markerColor) {
        var bitmap: Bitmap? = null
        coroutineScope.launch(Dispatchers.Default) {
            bitmap = createColoredMarker(context, android.R.drawable.ic_menu_mylocation, markerColor)
        }
        bitmap
    }

    // Инициализируем MapKit только один раз при первом создании компонента
    if (!isMapKitInitialized) {
        try {
            MapKitFactory.initialize(context)
            isMapKitInitialized = true
            Log.d(TAG, "MapKit успешно инициализирован в основном потоке")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка инициализации MapKit в основном потоке: ${e.message}")
        }
    }
    
    val mapView = remember { 
        MapView(context).apply {
            this.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        } 
    }
    
    // Обработка жизненного цикла для корректной работы карты
    DisposableEffect(lifecycleOwner) {
        var isStarted = false
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d(TAG, "onCreate")
                }
                Lifecycle.Event.ON_START -> {
                    if (!isStarted) {
                        try {
                            MapKitFactory.getInstance().onStart()
                            mapView.onStart()
                            isStarted = true
                            Log.d(TAG, "onStart")
                        } catch (e: Exception) {
                            Log.e(TAG, "Ошибка при onStart: ${e.message}")
                        }
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG, "onResume")
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "onPause")
                }
                Lifecycle.Event.ON_STOP -> {
                    if (isStarted) {
                        try {
                            mapView.onStop()
                            MapKitFactory.getInstance().onStop()
                            isStarted = false
                            Log.d(TAG, "onStop")
                        } catch (e: Exception) {
                            Log.e(TAG, "Ошибка при onStop: ${e.message}")
                        }
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d(TAG, "onDestroy")
                }
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            Log.d(TAG, "onDispose: очистка ресурсов карты")
            lifecycleOwner.lifecycle.removeObserver(observer)
            
            try {
                // Очищаем карту при удалении из композиции
                mapView.map.mapObjects.clear()
                
                // Отключаем карту
                mapView.onStop()
                
                // Удаляем view из родительского контейнера
                (mapView.parent as? ViewGroup)?.removeView(mapView)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при очистке ресурсов карты: ${e.message}")
            }
        }
    }
    
    // Этот DisposableEffect будет вызван при каждом изменении параметров композиции
    DisposableEffect(latitude, longitude, markerColor, coloredMarker) {
        // Добавляем маркер на карту при изменении координат
        val point = Point(latitude, longitude)
        coroutineScope.launch(Dispatchers.Main) {
            try {
                // Очищаем предыдущие маркеры
                mapView.map.mapObjects.clear()
                
                // Добавляем новый маркер
                val placemark = mapView.map.mapObjects.addPlacemark(point)
                
                // Устанавливаем иконку маркера
                coloredMarker?.let { bitmap ->
                    val iconStyle = IconStyle().apply {
                        scale = 1.0f
                    }
                    
                    placemark.setIcon(
                        ImageProvider.fromBitmap(bitmap),
                        iconStyle
                    )
                }
                
                // Устанавливаем текст маркера
                try {
                    // Создаем стиль текста для лучшей видимости
                    val textStyle = TextStyle().apply {
                        size = 10.0f
                        placement = TextStyle.Placement.BOTTOM
                        offset = 3.0f
                    }
                    
                    placemark.setText(address, textStyle)
                    Log.d(TAG, "Установлен текст маркера: $address")
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка при установке текста маркера: ${e.message}")
                }
                
                // Устанавливаем положение камеры
                mapView.map.move(
                    CameraPosition(
                        point,
                        14.0f, // Уровень масштабирования
                        0.0f, // Угол наклона
                        0.0f // Угол поворота
                    ),
                    Animation(Animation.Type.SMOOTH, 0.3f),
                    null
                )
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при обновлении маркера: ${e.message}")
            }
        }
        
        onDispose {
            Log.d(TAG, "onDispose координат: очистка маркеров")
            mapView.map.mapObjects.clear()
        }
    }
    
    // Отображаем карту
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Создает цветную иконку маркера в фоновом потоке
 */
private suspend fun createColoredMarker(context: Context, drawableResId: Int, color: Color): Bitmap = 
    withContext(Dispatchers.Default) {
        val drawable = ContextCompat.getDrawable(context, drawableResId)
            ?: return@withContext Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        
        val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
        DrawableCompat.setTint(wrappedDrawable, color.toArgb())
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
        
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, 
            drawable.intrinsicHeight, 
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        wrappedDrawable.setBounds(0, 0, canvas.width, canvas.height)
        wrappedDrawable.draw(canvas)
        
        bitmap
    } 