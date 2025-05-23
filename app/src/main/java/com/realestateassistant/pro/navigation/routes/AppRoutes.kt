package com.realestateassistant.pro.navigation.routes

/**
 * Основные маршруты приложения
 */
object AppRoutes {
    // Основные экраны
    const val PROPERTIES = "properties"
    const val ADD_PROPERTY = "add_property"
    const val PROPERTY_DETAIL = "property_detail/{propertyId}"
    const val EDIT_PROPERTY = "edit_property/{propertyId}"
    const val CLIENTS = "clients"
    const val APPOINTMENTS = "appointments"
    
    // Дополнительные экраны
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val ABOUT = "about"
    
    // Функции для создания маршрутов с параметрами
    fun propertyDetail(propertyId: String) = "property_detail/$propertyId"
    fun editProperty(propertyId: String) = "edit_property/$propertyId"
} 