package com.realestateassistant.pro.domain.model

/**
 * Модель для хранения сезонных цен на недвижимость
 */
data class SeasonalPrice(
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val price: Double = 0.0
) 