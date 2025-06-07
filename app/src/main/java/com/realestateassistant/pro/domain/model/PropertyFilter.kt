package com.realestateassistant.pro.domain.model

/**
 * Модель фильтров для объектов недвижимости
 *
 * @param propertyTypes набор типов недвижимости для фильтрации
 * @param minRooms минимальное количество комнат
 * @param maxRooms максимальное количество комнат
 * @param minPrice минимальная цена
 * @param maxPrice максимальная цена
 * @param districts набор районов для фильтрации
 * @param searchQuery строка поискового запроса
 */
data class PropertyFilter(
    val propertyTypes: Set<String> = emptySet(),
    val minRooms: Int? = null,
    val maxRooms: Int? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val districts: Set<String> = emptySet(),
    val searchQuery: String = ""
) {
    /**
     * Проверяет, есть ли активные фильтры
     * 
     * @return true если хотя бы один фильтр активен, false если нет активных фильтров
     */
    fun hasActiveFilters(): Boolean {
        return propertyTypes.isNotEmpty() || 
               minRooms != null || 
               maxRooms != null || 
               minPrice != null || 
               maxPrice != null || 
               districts.isNotEmpty() ||
               searchQuery.isNotEmpty()
    }
    
    /**
     * Проверяет, есть ли активные фильтры, кроме поискового запроса
     *
     * @return true если хотя бы один фильтр (кроме поискового запроса) активен
     */
    fun hasActiveFiltersBesideSearch(): Boolean {
        return propertyTypes.isNotEmpty() || 
               minRooms != null || 
               maxRooms != null || 
               minPrice != null || 
               maxPrice != null || 
               districts.isNotEmpty()
    }
    
    /**
     * Очищает все фильтры и возвращает новый экземпляр
     *
     * @return новый экземпляр PropertyFilter без активных фильтров
     */
    fun clear(): PropertyFilter {
        return PropertyFilter()
    }
    
    /**
     * Очищает все фильтры, сохраняя поисковый запрос
     *
     * @return новый экземпляр PropertyFilter только с поисковым запросом
     */
    fun clearFiltersKeepSearch(): PropertyFilter {
        return PropertyFilter(searchQuery = this.searchQuery)
    }
    
    /**
     * Возвращает количество активных фильтров
     *
     * @return количество активных фильтров
     */
    fun getActiveFiltersCount(): Int {
        return propertyTypes.size +
            (if (minRooms != null) 1 else 0) +
            (if (maxRooms != null) 1 else 0) +
            (if (minPrice != null) 1 else 0) +
            (if (maxPrice != null) 1 else 0) +
            districts.size +
            (if (searchQuery.isNotEmpty()) 1 else 0)
    }
} 