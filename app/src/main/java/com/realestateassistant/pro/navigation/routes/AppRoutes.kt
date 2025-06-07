package com.realestateassistant.pro.navigation.routes

/**
 * Основные маршруты приложения
 */
object AppRoutes {
    // Основные экраны
    const val DASHBOARD = "dashboard"
    const val PROPERTIES = "properties"
    const val ADD_PROPERTY = "add_property"
    const val PROPERTY_DETAIL = "property_detail/{propertyId}"
    const val EDIT_PROPERTY = "edit_property/{propertyId}"
    const val CLIENTS = "clients"
    const val ADD_CLIENT = "add_client"
    const val CLIENT_DETAIL = "client_detail/{clientId}"
    const val EDIT_CLIENT = "edit_client/{clientId}"
    const val APPOINTMENTS = "appointments"
    const val ADD_APPOINTMENT = "add_appointment"
    const val APPOINTMENT_DETAIL = "appointment_detail/{appointmentId}"
    const val APPOINTMENT_EDIT = "appointment_edit/{appointmentId}"
    const val NOTIFICATIONS = "notifications"
    
    // Дополнительные экраны
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val ABOUT = "about"
    
    // Функции для создания маршрутов с параметрами для объектов недвижимости
    fun propertyDetail(propertyId: String) = "property_detail/$propertyId"
    fun editProperty(propertyId: String) = "edit_property/$propertyId"
    
    // Функции для создания маршрутов с параметрами для клиентов
    fun clientDetail(clientId: String) = "client_detail/$clientId"
    fun editClient(clientId: String) = "edit_client/$clientId"
    
    // Функции для создания маршрутов с параметрами для встреч
    fun appointmentDetail(appointmentId: String) = "appointment_detail/$appointmentId"
    fun appointmentEdit(appointmentId: String) = "appointment_edit/$appointmentId"
} 