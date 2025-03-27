package com.realestateassistant.pro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue40.copy(alpha = 0.7f),
    onPrimaryContainer = Color.White,
    
    secondary = Green40,
    onSecondary = Color.White,
    secondaryContainer = Green40.copy(alpha = 0.7f),
    onSecondaryContainer = Color.White,
    
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Teal40.copy(alpha = 0.7f),
    onTertiaryContainer = Color.White,
    
    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorColor.copy(alpha = 0.7f),
    onErrorContainer = Color.White,
    
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    
    surfaceVariant = Gray40,
    onSurfaceVariant = Color.White.copy(alpha = 0.8f),
    outline = Gray40.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue80,
    onPrimaryContainer = Blue40,
    
    secondary = Green40,
    onSecondary = Color.White,
    secondaryContainer = Green80,
    onSecondaryContainer = Green40,
    
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Teal80,
    onTertiaryContainer = Teal40,
    
    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorColor.copy(alpha = 0.1f),
    onErrorContainer = ErrorColor,
    
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    
    surfaceVariant = Gray80,
    onSurfaceVariant = Gray40,
    outline = Gray40.copy(alpha = 0.5f)
)

@Composable
fun RealEstateAssistantProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Отключаем динамические цвета для консистентности
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Устанавливаем цвет статусбара в соответствии с темой
            window.statusBarColor = colorScheme.surface.toArgb()

            // Устанавливаем цвет иконок статусбара в зависимости от темы
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            
            // Устанавливаем цвет строки навигации
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}