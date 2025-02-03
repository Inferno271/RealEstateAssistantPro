package com.realestateassistant.pro.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер базы данных Firebase Realtime Database.
 * Предоставляет доступ к различным узлам базы данных:
 * - Объекты недвижимости
 * - Клиенты
 * - Встречи и показы
 * - Данные пользователей
 */
@Singleton
class FirebaseDatabaseManager @Inject constructor() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    
    /**
     * Возвращает ссылку на узел с объектами недвижимости
     * @return DatabaseReference для работы с объектами недвижимости
     */
    fun getPropertiesReference(): DatabaseReference = database.getReference("properties")
    
    /**
     * Возвращает ссылку на узел с данными клиентов
     * @return DatabaseReference для работы с клиентами
     */
    fun getClientsReference(): DatabaseReference = database.getReference("clients")
    
    /**
     * Возвращает ссылку на узел с данными встреч и показов
     * @return DatabaseReference для работы с встречами
     */
    fun getAppointmentsReference(): DatabaseReference = database.getReference("appointments")
    
    /**
     * Возвращает ссылку на узел с данными конкретного пользователя
     * @param userId Идентификатор пользователя
     * @return DatabaseReference для работы с данными пользователя
     */
    fun getUserReference(userId: String): DatabaseReference = database.getReference("users").child(userId)
} 