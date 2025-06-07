package com.realestateassistant.pro.ui.theme

import androidx.compose.ui.graphics.Color

// Основные цвета Material Design 3
val Primary = Color(0xFF1A56DB)              // Основной синий
val OnPrimary = Color(0xFFFFFFFF)            // Белый текст на основном цвете
val PrimaryContainer = Color(0xFFD1E0FF)     // Светло-синий контейнер
val OnPrimaryContainer = Color(0xFF001944)   // Темно-синий текст на контейнере

val Secondary = Color(0xFF3F4DB0)            // Вторичный синий
val OnSecondary = Color(0xFFFFFFFF)          // Белый текст на вторичном цвете
val SecondaryContainer = Color(0xFFDDE1FF)   // Светло-синий вторичный контейнер
val OnSecondaryContainer = Color(0xFF00105C) // Темно-синий текст на вторичном контейнере

val Tertiary = Color(0xFF00639D)             // Третичный синий
val OnTertiary = Color(0xFFFFFFFF)           // Белый текст на третичном цвете
val TertiaryContainer = Color(0xFFCFE5FF)    // Светло-синий третичный контейнер
val OnTertiaryContainer = Color(0xFF001D34)  // Темно-синий текст на третичном контейнере

// Нейтральные цвета
val Background = Color(0xFFF8F9FF)           // Фон с легким синим оттенком
val OnBackground = Color(0xFF1A1C1E)         // Темный текст на фоне
val Surface = Color(0xFFF8F9FF)              // Поверхность с легким синим оттенком
val OnSurface = Color(0xFF1A1C1E)            // Темный текст на поверхности
val SurfaceVariant = Color(0xFFE1E2EC)       // Вариант поверхности
val OnSurfaceVariant = Color(0xFF44474E)     // Текст на варианте поверхности
val Outline = Color(0xFF74777F)              // Цвет контура

// Семантические цвета
val Error = Color(0xFFBA1B1B)                // Цвет ошибки
val OnError = Color(0xFFFFFFFF)              // Текст на цвете ошибки
val ErrorContainer = Color(0xFFFFDAD4)       // Контейнер ошибки
val OnErrorContainer = Color(0xFF410001)     // Текст на контейнере ошибки

// Цвета для темной темы
val DarkPrimary = Color(0xFFA1C6FF)          // Светло-синий для темной темы
val DarkOnPrimary = Color(0xFF002D6E)        // Темно-синий текст для темной темы
val DarkPrimaryContainer = Color(0xFF0041A0) // Синий контейнер для темной темы
val DarkOnPrimaryContainer = Color(0xFFD1E0FF) // Светло-синий текст для темной темы

val DarkSecondary = Color(0xFFBAC3FF)        // Светло-синий для темной темы
val DarkOnSecondary = Color(0xFF00218F)      // Темно-синий текст для темной темы
val DarkSecondaryContainer = Color(0xFF293596) // Синий контейнер для темной темы
val DarkOnSecondaryContainer = Color(0xFFDDE1FF) // Светло-синий текст для темной темы

val DarkTertiary = Color(0xFF94CCFF)         // Светло-синий для темной темы
val DarkOnTertiary = Color(0xFF003355)       // Темно-синий текст для темной темы
val DarkTertiaryContainer = Color(0xFF004A77) // Синий контейнер для темной темы
val DarkOnTertiaryContainer = Color(0xFFCFE5FF) // Светло-синий текст для темной темы

val DarkBackground = Color(0xFF1A1C1E)       // Темный фон
val DarkOnBackground = Color(0xFFE2E2E6)     // Светлый текст на темном фоне
val DarkSurface = Color(0xFF1A1C1E)          // Темная поверхность
val DarkOnSurface = Color(0xFFE2E2E6)        // Светлый текст на темной поверхности
val DarkSurfaceVariant = Color(0xFF44474E)   // Вариант темной поверхности
val DarkOnSurfaceVariant = Color(0xFFC5C6D0) // Текст на варианте темной поверхности
val DarkOutline = Color(0xFF8E9099)          // Цвет контура для темной темы

// Цвета для статусов встреч
val appointmentScheduled = Primary           // Основной синий
val appointmentConfirmed = Color(0xFF4CAF50) // Зеленый
val appointmentInProgress = Secondary        // Вторичный синий
val appointmentCompleted = Color(0xFF673AB7) // Фиолетовый
val appointmentCancelled = Color(0xFFF44336) // Красный
val appointmentRescheduled = Color(0xFFFF9800) // Оранжевый
val appointmentNoShow = Tertiary             // Третичный синий