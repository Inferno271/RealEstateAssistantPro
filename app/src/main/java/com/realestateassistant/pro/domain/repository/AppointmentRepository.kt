package com.realestateassistant.pro.domain.repository

import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import kotlinx.coroutines.flow.Flow

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
     * Наблюдает за всеми встречами/показами.
     * @return Flow со списком всех встреч
     */
    fun observeAllAppointments(): Flow<List<Appointment>>

    /**
     * Получает встречи/показы для конкретного объекта недвижимости.
     * @param propertyId Идентификатор объекта недвижимости
     * @return Result со списком встреч для указанного объекта при успехе
     */
    suspend fun getAppointmentsByProperty(propertyId: String): Result<List<Appointment>>

    /**
     * Наблюдает за встречами/показами для конкретного объекта недвижимости.
     * @param propertyId Идентификатор объекта недвижимости
     * @return Flow со списком встреч для указанного объекта
     */
    fun observeAppointmentsByProperty(propertyId: String): Flow<List<Appointment>>

    /**
     * Получает встречи/показы для конкретного клиента.
     * @param clientId Идентификатор клиента
     * @return Result со списком встреч для указанного клиента при успехе
     */
    suspend fun getAppointmentsByClient(clientId: String): Result<List<Appointment>>

    /**
     * Наблюдает за встречами/показами для конкретного клиента.
     * @param clientId Идентификатор клиента
     * @return Flow со списком встреч для указанного клиента
     */
    fun observeAppointmentsByClient(clientId: String): Flow<List<Appointment>>

    /**
     * Получает встречи/показы на определенную дату.
     * @param date Дата в формате timestamp (начало дня)
     * @return Result со списком встреч на указанную дату при успехе
     */
    suspend fun getAppointmentsByDate(date: Long): Result<List<Appointment>>

    /**
     * Наблюдает за встречами/показами на определенную дату.
     * @param date Дата в формате timestamp (начало дня)
     * @return Flow со списком встреч на указанную дату
     */
    fun observeAppointmentsByDate(date: Long): Flow<List<Appointment>>

    /**
     * Получает встречи/показы за период времени.
     * @param startDate Начало периода в формате timestamp
     * @param endDate Конец периода в формате timestamp
     * @return Result со списком встреч за указанный период при успехе
     */
    suspend fun getAppointmentsByDateRange(startDate: Long, endDate: Long): Result<List<Appointment>>

    /**
     * Наблюдает за встречами/показами за период времени.
     * @param startDate Начало периода в формате timestamp
     * @param endDate Конец периода в формате timestamp
     * @return Flow со списком встреч за указанный период
     */
    fun observeAppointmentsByDateRange(startDate: Long, endDate: Long): Flow<List<Appointment>>

    /**
     * Получает встречи/показы с определенным статусом.
     * @param status Статус встречи
     * @return Result со списком встреч с указанным статусом при успехе
     */
    suspend fun getAppointmentsByStatus(status: AppointmentStatus): Result<List<Appointment>>

    /**
     * Наблюдает за встречами/показами с определенным статусом.
     * @param status Статус встречи
     * @return Flow со списком встреч с указанным статусом
     */
    fun observeAppointmentsByStatus(status: AppointmentStatus): Flow<List<Appointment>>

    /**
     * Получает встречи/показы определенного типа.
     * @param type Тип встречи
     * @return Result со списком встреч указанного типа при успехе
     */
    suspend fun getAppointmentsByType(type: AppointmentType): Result<List<Appointment>>

    /**
     * Наблюдает за встречами/показами определенного типа.
     * @param type Тип встречи
     * @return Flow со списком встреч указанного типа
     */
    fun observeAppointmentsByType(type: AppointmentType): Flow<List<Appointment>>

    /**
     * Получает количество встреч/показов на определенную дату.
     * @param date Дата в формате timestamp (начало дня)
     * @return Result с количеством встреч на указанную дату при успехе
     */
    suspend fun getAppointmentsCountForDay(date: Long): Result<Int>

    /**
     * Проверяет наличие пересечений по времени с другими встречами.
     * @param startTime Время начала встречи
     * @param endTime Время окончания встречи
     * @param excludeId ID встречи, которую нужно исключить из проверки (например, при редактировании)
     * @return Result со списком пересекающихся встреч при успехе
     */
    suspend fun getOverlappingAppointments(startTime: Long, endTime: Long, excludeId: String = ""): Result<List<Appointment>>

    /**
     * Поиск встреч/показов по заголовку или описанию.
     * @param query Поисковый запрос
     * @return Flow со списком найденных встреч
     */
    fun searchAppointments(query: String): Flow<List<Appointment>>

    /**
     * Получает предстоящие встречи/показы.
     * @return Flow со списком предстоящих встреч
     */
    fun observeUpcomingAppointments(): Flow<List<Appointment>>

    /**
     * Получает встречи/показы для календаря по месяцам.
     * @param year Год
     * @param month Месяц (1-12)
     * @return Flow со списком встреч для указанного месяца
     */
    fun observeAppointmentsForMonth(year: Int, month: Int): Flow<List<Appointment>>

    /**
     * Получает встречи/показы для календаря по неделям.
     * @param startOfWeek Начало недели в формате timestamp
     * @param endOfWeek Конец недели в формате timestamp
     * @return Flow со списком встреч для указанной недели
     */
    fun observeAppointmentsForWeek(startOfWeek: Long, endOfWeek: Long): Flow<List<Appointment>>
} 