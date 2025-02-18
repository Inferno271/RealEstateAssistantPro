package com.realestateassistant.pro.domain.repository

import com.realestateassistant.pro.domain.model.Appointment

interface AppointmentRepository {
    /**
     * Создает новую встречу/показ.
     * @param appointment Данные встречи для создания
     * @return Result с созданной встречей (включая сгенерированный id) при успехе
     */
    suspend fun createAppointment(appointment: Appointment): Result<Appointment>

    /**
     * Обновляет существующую встречу/показ.
     * @param appointment Обновленные данные встречи
     * @return Result с Unit при успехе
     */
    suspend fun updateAppointment(appointment: Appointment): Result<Unit>

    /**
     * Удаляет встречу/показ по id.
     * @param appointmentId Идентификатор встречи для удаления
     * @return Result с Unit при успехе
     */
    suspend fun deleteAppointment(appointmentId: String): Result<Unit>

    /**
     * Получает встречу/показ по id.
     * @param appointmentId Идентификатор встречи
     * @return Result с данными встречи при успехе
     */
    suspend fun getAppointment(appointmentId: String): Result<Appointment>

    /**
     * Получает все встречи/показы.
     * @return Result со списком всех встреч при успехе
     */
    suspend fun getAllAppointments(): Result<List<Appointment>>

    /**
     * Получает встречи/показы для конкретного объекта недвижимости.
     * @param propertyId Идентификатор объекта недвижимости
     * @return Result со списком встреч для указанного объекта при успехе
     */
    suspend fun getAppointmentsByProperty(propertyId: String): Result<List<Appointment>>

    /**
     * Получает встречи/показы для конкретного клиента.
     * @param clientId Идентификатор клиента
     * @return Result со списком встреч для указанного клиента при успехе
     */
    suspend fun getAppointmentsByClient(clientId: String): Result<List<Appointment>>

    /**
     * Получает встречи/показы на определенную дату.
     * @param date Дата в формате timestamp (начало дня)
     * @return Result со списком встреч на указанную дату при успехе
     */
    suspend fun getAppointmentsByDate(date: Long): Result<List<Appointment>>

    /**
     * Получает встречи/показы за период времени.
     * @param startDate Начало периода в формате timestamp
     * @param endDate Конец периода в формате timestamp
     * @return Result со списком встреч за указанный период при успехе
     */
    suspend fun getAppointmentsByDateRange(startDate: Long, endDate: Long): Result<List<Appointment>>
} 