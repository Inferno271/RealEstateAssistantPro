package com.realestateassistant.pro.domain.usecase.dashboard

import com.realestateassistant.pro.domain.model.Appointment
import com.realestateassistant.pro.domain.model.AppointmentStatus
import com.realestateassistant.pro.domain.model.AppointmentType
import com.realestateassistant.pro.domain.model.dashboard.*
import com.realestateassistant.pro.domain.repository.AppointmentRepository
import com.realestateassistant.pro.domain.repository.ClientRepository
import com.realestateassistant.pro.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/**
 * UseCase для получения данных для экрана панели управления
 *
 * В текущей версии содержит только информацию о быстрых действиях и предстоящих событиях
 */
class GetDashboardSummaryUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val clientRepository: ClientRepository,
    private val propertyRepository: PropertyRepository
) {
    /**
     * Получает данные для экрана панели управления
     *
     * @return Flow с объектом DashboardSummary
     */
    operator fun invoke(): Flow<DashboardSummary> {
        // Получаем предстоящие встречи
        val upcomingAppointmentsFlow = appointmentRepository.observeUpcomingAppointments()
            .map { appointments -> convertToUpcomingEvents(appointments) }
        
        // Создаем быстрые действия
        val quickActions = createQuickActions()
        
        // Комбинируем данные в один DashboardSummary
        return upcomingAppointmentsFlow.map { upcomingEvents ->
            DashboardSummary(
                quickActions = quickActions,
                upcomingEvents = upcomingEvents
            )
        }
    }
    
    /**
     * Конвертирует список встреч в список предстоящих событий для Dashboard
     */
    private fun convertToUpcomingEvents(appointments: List<Appointment>): List<UpcomingEvent> {
        val today = LocalDate.now()
        val todayMillis = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val tomorrowMillis = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return appointments
            .sortedBy { it.startTime }
            .take(5) // Ограничиваем количество отображаемых встреч
            .map { appointment ->
                // Определяем, является ли встреча сегодняшней
                val isToday = appointment.startTime >= todayMillis && appointment.startTime < tomorrowMillis
                UpcomingEvent(appointment, isToday)
            }
    }
    
    /**
     * Создает список быстрых действий для панели управления
     */
    private fun createQuickActions(): List<QuickAction> {
        return listOf(
            QuickAction(
                id = "new_property",
                title = "Новый объект",
                iconResId = android.R.drawable.ic_menu_add,
                route = "add_property"
            ),
            QuickAction(
                id = "new_client",
                title = "Новый клиент",
                iconResId = android.R.drawable.ic_menu_my_calendar,
                route = "add_client"
            ),
            QuickAction(
                id = "new_appointment",
                title = "Новая встреча",
                iconResId = android.R.drawable.ic_menu_my_calendar,
                route = "add_appointment"
            ),
            QuickAction(
                id = "calendar",
                title = "Календарь",
                iconResId = android.R.drawable.ic_menu_today,
                route = "calendar"
            )
        )
    }
}

/**
 * Extension функция для Property, которая определяет, активен ли объект недвижимости
 * В реальном приложении здесь была бы реальная логика
 */
private fun com.realestateassistant.pro.domain.model.Property.isActive(): Boolean {
    // Объект считается активным, если он имеет заполненные основные поля и фотографии
    return id.isNotEmpty() && 
           address.isNotEmpty() && 
           propertyType.isNotEmpty() && 
           photos.isNotEmpty()
}

/**
 * Extension функция для Property, которая определяет, полностью ли заполнен профиль объекта
 * В реальном приложении здесь была бы реальная логика
 */
private fun com.realestateassistant.pro.domain.model.Property.isComplete(): Boolean {
    // Проверяем основные обязательные поля для объекта недвижимости
    val hasBasicInfo = id.isNotEmpty() && 
                      address.isNotEmpty() && 
                      propertyType.isNotEmpty() && 
                      district.isNotEmpty() &&
                      area > 0 &&
                      roomsCount > 0
    
    // Проверяем контактную информацию
    val hasContactInfo = contactName.isNotEmpty() && contactPhone.isNotEmpty()
    
    // Проверяем наличие фотографий
    val hasPhotos = photos.isNotEmpty()
    
    // Проверяем специфичные поля в зависимости от типа аренды
    val hasRentalInfo = if (dailyPrice != null) {
        // Посуточная аренда
        checkInTime != null && checkOutTime != null && maxGuests != null
    } else if (monthlyRent != null) {
        // Длительная аренда
        minRentPeriod != null
    } else {
        false
    }
    
    return hasBasicInfo && hasContactInfo && hasPhotos && hasRentalInfo
}

/**
 * Extension функция для Client, которая определяет, полностью ли заполнен профиль клиента
 * В реальном приложении здесь была бы реальная логика
 */
private fun com.realestateassistant.pro.domain.model.Client.isProfileComplete(): Boolean {
    // Проверяем основные обязательные поля для клиента
    val hasBasicInfo = id.isNotEmpty() && 
                      fullName.isNotEmpty() && 
                      phone.isNotEmpty()
    
    // Проверяем основную информацию о потребностях клиента
    val hasRentalPreferences = rentalType.isNotEmpty()
    
    // Проверяем специфичные поля в зависимости от типа аренды
    val hasTypeSpecificInfo = if (rentalType.equals("длительная", ignoreCase = true)) {
        // Для длительной аренды
        longTermBudgetMin != null && longTermBudgetMax != null && 
        desiredPropertyType != null && desiredRoomsCount != null
    } else if (rentalType.equals("посуточная", ignoreCase = true)) {
        // Для посуточной аренды
        shortTermBudgetMin != null && shortTermBudgetMax != null && 
        (shortTermCheckInDate != null || shortTermCheckOutDate != null)
    } else {
        false
    }
    
    // Дополнительные сведения о клиенте
    val hasAdditionalInfo = peopleCount != null && 
                           (familyComposition != null || 
                            hasPets != null || 
                            preferredDistrict != null)
    
    return hasBasicInfo && hasRentalPreferences && hasTypeSpecificInfo && hasAdditionalInfo
} 