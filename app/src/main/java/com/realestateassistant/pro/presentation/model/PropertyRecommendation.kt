package com.realestateassistant.pro.presentation.model

import com.realestateassistant.pro.domain.model.Property

/**
 * Модель для представления рекомендаций объектов недвижимости в UI.
 * 
 * @property property Объект недвижимости
 * @property matchScore Оценка соответствия предпочтениям клиента (от 0.0 до 1.0)
 * @property matchPercent Процент соответствия (от 0 до 100)
 * @property matchDescription Текстовое описание уровня соответствия
 */
data class PropertyRecommendation(
    val property: Property,
    val matchScore: Float,
    val matchPercent: Int = (matchScore * 100).toInt(),
    val matchDescription: String = getMatchDescription(matchScore)
) {
    companion object {
        /**
         * Возвращает текстовое описание уровня соответствия на основе оценки.
         * 
         * @param score Оценка соответствия (от 0.0 до 1.0)
         * @return Текстовое описание уровня соответствия
         */
        fun getMatchDescription(score: Float): String {
            return when {
                score >= 0.9f -> "Идеальное соответствие"
                score >= 0.8f -> "Отличное соответствие"
                score >= 0.7f -> "Очень хорошее соответствие"
                score >= 0.6f -> "Хорошее соответствие"
                score >= 0.5f -> "Среднее соответствие"
                score >= 0.4f -> "Приемлемое соответствие"
                score >= 0.3f -> "Низкое соответствие"
                else -> "Минимальное соответствие"
            }
        }
    }
} 