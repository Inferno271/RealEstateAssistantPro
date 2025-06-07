package com.realestateassistant.pro.domain.model

/**
 * Модель фильтров для клиентов
 *
 * @param rentalTypes набор типов аренды для фильтрации
 * @param familyCompositions набор типов состава семьи для фильтрации
 * @param urgencyLevels набор уровней срочности для фильтрации
 * @param propertyTypes набор типов недвижимости для фильтрации
 * @param peopleCountRanges набор диапазонов количества проживающих для фильтрации
 * @param districts набор районов для фильтрации
 * @param minLongTermBudget минимальный бюджет для длительной аренды
 * @param maxLongTermBudget максимальный бюджет для длительной аренды
 * @param minShortTermBudget минимальный бюджет для посуточной аренды
 * @param maxShortTermBudget максимальный бюджет для посуточной аренды
 * @param searchQuery строка поискового запроса
 */
data class ClientFilter(
    val rentalTypes: Set<String> = emptySet(),
    val familyCompositions: Set<String> = emptySet(),
    val urgencyLevels: Set<String> = emptySet(),
    val propertyTypes: Set<String> = emptySet(),
    val peopleCountRanges: Set<String> = emptySet(),
    val districts: Set<String> = emptySet(),
    val minLongTermBudget: Double? = null,
    val maxLongTermBudget: Double? = null,
    val minShortTermBudget: Double? = null,
    val maxShortTermBudget: Double? = null,
    val searchQuery: String = ""
) {
    /**
     * Проверяет, есть ли активные фильтры
     * 
     * @return true если хотя бы один фильтр активен, false если нет активных фильтров
     */
    fun hasActiveFilters(): Boolean {
        return rentalTypes.isNotEmpty() || 
               familyCompositions.isNotEmpty() || 
               urgencyLevels.isNotEmpty() || 
               propertyTypes.isNotEmpty() ||
               peopleCountRanges.isNotEmpty() ||
               districts.isNotEmpty() ||
               minLongTermBudget != null ||
               maxLongTermBudget != null ||
               minShortTermBudget != null ||
               maxShortTermBudget != null ||
               searchQuery.isNotEmpty()
    }
    
    /**
     * Проверяет, есть ли активные фильтры кроме поиска
     * 
     * @return true если есть активные фильтры кроме поискового запроса
     */
    fun hasActiveFiltersBesideSearch(): Boolean {
        return rentalTypes.isNotEmpty() || 
               familyCompositions.isNotEmpty() || 
               urgencyLevels.isNotEmpty() ||
               propertyTypes.isNotEmpty() ||
               peopleCountRanges.isNotEmpty() ||
               districts.isNotEmpty() ||
               minLongTermBudget != null ||
               maxLongTermBudget != null ||
               minShortTermBudget != null ||
               maxShortTermBudget != null
    }
    
    /**
     * Очищает все фильтры и возвращает новый экземпляр
     *
     * @return новый экземпляр ClientFilter без активных фильтров
     */
    fun clear(): ClientFilter {
        return ClientFilter()
    }
    
    /**
     * Очищает все фильтры кроме поискового запроса
     *
     * @return новый экземпляр ClientFilter только с поисковым запросом
     */
    fun clearFiltersKeepSearch(): ClientFilter {
        return this.copy(
            rentalTypes = emptySet(),
            familyCompositions = emptySet(),
            urgencyLevels = emptySet(),
            propertyTypes = emptySet(),
            peopleCountRanges = emptySet(),
            districts = emptySet(),
            minLongTermBudget = null,
            maxLongTermBudget = null,
            minShortTermBudget = null,
            maxShortTermBudget = null
        )
    }
    
    /**
     * Получает количество активных фильтров
     *
     * @return количество активных фильтров
     */
    fun getActiveFiltersCount(): Int {
        var count = 0
        
        if (rentalTypes.isNotEmpty()) count++
        if (familyCompositions.isNotEmpty()) count++
        if (urgencyLevels.isNotEmpty()) count++
        if (propertyTypes.isNotEmpty()) count++
        if (peopleCountRanges.isNotEmpty()) count++
        if (districts.isNotEmpty()) count++
        if (minLongTermBudget != null || maxLongTermBudget != null) count++
        if (minShortTermBudget != null || maxShortTermBudget != null) count++
        if (searchQuery.isNotEmpty()) count++
        
        return count
    }
} 