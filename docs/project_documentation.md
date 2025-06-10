# Документация проекта "RealEstateAssistant PRO"

## Содержание

1. [Общее описание приложения](#общее-описание-приложения)
2. [Архитектура проекта](#архитектура-проекта)
3. [Модули и основные компоненты](#модули-и-основные-компоненты)
4. [Модель данных](#модель-данных)
5. [Управление состоянием](#управление-состоянием)
6. [Пользовательский интерфейс](#пользовательский-интерфейс)
7. [Работа с изображениями](#работа-с-изображениями)
8. [Работа с документами](#работа-с-документами)
9. [Интеграция с картами](#интеграция-с-картами)
10. [Безопасность и шифрование данных](#безопасность-и-шифрование-данных)
11. [Управление зависимостями](#управление-зависимостями)
12. [Навигация](#навигация)
13. [Бронирования и календарь](#бронирования-и-календарь)
14. [Технический долг и планы развития](#технический-долг-и-планы-развития)

## Общее описание приложения

**RealEstateAssistant PRO** - это профессиональное мобильное приложение для риелторов и агентов недвижимости, разработанное для эффективного управления объектами недвижимости, клиентами, встречами и бронированиями. Приложение предоставляет полноценное решение для ведения бизнеса в сфере недвижимости, включая поддержку как долгосрочной, так и посуточной аренды.

### Основные функции

- Создание и редактирование карточек объектов недвижимости с детальной информацией
- Управление фотографиями и документами для объектов недвижимости
- Ведение базы данных клиентов с детальной информацией о предпочтениях
- Планирование и отслеживание встреч и просмотров объектов
- Управление бронированиями с календарём занятости
- Быстрый поиск и фильтрация объектов и клиентов
- Панель управления с аналитикой и ключевыми показателями
- Система рекомендаций объектов для клиентов

## Архитектура проекта

Проект реализован с использованием архитектурного паттерна **Clean Architecture** и **MVVM** (Model-View-ViewModel). Приложение разделено на следующие основные слои:

### Слои приложения

1. **Presentation layer (UI)**
   - `presentation/screens` - экраны приложения с Jetpack Compose
   - `presentation/components` - переиспользуемые UI-компоненты
   - `presentation/viewmodels` - ViewModel-компоненты для связи UI и бизнес-логики
   - `presentation/state` - состояния UI для различных экранов
   - `presentation/theme` - темизация приложения (Material 3)

2. **Domain layer**
   - `domain/model` - бизнес-модели данных
   - `domain/usecase` - интерактеры (юзкейсы) для бизнес-логики
   - `domain/repository` - интерфейсы репозиториев

3. **Data layer**
   - `data/repository` - имплементации репозиториев
   - `data/local` - источники данных (Room Database)
     - `data/local/dao` - объекты доступа к данным
     - `data/local/entity` - сущности базы данных
     - `data/local/converter` - конвертеры типов для Room
     - `data/local/database` - класс базы данных
     - `data/local/security` - шифрование базы данных
   - `data/mapper` - преобразователи между различными моделями
   - `data/service` - сервисы для работы с внешними API

4. **Core layer**
   - `core/analytics` - аналитика и мониторинг
   - `core/date` - утилиты для работы с датами
   - `core/error` - обработка ошибок
   - `core/file` - работа с файловой системой
   - `core/image` - обработка изображений
   - `core/localization` - локализация
   - `core/logging` - логирование
   - `core/navigation` - настройки навигации
   - `core/network` - сетевые компоненты
   - `core/permission` - управление разрешениями
   - `core/resource` - доступ к ресурсам
   - `core/result` - обертка результатов операций
   - `core/security` - безопасность и шифрование
   - `core/settings` - настройки приложения
   - `core/state` - управление состоянием
   - `core/theme` - базовые компоненты темы
   - `core/util` - общие утилиты
   - `core/validation` - валидация данных
   - `core/viewmodel` - базовый функционал ViewModel

### Инфраструктура

- **Dependency Injection**: Dagger Hilt
- **UI Framework**: Jetpack Compose
- **Persistence**: Room Database с шифрованием SQLCipher
- **Асинхронность**: Kotlin Coroutines и Flow
- **Обработка изображений**: Coil
- **Навигация**: Jetpack Navigation Compose
- **Геолокация и карты**: Yandex MapKit

## Модули и основные компоненты

### Модуль недвижимости

#### Модели
- `Property` - модель данных объекта недвижимости
- `PropertyEntity` - сущность базы данных для объекта недвижимости
- `PropertyFormState` - состояние формы редактирования/создания объекта
- `PropertyTypeCharacteristics` - определяет характеристики для разных типов недвижимости

#### Use Cases
- `AddPropertyUseCase` - добавление нового объекта
- `UpdatePropertyUseCase` - обновление объекта
- `DeletePropertyUseCase` - удаление объекта
- `GetPropertyUseCase` - получение детальной информации об объекте
- `GetAllPropertiesUseCase` - получение списка всех объектов
- `ObservePropertiesUseCase` - наблюдение за списком объектов в реальном времени

#### Репозитории
- `PropertyRepository` - интерфейс репозитория для работы с объектами
- `PropertyRepositoryImpl` - имплементация репозитория

#### UI компоненты
- `PropertyListScreen` - экран списка объектов недвижимости
- `PropertyDetailScreen` - экран детальной информации об объекте
- `AddPropertyScreen` - экран добавления нового объекта
- `EditPropertyScreen` - экран редактирования объекта
- `PropertyCard` - компонент для отображения карточки объекта в списке
- `PropertyFilter` - компонент для фильтрации списка объектов
- `PhotoGallery` - компонент для отображения галереи фотографий

### Модуль клиентов

#### Модели
- `Client` - модель данных клиента
- `ClientEntity` - сущность базы данных для клиента
- `ClientFormState` - состояние формы редактирования/создания клиента

#### Use Cases
- `AddClientUseCase` - добавление нового клиента
- `UpdateClientUseCase` - обновление данных клиента
- `DeleteClientUseCase` - удаление клиента
- `GetClientUseCase` - получение информации о клиенте
- `GetAllClientsUseCase` - получение списка всех клиентов
- `GetClientsByRentalTypeUseCase` - получение клиентов по типу аренды

#### Репозитории
- `ClientRepository` - интерфейс репозитория для работы с клиентами
- `ClientRepositoryImpl` - имплементация репозитория

#### UI компоненты
- `ClientListScreen` - экран списка клиентов
- `ClientDetailScreen` - экран детальной информации о клиенте
- `AddClientScreen` - экран добавления нового клиента
- `EditClientScreen` - экран редактирования клиента
- `ClientCard` - компонент карточки клиента
- `ClientSearchBar` - компонент поиска клиентов

### Модуль встреч

#### Модели
- `Appointment` - модель данных встречи
- `AppointmentEntity` - сущность базы данных для встречи
- `AppointmentStatus` - статусы встреч (запланирована, завершена, отменена)
- `AppointmentType` - типы встреч (показ, осмотр, подписание, другое)

#### Use Cases
- `CreateAppointmentUseCase` - создание новой встречи
- `UpdateAppointmentUseCase` - обновление встречи
- `DeleteAppointmentUseCase` - удаление встречи
- `GetAppointmentUseCase` - получение детальной информации о встрече
- `GetAppointmentsByDateUseCase` - получение встреч на определенную дату
- `ObserveAppointmentsByDateRangeUseCase` - наблюдение за встречами в периоде

#### Репозитории
- `AppointmentRepository` - интерфейс репозитория для работы с встречами
- `AppointmentRepositoryImpl` - имплементация репозитория

#### UI компоненты
- `AppointmentScreen` - экран списка встреч
- `AppointmentDetailScreen` - экран детальной информации о встрече
- `AddAppointmentScreen` - экран добавления новой встречи
- `EditAppointmentScreen` - экран редактирования встречи
- `CalendarView` - календарь для выбора даты встречи

### Модуль бронирований

#### Модели
- `Booking` - модель данных бронирования
- `BookingEntity` - сущность базы данных для бронирования
- `BookingStatus` - статусы бронирования (ожидание, подтверждено, отменено)
- `PaymentStatus` - статусы оплаты (не оплачено, частично оплачено, полностью оплачено)
- `AdditionalService` - дополнительные услуги для бронирования

#### Use Cases
- `AddBookingUseCase` - добавление нового бронирования
- `UpdateBookingUseCase` - обновление бронирования с проверкой конфликтов дат (исключая текущее бронирование)
- `DeleteBookingUseCase` - удаление бронирования
- `GetBookingUseCase` - получение детальной информации о бронировании
- `HasBookingConflictsUseCase` - проверка на конфликты дат бронирования
- `ObserveBookingsByPropertyUseCase` - наблюдение за бронированиями объекта

#### Репозитории
- `BookingRepository` - интерфейс репозитория для работы с бронированиями
  - `hasBookingConflicts` - проверка конфликтов для нового бронирования
  - `hasBookingConflictsExcludingBooking` - проверка конфликтов при редактировании (исключая текущее бронирование)
- `BookingRepositoryImpl` - имплементация репозитория

#### UI компоненты
- `BookingCalendarScreen` - экран календаря бронирований
- `BookingListScreen` - экран списка бронирований
- `BookingDetailDialog` - диалог с деталями бронирования
- `BookingFormDialog` - диалог для создания/редактирования бронирования

#### UpdateBookingUseCase

```kotlin
class UpdateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    /**
     * Обновляет существующее бронирование с проверкой на конфликты
     * При проверке конфликтов дат исключает текущее бронирование, чтобы избежать 
     * ложного обнаружения конфликта с самим собой при редактировании
     * 
     * @param booking Бронирование для обновления
     * @return Result с успехом или ошибкой
     */
    suspend operator fun invoke(booking: Booking): Result<Unit> {
        // Получаем текущее бронирование для проверки
        val currentBookingResult = bookingRepository.getBooking(booking.id)
        if (currentBookingResult.isFailure) {
            return Result.failure(currentBookingResult.exceptionOrNull() 
                ?: IllegalStateException("Не удалось найти бронирование для обновления"))
        }
        
        val currentBooking = currentBookingResult.getOrThrow()
        
        // Если даты изменились, проверяем наличие конфликтов
        if (currentBooking.startDate != booking.startDate || currentBooking.endDate != booking.endDate) {
            val hasConflicts = bookingRepository.hasBookingConflictsExcludingBooking(
                propertyId = booking.propertyId,
                fromDate = booking.startDate,
                toDate = booking.endDate,
                excludeBookingId = booking.id
            ).getOrNull() ?: false
            
            // Если есть конфликты, возвращаем ошибку
            if (hasConflicts) {
                return Result.failure(IllegalStateException("Выбранные даты уже заняты для этого объекта недвижимости"))
            }
        }
        
        // Если нет конфликтов, обновляем бронирование
        return bookingRepository.updateBooking(booking)
    }
}
```

### BookingRepository

Репозиторий бронирований предоставляет два метода для проверки конфликтов дат:

```kotlin
interface BookingRepository {
    // ... существующие методы ...
    
    /**
     * Проверяет, есть ли конфликты бронирований для указанного объекта в указанном диапазоне дат
     * Возвращает true, если есть конфликты
     */
    suspend fun hasBookingConflicts(propertyId: String, fromDate: Long, toDate: Long): Result<Boolean>
    
    /**
     * Проверяет, есть ли конфликты бронирований для указанного объекта в указанном диапазоне дат,
     * исключая указанное бронирование (для редактирования)
     * Возвращает true, если есть конфликты
     */
    suspend fun hasBookingConflictsExcludingBooking(propertyId: String, fromDate: Long, toDate: Long, excludeBookingId: String): Result<Boolean>
    
    // ... другие методы ...
}
```

### BookingDao

DAO для работы с бронированиями предоставляет SQL-запросы для проверки конфликтов:

```kotlin
interface BookingDao {
    // ... существующие методы ...
    
    /**
     * Проверяет, есть ли конфликты бронирований для указанного объекта в указанном диапазоне дат
     * Возвращает количество конфликтующих бронирований
     */
    @Query("SELECT COUNT(*) FROM bookings WHERE propertyId = :propertyId AND status != 'CANCELLED' AND status != 'EXPIRED' AND ((startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate) OR (:fromDate BETWEEN startDate AND endDate))")
    suspend fun countBookingConflicts(propertyId: String, fromDate: Long, toDate: Long): Int
    
    /**
     * Проверяет, есть ли конфликты бронирований для указанного объекта в указанном диапазоне дат,
     * исключая указанное бронирование по id (для редактирования)
     * Возвращает количество конфликтующих бронирований
     */
    @Query("SELECT COUNT(*) FROM bookings WHERE propertyId = :propertyId AND id != :excludeBookingId AND status != 'CANCELLED' AND status != 'EXPIRED' AND ((startDate BETWEEN :fromDate AND :toDate) OR (endDate BETWEEN :fromDate AND :toDate) OR (:fromDate BETWEEN startDate AND endDate))")
    suspend fun countBookingConflictsExcludingBooking(propertyId: String, fromDate: Long, toDate: Long, excludeBookingId: String): Int
    
    // ... другие методы ...
}
```

### Модуль изображений

#### Модели и компоненты
- `ImageRepository` - интерфейс для работы с изображениями
- `ImageRepositoryImpl` - реализация репозитория изображений
- `ImageUseCases` - юзкейсы для работы с изображениями
- `CoilUtils` - утилиты для оптимизации загрузки изображений
- Компоненты UI для отображения и управления изображениями

### Модуль документов

#### Модели и компоненты
- `DocumentRepository` - интерфейс для работы с документами
- `DocumentRepositoryImpl` - реализация репозитория документов
- `DocumentUseCases` - юзкейсы для работы с документами
- `DocumentViewModel` - ViewModel для управления документами
- `DocumentItem` - UI компонент для отображения документа в списке

### Модуль панели управления

#### Модели и компоненты
- `DashboardSummary` - модель данных для сводки на панели управления
- `DashboardState` - состояние UI для экрана панели управления
- `GetDashboardSummaryUseCase` - получение данных для панели управления
- `DashboardViewModel` - ViewModel для управления панелью
- `DashboardScreen` - главный экран с панелью управления
- `StatisticCard` - компонент для отображения статистических данных

## Модель данных

### Property (Объект недвижимости)

```kotlin
data class Property(
    val id: String = "",
    
    // Контактная информация
    val contactName: String = "",
    val contactPhone: List<String> = emptyList(),
    val additionalContactInfo: String? = null,
    
    // Общая информация
    val propertyType: String = "",
    val address: String = "",
    val district: String = "",
    val nearbyObjects: List<String> = emptyList(),
    val views: List<String> = emptyList(),
    val area: Double = 0.0,
    val roomsCount: Int = 0,
    val isStudio: Boolean = false,
    val layout: String = "",
    val floor: Int = 0,
    val totalFloors: Int = 0,
    val description: String? = null,
    val management: List<String> = emptyList(),
    
    // Координаты для карты
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Поля для разных типов недвижимости
    val levelsCount: Int = 0,
    val landArea: Double = 0.0,
    val hasGarage: Boolean = false,
    val garageSpaces: Int = 0,
    val hasBathhouse: Boolean = false,
    val hasPool: Boolean = false,
    val poolType: String = "",
    
    // Характеристики объекта
    val repairState: String = "",
    val bedsCount: Int? = null,
    val bathroomsCount: Int? = null,
    val bathroomType: String = "",
    val noFurniture: Boolean = false,
    val hasAppliances: Boolean = false,
    val heatingType: String = "",
    val balconiesCount: Int = 0,
    val elevatorsCount: Int? = null,
    val hasParking: Boolean = false,
    val parkingType: String? = null,
    val parkingSpaces: Int? = null,
    val amenities: List<String> = emptyList(),
    val smokingAllowed: Boolean = false,
    
    // Условия проживания
    val childrenAllowed: Boolean = false,
    val minChildAge: Int? = null,
    val maxChildrenCount: Int? = null,
    val petsAllowed: Boolean = false,
    val maxPetsCount: Int? = null,
    val allowedPetTypes: List<String> = emptyList(),
    
    // Для долгосрочной аренды
    val monthlyRent: Double? = null,
    val winterMonthlyRent: Double? = null,
    val summerMonthlyRent: Double? = null,
    val utilitiesIncluded: Boolean = false,
    val utilitiesCost: Double? = null,
    val minRentPeriod: String? = null,
    val maxRentPeriod: Int? = null,
    val depositMonths: String? = null,
    val depositCustomAmount: Double? = null,
    
    // Для посуточной аренды
    val dailyPrice: Double? = null,
    val minStayDays: Int? = null,
    val maxStayDays: Int? = null,
    val maxGuests: Int? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val shortTermDeposit: Double? = null,
    val seasonalPrices: List<SeasonalPrice> = emptyList(),
    
    // Дополнительная информация
    val photos: List<String> = emptyList(),
    val documents: List<String> = emptyList(),
    
    // Метаданные
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Client (Клиент)

```kotlin
data class Client(
    val id: String = "",
    val fullName: String = "",
    val phone: List<String> = emptyList(),
    val email: String? = null,
    val rentalType: String = "", // "long_term", "short_term", "both"
    val propertyTypeInterests: List<String> = emptyList(),
    val districtInterests: List<String> = emptyList(),
    val priceRange: PriceRange? = null,
    val minArea: Double? = null,
    val minRooms: Int? = null,
    val additionalRequirements: String? = null,
    val source: String? = null, // Откуда пришел клиент
    val status: String = "active", // active, inactive, deal_completed
    val notes: String? = null,
    
    // Информация о клиенте
    val familyComposition: String? = null,
    val peopleCount: Int? = null,
    val childrenCount: Int? = null,
    val childrenAges: List<Int> = emptyList(),
    val hasPets: Boolean = false,
    val petTypes: List<String> = emptyList(),
    val petCount: Int? = null,
    val occupation: String? = null,
    val isSmokingClient: Boolean = false,
    
    // Дополнительные предпочтения
    val preferredAmenities: List<String> = emptyList(),
    val preferredViews: List<String> = emptyList(),
    val preferredNearbyObjects: List<String> = emptyList(),
    val preferredRepairState: String? = null,
    val preferredFloorMin: Int? = null,
    val preferredFloorMax: Int? = null,
    val needsElevator: Boolean? = null,
    val preferredBalconiesCount: Int? = null,
    val preferredBathroomsCount: Int? = null,
    val preferredBathroomType: String? = null,
    val needsFurniture: Boolean? = null,
    val needsAppliances: Boolean? = null,
    val preferredHeatingType: String? = null,
    val needsParking: Boolean? = null,
    val preferredParkingType: String? = null,
    
    // Метаданные
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Ценовой диапазон для клиента
 */
data class PriceRange(
    val min: Double = 0.0,
    val max: Double = 0.0
)
```

### Appointment (Встреча)

```kotlin
data class Appointment(
    val id: String = "",
    val propertyId: String = "",
    val clientId: String = "",
    val agentId: String? = null,
    val title: String = "",
    val description: String? = null,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val type: AppointmentType = AppointmentType.SHOWING,
    val location: String? = null,
    val notes: String? = null,
    val reminderTime: Long? = null,
    val isAllDay: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    val color: String? = null,
    val attachments: List<String> = emptyList(),
    val participants: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    RESCHEDULED,
    NO_SHOW
}

enum class AppointmentType {
    SHOWING,
    INSPECTION,
    SIGNING,
    OTHER
}
```

### Booking (Бронирование)

```kotlin
data class Booking(
    val id: String = "",
    
    // Связи с другими сущностями
    val propertyId: String,
    val clientId: String? = null, // Может быть null для предварительных бронирований
    
    // Общие поля для обоих типов аренды
    val startDate: Long, // Дата заезда/начала аренды
    val endDate: Long, // Дата выезда/окончания аренды
    val status: BookingStatus = BookingStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val totalAmount: Double, // Общая сумма бронирования
    val depositAmount: Double? = null, // Сумма депозита
    val notes: String? = null, // Дополнительные примечания
    
    // Поля для посуточной аренды
    val guestsCount: Int? = null, // Количество гостей
    val checkInTime: String? = null, // Время заезда
    val checkOutTime: String? = null, // Время выезда
    val includedServices: List<String> = emptyList(), // Включенные услуги
    val additionalServices: List<AdditionalService> = emptyList(), // Дополнительные услуги
    
    // Поля для долгосрочной аренды
    val rentPeriodMonths: Int? = null, // Срок аренды в месяцах
    val monthlyPaymentAmount: Double? = null, // Ежемесячный платеж
    val utilityPayments: Boolean? = null, // Включены ли коммунальные платежи
    val contractType: String? = null, // Тип договора
    
    // Метаданные
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    NO_SHOW
}

enum class PaymentStatus {
    UNPAID,
    PARTIALLY_PAID,
    FULLY_PAID,
    REFUNDED,
    DEPOSIT_PAID
}

data class AdditionalService(
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val isIncluded: Boolean = false
)
```



### SeasonalPrice (Сезонная цена)

```kotlin
data class SeasonalPrice(
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val price: Double = 0.0
)
```

### PropertyId (Идентификатор объекта)

```kotlin
data class PropertyId(
    val id: String
)
```

## Управление состоянием

Управление состоянием в приложении реализовано с использованием ViewModel из библиотеки Jetpack и StateFlow/SharedFlow из Kotlin Coroutines. Это обеспечивает реактивное обновление UI при изменении данных.

### Базовая структура состояния

Для каждого экрана или функционального модуля создаются отдельные классы состояния, которые содержат все необходимые данные для отображения UI.

```kotlin
data class PropertyState(
    val property: Property? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

Для более сложных экранов используются составные состояния, содержащие все аспекты UI:

```kotlin
data class BookingCalendarState(
    val selectedPropertyId: String? = null,
    val bookings: List<Booking> = emptyList(),
    val clients: List<Client> = emptyList(),
    val selectedBooking: Booking? = null,
    val selectedStartDate: LocalDate? = null,
    val selectedEndDate: LocalDate? = null,
    val selectedClient: Client? = null,
    val isSelectionMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookingDialogVisible: Boolean = false,
    val isInfoMode: Boolean = true,
    val availableTimeSlots: List<AvailableTimeSlot> = emptyList(),
    val allBookings: List<Booking> = emptyList(),
    val filteredBookings: List<Booking> = emptyList(),
    val allProperties: List<PropertyId> = emptyList()
)
```

### События UI

Для обработки пользовательских действий используются Sealed классы событий:

```kotlin
sealed class BookingCalendarEvent {
    data class SelectProperty(val propertyId: String) : BookingCalendarEvent()
    data class SelectDate(val date: LocalDate) : BookingCalendarEvent()
    data class SelectClient(val client: Client) : BookingCalendarEvent()
    data class SelectBooking(val booking: Booking) : BookingCalendarEvent()
    data class UpdateDates(val start: LocalDate, val end: LocalDate) : BookingCalendarEvent()
    data class CreateBooking(
        val propertyId: String,
        val clientId: String?,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val amount: Double,
        val guestsCount: Int = 1,
        val notes: String? = null
    ) : BookingCalendarEvent()
    // ... другие события
}
```

### Основные ViewModel компоненты

#### PropertyViewModel

```kotlin
@HiltViewModel
class PropertyViewModel @Inject constructor(
    private val propertyUseCases: PropertyUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(PropertyState())
    val state: StateFlow<PropertyState> = _state.asStateFlow()
    
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    
    init {
        savedStateHandle.get<String>("propertyId")?.let { propertyId ->
            if (propertyId.isNotBlank()) {
                getProperty(propertyId)
            }
        }
    }
    
    fun onEvent(event: PropertyEvent) {
        when(event) {
            is PropertyEvent.SaveProperty -> {
                viewModelScope.launch {
                    // Логика сохранения объекта
                }
            }
            is PropertyEvent.DeleteProperty -> {
                viewModelScope.launch {
                    // Логика удаления объекта
                }
            }
            // Обработка других событий
        }
    }
    
    private fun getProperty(propertyId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            propertyUseCases.getPropertyUseCase(propertyId)
                .onSuccess { property ->
                    _state.update { 
                        it.copy(
                            property = property,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Не удалось загрузить объект"
                        )
                    }
                }
        }
    }
    
    // Другие методы для работы с объектами недвижимости
}
```

#### BookingCalendarViewModel

```kotlin
@HiltViewModel
class BookingCalendarViewModel @Inject constructor(
    private val bookingUseCases: BookingUseCases,
    private val clientUseCases: ClientUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(BookingCalendarState())
    val state: StateFlow<BookingCalendarState> = _state.asStateFlow()
    
    init {
        loadClients()
        loadAllBookings()
        updateBookingStatusesAutomatically()
        
        // Загрузка начального propertyId из аргументов
        savedStateHandle.get<String>("propertyId")?.let { propertyId ->
            if (propertyId.isNotBlank()) {
                handleEvent(BookingCalendarEvent.SelectProperty(propertyId))
            }
        }
    }
    
    fun handleEvent(event: BookingCalendarEvent) {
        when (event) {
            is BookingCalendarEvent.SelectProperty -> selectProperty(event.propertyId)
            is BookingCalendarEvent.SelectDate -> selectDate(event.date)
            is BookingCalendarEvent.UpdateDates -> updateDates(event.start, event.end)
            is BookingCalendarEvent.CreateBooking -> createBooking(
                event.propertyId,
                event.clientId,
                event.startDate,
                event.endDate,
                event.amount,
                event.guestsCount,
                event.notes
            )
            // Обработка других событий
        }
    }
    
    // Методы для работы с бронированиями
}
```

#### ClientViewModel

```kotlin
@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientUseCases: ClientUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(ClientState())
    val state: StateFlow<ClientState> = _state.asStateFlow()
    
    init {
        savedStateHandle.get<String>("clientId")?.let { clientId ->
            if (clientId.isNotBlank()) {
                getClient(clientId)
            }
        } ?: run {
            loadAllClients()
        }
    }
    
    // Методы для работы с клиентами
}
```

#### AppointmentViewModel

```kotlin
@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _state = MutableStateFlow(AppointmentState())
    val state: StateFlow<AppointmentState> = _state.asStateFlow()
    
    init {
        savedStateHandle.get<String>("appointmentId")?.let { appointmentId ->
            if (appointmentId.isNotBlank()) {
                getAppointment(appointmentId)
            }
        }
    }
    
    // Методы для работы с встречами
}
```

#### DashboardViewModel

```kotlin
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        getDashboardSummaryUseCase()
            .onStart {
                _state.update { it.copy(isLoading = true) }
            }
            .onEach { dashboardSummary ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        dashboardSummary = dashboardSummary,
                        error = null
                    )
                }
            }
            .catch { e ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки данных"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
```

#### ImageViewModel и DocumentViewModel

Эти ViewModel предоставляют методы для работы с изображениями и документами соответственно, оставаясь при этом достаточно простыми:

```kotlin
@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val documentUseCases: DocumentUseCases
) : ViewModel() {
    
    suspend fun saveDocument(uri: Uri): Result<String> {
        return documentUseCases.saveDocument(uri)
    }
    
    suspend fun openDocument(path: String): Result<Pair<Uri, String?>> {
        return documentUseCases.openDocument(path)
    }
    
    suspend fun deleteDocument(path: String): Result<Unit> {
        return documentUseCases.deleteDocument(path)
    }
}
```

## Пользовательский интерфейс

Пользовательский интерфейс приложения полностью реализован с использованием Jetpack Compose - современного декларативного инструментария для разработки UI на Android.

### Основная структура UI

Приложение использует Material 3 для дизайна и имеет навигационную структуру с боковым меню (Drawer) и основным навигационным контейнером:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoutes.DASHBOARD
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Получаем текущий маршрут для отображения заголовка
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Определяем заголовок экрана
    val screenTitle = when {
        currentRoute == AppRoutes.DASHBOARD -> "Панель управления"
        currentRoute == AppRoutes.PROPERTIES -> "Объекты недвижимости"
        // ... другие заголовки
        else -> "Real Estate Assistant Pro"
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(screenTitle) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Меню"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues),
                drawerState = drawerState
            )
        }
    }
}
```

### Основные экраны

#### DashboardScreen

```kotlin
@Composable
fun DashboardScreen(
    navController: NavHostController,
    drawerState: DrawerState?,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Отображение статистики
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state.error != null) {
            ErrorView(
                errorMessage = state.error!!,
                onRetry = { viewModel.loadDashboardData() }
            )
        } else {
            // Отображение карточек с ключевыми показателями
            DashboardContent(
                dashboardSummary = state.dashboardSummary,
                onNavigateToProperties = {
                    navController.navigate(AppRoutes.PROPERTIES)
                },
                onNavigateToClients = {
                    navController.navigate(AppRoutes.CLIENTS)
                },
                onNavigateToAppointments = {
                    navController.navigate(AppRoutes.APPOINTMENTS)
                }
            )
        }
    }
}
```

#### PropertyListScreen

```kotlin
@Composable
fun PropertyListScreen(
    onNavigateToAddProperty: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    drawerState: DrawerState?,
    viewModel: PropertyListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProperty
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить объект"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Фильтры для объектов
            PropertyFilterTabs(
                selectedFilter = state.selectedFilter,
                onFilterSelected = { filter ->
                    viewModel.onEvent(PropertyListEvent.FilterSelected(filter))
                }
            )
            
            // Список объектов
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (state.error != null) {
                ErrorView(
                    errorMessage = state.error!!,
                    onRetry = {
                        viewModel.onEvent(PropertyListEvent.LoadProperties)
                    }
                )
            } else if (state.properties.isEmpty()) {
                EmptyView(
                    message = "Нет объектов недвижимости",
                    icon = Icons.Default.Home,
                    onActionClick = onNavigateToAddProperty,
                    actionText = "Добавить объект"
                )
            } else {
                LazyColumn {
                    items(state.properties) { property ->
                        PropertyCard(
                            property = property,
                            onClick = {
                                onNavigateToPropertyDetail(property.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
```

#### BookingCalendarScreen

```kotlin
@Composable
fun BookingCalendarScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    viewModel: BookingCalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(propertyId) {
        viewModel.handleEvent(BookingCalendarEvent.SelectProperty(propertyId))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Календарь бронирований") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // UI для календаря бронирований
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Компонент календаря
                BookingCalendarView(
                    bookings = state.bookings,
                    selectedStartDate = state.selectedStartDate,
                    selectedEndDate = state.selectedEndDate,
                    onDateSelected = { date ->
                        viewModel.handleEvent(BookingCalendarEvent.SelectDate(date))
                    },
                    onDateRangeConfirmed = {
                        viewModel.handleEvent(BookingCalendarEvent.ConfirmDateSelection)
                    }
                )
                
                // Если выбрано бронирование, показываем его детали
                state.selectedBooking?.let { booking ->
                    BookingDetailCard(
                        booking = booking,
                        onEditClick = {
                            viewModel.handleEvent(BookingCalendarEvent.SwitchToEditMode)
                        },
                        onDeleteClick = {
                            viewModel.handleEvent(BookingCalendarEvent.DeleteBooking(booking.id))
                        }
                    )
                }
                
                // Кнопка для создания нового бронирования
                Button(
                    onClick = {
                        viewModel.handleEvent(BookingCalendarEvent.ShowBookingDialog)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Создать бронирование")
                }
            }
        }
        
        // Диалог создания/редактирования бронирования
        if (state.isBookingDialogVisible) {
            BookingFormDialog(
                propertyId = propertyId,
                startDate = state.selectedStartDate,
                endDate = state.selectedEndDate,
                clients = state.clients,
                selectedClient = state.selectedClient,
                editing = !state.isInfoMode,
                booking = state.selectedBooking,
                onClientSelected = { client ->
                    viewModel.handleEvent(BookingCalendarEvent.SelectClient(client))
                },
                onSave = { clientId, startDate, endDate, amount, guestsCount, notes ->
                    if (state.selectedBooking != null && !state.isInfoMode) {
                        viewModel.handleEvent(
                            BookingCalendarEvent.UpdateBooking(
                                state.selectedBooking!!.id,
                                clientId,
                                startDate,
                                endDate,
                                amount,
                                guestsCount,
                                notes
                            )
                        )
                    } else {
                        viewModel.handleEvent(
                            BookingCalendarEvent.CreateBooking(
                                propertyId,
                                clientId,
                                startDate,
                                endDate,
                                amount,
                                guestsCount,
                                notes
                            )
                        )
                    }
                },
                onDismiss = {
                    viewModel.handleEvent(BookingCalendarEvent.HideBookingDialog)
                }
            )
        }
    }
}
```

### Повторно используемые компоненты

#### PropertyCard

```kotlin
@Composable
fun PropertyCard(
    property: Property,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Миниатюра первого изображения объекта, если есть
            if (property.photos.isNotEmpty()) {
                AsyncImage(
                    model = property.photos.first(),
                    contentDescription = property.address,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                // Заглушка, если нет изображений
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Нет изображения",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Адрес объекта
            Text(
                text = property.address,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Информация о районе
            Text(
                text = property.district,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Основные характеристики
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Тип объекта
                PropertyFeatureChip(
                    icon = Icons.Default.Home,
                    text = property.propertyType
                )
                
                // Площадь
                PropertyFeatureChip(
                    icon = Icons.Default.SquareFoot,
                    text = "${property.area} м²"
                )
                
                // Количество комнат
                PropertyFeatureChip(
                    icon = Icons.Default.Door,
                    text = if (property.isStudio) "Студия" else "${property.roomsCount} комн."
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Цена
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (property.monthlyRent != null) {
                    Text(
                        text = "${property.monthlyRent} ₽/мес",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (property.dailyPrice != null) {
                    Text(
                        text = "${property.dailyPrice} ₽/сутки",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
```

#### ClientCard

```kotlin
@Composable
fun ClientCard(
    client: Client,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар клиента
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = client.fullName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Имя клиента
                Text(
                    text = client.fullName,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Телефон
                if (client.phone.isNotEmpty()) {
                    Text(
                        text = client.phone.first(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Тип аренды
                val rentalTypeText = when (client.rentalType) {
                    "long_term" -> "Длительная аренда"
                    "short_term" -> "Посуточная аренда"
                    "both" -> "Длительная и посуточная аренда"
                    else -> "Тип аренды не указан"
                }
                
                Text(
                    text = rentalTypeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            // Статус клиента
            ClientStatusChip(status = client.status)
        }
    }
}
```

#### AppointmentCard

```kotlin
@Composable
fun AppointmentCard(
    appointment: Appointment,
    property: Property?,
    client: Client?,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок и статус
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                AppointmentStatusChip(status = appointment.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Дата и время
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Время",
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                
                val startDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(appointment.startTime),
                    ZoneId.systemDefault()
                )
                val endDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(appointment.endTime),
                    ZoneId.systemDefault()
                )
                
                Text(
                    text = "${startDateTime.format(dateFormatter)}, " +
                            "${startDateTime.format(timeFormatter)} - " +
                            "${endDateTime.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Информация об объекте
            property?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Объект",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = property.address,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Информация о клиенте
            client?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Клиент",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = client.fullName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Примечания
            appointment.notes?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
```

### Темизация

Приложение поддерживает светлую и темную темы с использованием Material 3, включая возможность адаптации к системным цветам на Android 12+ (dynamic colors):

```kotlin
private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    // ...остальные цвета
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    // ...остальные цвета
)

@Composable
fun RealEstateAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

## Работа с изображениями

В приложении реализована комплексная система работы с изображениями, которая включает:

### Репозиторий изображений

Интерфейс `ImageRepository` и его реализация `ImageRepositoryImpl` предоставляют методы для:
- Сохранения изображений с опциональным сжатием
- Загрузки изображений из файловой системы
- Удаления изображений
- Проверки валидности URI изображений
- Очистки кэша изображений

```kotlin
interface ImageRepository {
    suspend fun saveImage(uri: Uri, compress: Boolean = true, quality: Int = 80): Result<String>
    suspend fun loadImage(path: String): Result<Bitmap>
    suspend fun deleteImage(path: String): Result<Unit>
    suspend fun isValidImageUri(uri: Uri): Boolean
    suspend fun clearImageCache(): Result<Unit>
}
```

### Юзкейсы для работы с изображениями

- `SaveImage` - сохранение одного изображения
- `SaveImages` - сохранение нескольких изображений
- `LoadImage` - загрузка изображения
- `DeleteImage` - удаление изображения
- `ValidateImageUri` - проверка валидности URI
- `ClearImageCache` - очистка кэша изображений

```kotlin
data class ImageUseCases @Inject constructor(
    val saveImage: SaveImage,
    val saveImages: SaveImages,
    val loadImage: LoadImage,
    val deleteImage: DeleteImage,
    val validateImageUri: ValidateImageUri,
    val clearImageCache: ClearImageCache
)
```

### Оптимизация загрузки изображений

Класс `CoilUtils` содержит утилиты для оптимизации загрузки изображений с помощью библиотеки Coil:

```kotlin
object CoilUtils {
    fun createImageLoader(
        context: Context,
        diskCacheSize: Long = 100 * 1024 * 1024,
        memoryCacheSize: Int? = null
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(diskCacheSize)
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(memoryCacheSize?.toFloat() ?: 0.25f)
                    .build()
            }
            .respectCacheHeaders(false)
            .build()
    }
    
    fun createImageRequest(
        context: Context,
        data: Any,
        size: Size = Size.ORIGINAL,
        diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
        memoryCachePolicy: CachePolicy = CachePolicy.ENABLED
    ): ImageRequest.Builder {
        return ImageRequest.Builder(context)
            .data(data)
            .size(size)
            .diskCachePolicy(diskCachePolicy)
            .memoryCachePolicy(memoryCachePolicy)
    }
}
```

### UI компоненты для работы с изображениями

#### PhotoGallery

```kotlin
@Composable
fun PhotoGallery(
    photos: List<String>,
    modifier: Modifier = Modifier,
    onPhotoClick: (String) -> Unit = {}
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(photos) { photoPath ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoPath)
                    .crossfade(true)
                    .build(),
                contentDescription = "Фото объекта",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onPhotoClick(photoPath) }
            )
        }
    }
}
```

#### FullscreenPhotoViewer

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenPhotoViewer(
    photoPath: String,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0)
    
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Изображение на весь экран
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoPath)
                    .crossfade(true)
                    .build(),
                contentDescription = "Фото объекта",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            
            // Кнопка закрытия
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = Color.White
                )
            }
        }
    }
}
```

#### MediaSection

```kotlin
@Composable
fun MediaSection(
    formState: PropertyFormState,
    onFormStateChange: (PropertyFormState) -> Unit,
    expandedSections: MutableMap<PropertySection, Boolean>,
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isExpanded = expandedSections.getOrDefault(PropertySection.MEDIA, false)
    
    // Состояние для выбора изображений
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                try {
                    // Сохраняем изображения
                    val result = imageViewModel.saveImages(uris)
                    result.onSuccess { paths ->
                        // Обновляем состояние формы
                        onFormStateChange(
                            formState.copy(
                                photos = formState.photos + paths
                            )
                        )
                    }.onFailure { e ->
                        // Показываем ошибку
                        Toast.makeText(
                            context,
                            "Ошибка при сохранении изображений: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Ошибка при обработке изображений: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    // Состояние для выбора документов
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                try {
                    // Обрабатываем каждый документ отдельно
                    val documentViewModel: DocumentViewModel = hiltViewModel()
                    val savedDocuments = mutableListOf<String>()
                    
                    uris.forEach { uri ->
                        documentViewModel.saveDocument(uri).onSuccess { path ->
                            savedDocuments.add(path)
                        }
                    }
                    
                    if (savedDocuments.isNotEmpty()) {
                        onFormStateChange(
                            formState.copy(
                                documents = formState.documents + savedDocuments
                            )
                        )
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Ошибка при сохранении документов: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    SectionCard(
        title = "Медиафайлы",
        icon = Icons.Default.Photo,
        isExpanded = isExpanded,
        onExpandToggle = {
            expandedSections[PropertySection.MEDIA] = !isExpanded
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Секция фотографий
            Text(
                text = "Фотографии",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Предпросмотр фотографий
            if (formState.photos.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(formState.photos) { photoPath ->
                        PhotoThumbnail(
                            photoPath = photoPath,
                            onDeleteClick = {
                                onFormStateChange(
                                    formState.copy(
                                        photos = formState.photos - photoPath
                                    )
                                )
                                scope.launch {
                                    try {
                                        imageViewModel.deleteImage(photoPath)
                                    } catch (e: Exception) {
                                        // Игнорируем ошибки при удалении
                                    }
                                }
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Кнопка добавления фотографий
            Button(
                onClick = {
                    photoPickerLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Добавить фото"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Добавить фотографии")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Секция документов
            Text(
                text = "Документы",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Список документов
            if (formState.documents.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    formState.documents.forEachIndexed { index, documentPath ->
                        DocumentItem(
                            documentUri = documentPath,
                            onDeleteClick = {
                                onFormStateChange(
                                    formState.copy(
                                        documents = formState.documents - documentPath
                                    )
                                )
                                scope.launch {
                                    try {
                                        val documentViewModel: DocumentViewModel = hiltViewModel()
                                        documentViewModel.deleteDocument(documentPath)
                                    } catch (e: Exception) {
                                        // Игнорируем ошибки при удалении
                                    }
                                }
                            },
                            index = index + 1
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Кнопка добавления документов
            Button(
                onClick = {
                    documentPickerLauncher.launch("*/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Добавить документ"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Добавить документы")
            }
        }
    }
}
```

## Работа с документами

В приложении реализована система работы с документами, которая обеспечивает возможность сохранения, открытия и удаления различных типов документов для объектов недвижимости.

### Репозиторий документов

Интерфейс `DocumentRepository` и его реализация `DocumentRepositoryImpl` предоставляют методы для:
- Сохранения документов из внешних источников
- Получения URI документов для открытия во внешних приложениях
- Удаления документов
- Проверки валидности URI документов
- Определения MIME-типа документов

```kotlin
interface DocumentRepository {
    suspend fun saveDocument(uri: Uri): Result<String>
    suspend fun getDocumentUri(path: String): Result<Uri>
    suspend fun deleteDocument(path: String): Result<Unit>
    suspend fun isValidDocumentUri(uri: Uri): Boolean
    suspend fun getDocumentMimeType(path: String): String?
}
```

### Юзкейсы для работы с документами

```kotlin
data class DocumentUseCases @Inject constructor(
    val saveDocument: SaveDocument,
    val getDocumentUri: GetDocumentUri,
    val deleteDocument: DeleteDocument,
    val openDocument: OpenDocument
)

class SaveDocument @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(uri: Uri): Result<String> {
        return repository.saveDocument(uri)
    }
}

class OpenDocument @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(path: String): Result<Pair<Uri, String?>> {
        return repository.getDocumentUri(path).map { uri ->
            val mimeType = repository.getDocumentMimeType(path)
            uri to mimeType
        }
    }
}
```

### UI компоненты для работы с документами

#### DocumentItem

```kotlin
@Composable
fun DocumentItem(
    documentUri: String,
    onDeleteClick: () -> Unit,
    index: Int,
    documentViewModel: DocumentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка документа
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Документ",
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Название документа (или индекс)
            Text(
                text = "Документ $index",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            // Кнопка открытия документа
            IconButton(
                onClick = {
                    scope.launch {
                        documentViewModel.openDocument(documentUri).onSuccess { (uri, mimeType) ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, mimeType ?: "application/octet-stream")
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Не удалось открыть документ: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.onFailure { e ->
                            Toast.makeText(
                                context,
                                "Ошибка при открытии документа: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Открыть документ"
                )
            }
            
            // Кнопка удаления документа
            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить документ",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
```

## Интеграция с картами

В приложении реализована интеграция с картами для отображения местоположения объектов недвижимости, что повышает наглядность и удобство работы с географической информацией.

### Компоненты системы карт

#### PropertyMapView

```kotlin
@Composable
fun PropertyMapView(
    address: String,
    latitude: Double?,
    longitude: Double?,
    markerColor: Color = Color(0xFF2196F3),
    modifier: Modifier = Modifier,
    onLocationSelected: (Double, Double) -> Unit = { _, _ -> }
) {
    // Локальные состояния
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapInitialized by remember { mutableStateOf(false) }
    var currentLatitude by remember { mutableStateOf(latitude) }
    var currentLongitude by remember { mutableStateOf(longitude) }
    val scope = rememberCoroutineScope()
    
    // Эффект для инициализации MapKit
    DisposableEffect(Unit) {
        MapKitFactory.initialize(context)
        onDispose {
            mapView?.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
    
    // Эффект для автоматического геокодирования адреса
    LaunchedEffect(address) {
        if ((currentLatitude == null || currentLongitude == null) && address.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                YandexGeocoderService.getCoordinates(address)?.let { (lat, lon) ->
                    currentLatitude = lat
                    currentLongitude = lon
                    withContext(Dispatchers.Main) {
                        // Обновляем позицию на карте
                        mapView?.map?.let { map ->
                            val point = Point(lat, lon)
                            map.move(
                                CameraPosition(point, 15.0f, 0.0f, 0.0f),
                                Animation(Animation.Type.SMOOTH, 1f),
                                null
                            )
                        }
                        // Вызываем колбэк
                        onLocationSelected(lat, lon)
                    }
                }
            }
        }
    }
    
    // Рендеринг карты
    AndroidView(
        factory = { ctx ->
            // Создание и настройка MapView
            MapView(ctx).apply {
                mapView = this
                this.onStart()
                MapKitFactory.getInstance().onStart()
                
                val map = this.map
                
                // Настройка карты
                map.isRotateGesturesEnabled = false
                map.isZoomGesturesEnabled = true
                map.isTiltGesturesEnabled = false
                
                // Если координаты уже известны, центрируем карту на них
                if (currentLatitude != null && currentLongitude != null) {
                    val point = Point(currentLatitude!!, currentLongitude!!)
                    map.move(
                        CameraPosition(point, 15.0f, 0.0f, 0.0f)
                    )
                    
                    // Добавляем маркер
                    val imageProvider = ImageProvider.fromResource(
                        context,
                        R.drawable.map_pin
                    )
                    val placemark = map.mapObjects.addPlacemark().apply {
                        geometry = point
                        setIcon(imageProvider)
                    }
                }
                
                // Обработка тапов по карте
                map.addInputListener(object : InputListener {
                    override fun onMapTap(map: Map, point: Point) {
                        currentLatitude = point.latitude
                        currentLongitude = point.longitude
                        
                        // Обновляем маркер
                        map.mapObjects.clear()
                        val imageProvider = ImageProvider.fromResource(
                            context,
                            R.drawable.map_pin
                        )
                        val placemark = map.mapObjects.addPlacemark().apply {
                            geometry = point
                            setIcon(imageProvider)
                        }
                        
                        // Вызываем колбэк
                        onLocationSelected(point.latitude, point.longitude)
                    }
                    
                    override fun onMapLongTap(map: Map, point: Point) {
                        // Ничего не делаем
                    }
                })
                
                mapInitialized = true
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(8.dp)),
        update = { view ->
            // Обновление при изменении координат
            if (mapInitialized && currentLatitude != null && currentLongitude != null) {
                view.map.mapObjects.clear()
                val point = Point(currentLatitude!!, currentLongitude!!)
                
                // Добавляем маркер
                val imageProvider = ImageProvider.fromResource(
                    context,
                    R.drawable.map_pin
                )
                val placemark = view.map.mapObjects.addPlacemark().apply {
                    geometry = point
                    setIcon(imageProvider)
                }
            }
        }
    )
}
```

#### YandexGeocoderService

```kotlin
object YandexGeocoderService {
    private const val API_KEY = "80b06c04-6156-4dfd-a086-2cee0b05fdb8"
    private const val BASE_URL = "https://geocode-maps.yandex.ru/1.x/"
    
    // Кэш для геокодирования
    private val geocodeCache = HashMap<String, Pair<Double, Double>>()
    
    /**
     * Получает координаты (широта, долгота) по адресу
     * Возвращает пару (latitude, longitude) или null, если адрес не найден
     */
    suspend fun getCoordinates(address: String): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        // Проверяем кэш
        if (geocodeCache.containsKey(address)) {
            return@withContext geocodeCache[address]
        }
        
        // Формируем URL запроса
        val encodedAddress = URLEncoder.encode(address, "UTF-8")
        val url = "$BASE_URL?apikey=$API_KEY&format=json&geocode=$encodedAddress"
        
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                
                // Парсим ответ
                val jsonObject = JSONObject(response)
                val featureMember = jsonObject
                    .getJSONObject("response")
                    .getJSONObject("GeoObjectCollection")
                    .getJSONArray("featureMember")
                
                if (featureMember.length() > 0) {
                    val point = featureMember
                        .getJSONObject(0)
                        .getJSONObject("GeoObject")
                        .getJSONObject("Point")
                        .getString("pos")
                    
                    // В формате "долгота широта"
                    val coordinates = point.split(" ")
                    if (coordinates.size == 2) {
                        val longitude = coordinates[0].toDouble()
                        val latitude = coordinates[1].toDouble()
                        
                        // Сохраняем в кэш
                        geocodeCache[address] = Pair(latitude, longitude)
                        
                        return@withContext Pair(latitude, longitude)
                    }
                }
            }
            
            return@withContext null
        } catch (e: Exception) {
            Log.e("YandexGeocoderService", "Ошибка при геокодировании: ${e.message}")
            return@withContext null
        }
    }
}
```

## Навигация

Навигация в приложении реализована с использованием Jetpack Navigation Compose с боковым меню (Drawer) и вложенными графами навигации.

### Структура навигации

Основная структура навигации определена в классе `AppNavHost`:

```kotlin
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = AppRoutes.DASHBOARD,
    modifier: Modifier = Modifier,
    drawerState: DrawerState? = null
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Панель управления
        composable(route = AppRoutes.DASHBOARD) {
            DashboardScreen(
                navController = navController,
                drawerState = drawerState
            )
        }
        
        // Экраны объектов недвижимости
        composable(route = AppRoutes.PROPERTIES) {
            PropertyListScreen(
                onNavigateToAddProperty = {
                    navController.navigate(AppRoutes.ADD_PROPERTY)
                },
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(AppRoutes.propertyDetail(propertyId))
                },
                drawerState = drawerState
            )
        }
        
        composable(route = AppRoutes.ADD_PROPERTY) {
            AddPropertyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = AppRoutes.PROPERTY_DETAIL,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            PropertyDetailScreen(
                propertyId = propertyId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.navigate(AppRoutes.editProperty(id))
                },
                onNavigateToBookingCalendar = { id ->
                    navController.navigate(AppRoutes.bookingCalendar(id))
                }
            )
        }
        
        // Экраны клиентов
        composable(route = AppRoutes.CLIENTS) {
            ClientListScreen(
                onNavigateToAddClient = {
                    navController.navigate(AppRoutes.ADD_CLIENT)
                },
                onNavigateToClientDetail = { clientId ->
                    navController.navigate(AppRoutes.clientDetail(clientId))
                },
                drawerState = drawerState
            )
        }
        
        // ... остальные экраны клиентов
        
        // Экраны встреч
        composable(route = AppRoutes.APPOINTMENTS) {
            AppointmentScreen(
                onNavigateToAddAppointment = {
                    navController.navigate(AppRoutes.ADD_APPOINTMENT)
                },
                onNavigateToAppointmentDetail = { appointmentId ->
                    navController.navigate(AppRoutes.appointmentDetail(appointmentId))
                },
                drawerState = drawerState
            )
        }
        
        // ... остальные экраны встреч
        
        // Экраны бронирований
        composable(route = AppRoutes.BOOKINGS) {
            BookingListScreen(
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(AppRoutes.propertyDetail(propertyId))
                },
                onNavigateToClientDetail = { clientId ->
                    navController.navigate(AppRoutes.clientDetail(clientId))
                },
                onNavigateToBookingCalendar = { propertyId ->
                    navController.navigate(AppRoutes.bookingCalendar(propertyId))
                },
                drawerState = drawerState
            )
        }
        
        composable(
            route = AppRoutes.BOOKING_CALENDAR,
            arguments = listOf(
                navArgument("propertyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId") ?: ""
            BookingCalendarScreen(
                propertyId = propertyId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Экраны рекомендаций
        composable(
            route = AppRoutes.PROPERTY_RECOMMENDATIONS,
            arguments = listOf(
                navArgument("clientId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId") ?: ""
            PropertyRecommendationsScreen(
                clientId = clientId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPropertyDetail = { propertyId ->
                    navController.navigate(AppRoutes.propertyDetail(propertyId))
                }
            )
        }
        
        // Дополнительные экраны
        composable(route = AppRoutes.HELP) {
            HelpScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = AppRoutes.ABOUT) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
```

### Маршруты навигации

Все маршруты навигации определены в объекте `AppRoutes`:

```kotlin
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
    const val BOOKING_CALENDAR = "booking_calendar/{propertyId}"
    const val BOOKINGS = "bookings"
    const val PROPERTY_RECOMMENDATIONS = "property_recommendations/{clientId}"
    
    // Дополнительные экраны
    const val HELP = "help"
    const val ABOUT = "about"
    
    // Функции для создания маршрутов с параметрами для объектов недвижимости
    fun propertyDetail(propertyId: String) = "property_detail/$propertyId"
    fun editProperty(propertyId: String) = "edit_property/$propertyId"
    fun bookingCalendar(propertyId: String) = "booking_calendar/$propertyId"
    
    // Функции для создания маршрутов с параметрами для клиентов
    fun clientDetail(clientId: String) = "client_detail/$clientId"
    fun editClient(clientId: String) = "edit_client/$clientId"
    fun propertyRecommendations(clientId: String) = "property_recommendations/$clientId"
    
    // Функции для создания маршрутов с параметрами для встреч
    fun appointmentDetail(appointmentId: String) = "appointment_detail/$appointmentId"
    fun appointmentEdit(appointmentId: String) = "appointment_edit/$appointmentId"
}
```

### Боковое меню навигации

Боковое меню навигации (Drawer) реализовано в компоненте `AppDrawer`:

```kotlin
@Composable
fun AppDrawer(
    navController: NavHostController,
    onCloseDrawer: () -> Unit
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Шапка drawer с логотипом и информацией
        DrawerHeader()
        
        // Разделитель
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Основные пункты навигации
        NavigationItems.mainItems.forEach { item ->
            DrawerItem(
                item = item,
                isSelected = currentRoute == item.route,
                onItemClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(AppRoutes.DASHBOARD) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    onCloseDrawer()
                }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Разделитель
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        // Дополнительные пункты навигации (в нижней части drawer)
        NavigationItems.bottomItems.forEach { item ->
            DrawerItem(
                item = item,
                isSelected = currentRoute == item.route,
                onItemClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                    onCloseDrawer()
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
```

### Элементы навигации

Элементы навигации для бокового меню определены в объекте `NavigationItems`:

```kotlin
object NavigationItems {
    // Основные пункты меню
    val mainItems = listOf(
        NavigationItem(
            route = AppRoutes.DASHBOARD,
            title = "Панель управления",
            icon = Icons.Default.Dashboard,
            contentDescription = "Панель управления"
        ),
        NavigationItem(
            route = AppRoutes.PROPERTIES,
            title = "Объекты",
            icon = Icons.Default.Home,
            contentDescription = "Список объектов недвижимости"
        ),
        NavigationItem(
            route = AppRoutes.CLIENTS,
            title = "Клиенты",
            icon = Icons.Default.Person,
            contentDescription = "Список клиентов"
        ),
        NavigationItem(
            route = AppRoutes.BOOKINGS,
            title = "Бронирования",
            icon = Icons.Default.BookOnline,
            contentDescription = "Список бронирований"
        ),
        NavigationItem(
            route = AppRoutes.APPOINTMENTS,
            title = "Встречи",
            icon = Icons.Default.DateRange,
            contentDescription = "Список встреч"
        )
    )
    
    // Дополнительные пункты меню (нижняя часть drawer)
    val bottomItems = listOf(
        NavigationItem(
            route = AppRoutes.HELP,
            title = "Помощь",
            icon = Icons.Default.Help,
            contentDescription = "Помощь и инструкции"
        ),
        NavigationItem(
            route = AppRoutes.ABOUT,
            title = "О приложении",
            icon = Icons.Default.Info,
            contentDescription = "Информация о приложении"
        )
    )
}
```

## Бронирования и календарь

В приложении реализована система бронирования и управления календарем для объектов недвижимости.

### Модели бронирования

- `Booking` - модель данных бронирования
- `BookingEntity` - сущность базы данных для бронирования
- `BookingStatus` - статусы бронирования (ожидание, подтверждено, отменено)
- `PaymentStatus` - статусы оплаты (не оплачено, частично оплачено, полностью оплачено)
- `AdditionalService` - дополнительные услуги для бронирования

### Модели календаря

- `CalendarView` - компонент календаря для выбора даты
- `BookingCalendarScreen` - экран календаря бронирований с отображением адреса выбранного объекта в заголовке
- `BookingListScreen` - экран списка бронирований с фильтрацией и сортировкой

### Компоненты календаря

В календаре бронирований используются следующие компоненты:

```kotlin
@Composable
fun BookingCalendarCompat(
    bookings: List<Booking>,
    selectedDate: LocalDate? = null,
    selectedEndDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit,
    onBookingSelected: (Booking) -> Unit,
    modifier: Modifier = Modifier
)
```

Для удобства пользователей реализована возможность отображения адреса объекта недвижимости в заголовке экрана календаря:

```kotlin
// Отображение адреса объекта в заголовке экрана календаря бронирований
TopAppBar(
    title = { 
        Column {
            Text("Календарь бронирований") 
            val property = state.selectedProperty
            if (property != null) {
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    },
    navigationIcon = {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
        }
    }
)
```

### Улучшенная работа с объектами в бронировании

#### Поиск объектов в диалоге выбора

Для удобства работы с большим количеством объектов недвижимости реализован диалог с функцией поиска:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertySelectorDialog(
    properties: List<Property>,
    onPropertySelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Заголовок диалога
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Выберите объект",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Поле поиска
                var searchQuery by remember { mutableStateOf("") }
                val filteredProperties = remember(searchQuery, properties) {
                    if (searchQuery.isBlank()) {
                        properties
                    } else {
                        properties.filter { property ->
                            property.address.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск по адресу") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Очистить")
                            }
                        }
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Кнопка "Все объекты"
                OutlinedButton(
                    onClick = { 
                        onPropertySelected(null)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Все объекты")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Список объектов
                if (filteredProperties.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет объектов, соответствующих запросу",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredProperties) { property ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onPropertySelected(property.id)
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = property.address,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Цена
                                        val price = property.dailyPrice ?: property.monthlyRent
                                        if (price != null) {
                                            Text(
                                                text = "${price.toInt()} ₽" + 
                                                      if (property.dailyPrice != null) "/сутки" else "/месяц",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        // Площадь и комнаты
                                        Text(
                                            text = "${property.area} м² • ${property.roomsCount} комн.",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
```

Данный компонент позволяет:
- Осуществлять поиск объектов по адресу
- Выбирать опцию "Все объекты" для сброса фильтра
- Просматривать расширенную информацию об объекте (адрес, цена, площадь, количество комнат)
- Получать уведомление, если по поисковому запросу ничего не найдено

## Интеграция с картами Яндекс

Приложение интегрирует Yandex MapKit для отображения местоположения объектов недвижимости на карте. Интеграция осуществлена с использованием Jetpack Compose.

### Инициализация MapKit

В основном классе приложения инициализируется MapKit с использованием API-ключа:

```kotlin
// RealEstateApplication.kt
override fun onCreate() {
    // ...
    
    // Инициализация Yandex MapKit
    MapKitFactory.setApiKey("80b06c04-6156-4dfd-a086-2cee0b05fdb8")
    
    // ...
}
```

### Компоненты карты

#### PropertyMapView

Основной компонент для отображения объекта недвижимости на карте:

```kotlin
@Composable
fun PropertyMapView(
    address: String,
    latitude: Double?,
    longitude: Double?,
    markerColor: Color = Color(0xFF2196F3),
    modifier: Modifier = Modifier
) {
    // Реализация компонента с Yandex MapKit
}
```

#### Геокодирование адресов

Для преобразования адресов в координаты используется сервис геокодирования:

```kotlin
object YandexGeocoderService {
    private const val API_KEY = "80b06c04-6156-4dfd-a086-2cee0b05fdb8"
    private const val BASE_URL = "https://geocode-maps.yandex.ru/1.x/"
    
    // Кэш для геокодирования
    private val geocodeCache = HashMap<String, Pair<Double, Double>>()
    
    /**
     * Получает координаты (широта, долгота) по адресу
     * Возвращает пару (latitude, longitude) или null, если адрес не найден
     */
    suspend fun getCoordinates(address: String): Pair<Double, Double>? {
        // ... реализация геокодирования
    }
}
```

### Интеграция с объектами недвижимости

При создании и редактировании объектов недвижимости карта помогает визуализировать местоположение и уточнять координаты:

```kotlin
// При изменении адреса автоматически выполняется геокодирование
LaunchedEffect(address) {
    if (property.latitude == null && property.longitude == null && address.isNotEmpty()) {
        try {
            val coordinates = YandexGeocoderService.getCoordinates(address)
            coordinates?.let {
                latitude = it.first
                longitude = it.second
            }
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }
}
```

## Экспорт данных в PDF

Приложение поддерживает экспорт данных об объектах недвижимости в формат PDF для создания отчетов и коммерческих предложений.

### Архитектура системы экспорта PDF

#### PdfExportService

Основной сервис для формирования PDF-документов:

```kotlin
@Singleton
class PdfExportService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Формирует содержимое PDF-документа с данными объекта недвижимости
     */
    fun drawPropertyContent(document: PdfDocument, property: Property, images: List<Bitmap>) {
        // ... реализация генерации PDF
    }
    
    /**
     * Рисует шапку документа
     */
    private fun drawHeader(canvas: Canvas, title: String, date: String, startY: Float, pageNumber: Int): Float {
        // ... реализация отрисовки шапки
    }
    
    // Другие вспомогательные методы
}
```

#### ExportPropertyToPdfUseCase

Use case для экспорта объекта недвижимости в PDF:

```kotlin
class ExportPropertyToPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageRepository: ImageRepository,
    private val documentRepository: DocumentRepository,
    private val pdfExportService: PdfExportService,
    private val storageHelper: StorageHelper
) {
    /**
     * Генерирует PDF-документ с информацией о объекте недвижимости.
     *
     * @param property Объект недвижимости
     * @return Result, содержащий Uri для доступа к сгенерированному PDF файлу, либо ошибку
     */
    suspend fun invoke(property: Property): Result<Uri> = withContext(Dispatchers.IO) {
        // ... реализация экспорта в PDF
    }
    
    /**
     * Генерирует имя файла на основе характеристик объекта
     */
    private fun generateFileName(property: Property): String {
        // ... генерация имени файла
    }
}
```

#### PdfExportViewModel

ViewModel для управления экспортом из UI:

```kotlin
@HiltViewModel
class PdfExportViewModel @Inject constructor(
    private val exportPropertyToPdfUseCase: ExportPropertyToPdfUseCase
) : ViewModel() {
    
    /**
     * Состояния процесса экспорта PDF
     */
    sealed class ExportState {
        object Initial : ExportState()
        object Loading : ExportState()
        data class Success(val uri: Uri) : ExportState()
        data class Error(val message: String) : ExportState()
    }
    
    // Состояние экспорта
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Initial)
    val exportState: StateFlow<ExportState> = _exportState
    
    /**
     * Экспортирует информацию об объекте недвижимости в PDF
     *
     * @param property Объект недвижимости для экспорта
     */
    fun exportPropertyToPdf(property: Property) {
        // ... инициирование экспорта и обновление состояний
    }
}
```

### Структура PDF-документа

Сгенерированный PDF-документ содержит:

1. Шапку с логотипом, датой и номером страницы
2. Общую информацию об объекте (адрес, тип, площадь, цена)
3. Фотографии объекта недвижимости
4. Подробные характеристики объекта
5. Дополнительную информацию (описание, заметки)
6. Контактную информацию агента
7. Подвал с QR-кодом для быстрого доступа

### Поддержка платформ и разрешений

Экспорт PDF поддерживает различные версии Android:
- Для Android 10+ используется API MediaStore для сохранения в общую директорию Downloads
- Для более ранних версий используются стандартные методы сохранения файлов
- Имеется встроенный менеджер разрешений для запроса доступа к хранилищу

### Предоставление доступа к PDF

После генерации PDF файла приложение предлагает пользователю открыть его:

```kotlin
fun openPdfFile(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    
    val chooserIntent = Intent.createChooser(intent, "Открыть PDF с помощью")
    chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(chooserIntent)
}
```

### Улучшенное открытие PDF и работа с уведомлениями

В приложении реализован надежный механизм открытия PDF файлов, в том числе из уведомлений, что решает проблему "device has no application to view" при попытке открыть файл:

```kotlin
/**
 * Открывает сгенерированный PDF файл, используя наиболее совместимый подход
 */
fun openPdf(context: Context, uri: Uri) {
    Log.d("PdfExportViewModel", "Пытаемся открыть PDF: $uri")
    
    // Пробуем разные подходы последовательно
    if (!tryOpenWithExplicitChooser(context, uri) && 
        !tryOpenWithGenericIntent(context, uri) &&
        !tryOpenWithSendAction(context, uri)) {
        
        // Если все методы не сработали, сообщаем пользователю
        Toast.makeText(
            context,
            "На устройстве не найдено приложение для просмотра PDF. Установите PDF-ридер.",
            Toast.LENGTH_LONG
        ).show()
    }
}

/**
 * Пытается открыть PDF с явным выбором приложения
 */
private fun tryOpenWithExplicitChooser(context: Context, uri: Uri): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooserIntent = Intent.createChooser(intent, "Открыть PDF с помощью")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        
        context.startActivity(chooserIntent)
        Log.d("PdfExportViewModel", "PDF открыт через явный выбор приложения")
        true
    } catch (e: Exception) {
        Log.e("PdfExportViewModel", "Ошибка при открытии PDF через выбор приложения", e)
        false
    }
}
```

Система также обеспечивает корректное открытие PDF файлов из уведомлений:

1. Используется FileProvider для предоставления доступа к файлам через провайдер контента
2. Добавлены корректные флаги для Intent: `FLAG_GRANT_READ_URI_PERMISSION`
3. Реализована проверка наличия приложений для открытия PDF
4. Добавлена иерархия запасных методов открытия файлов

#### Конфигурация FileProvider

В AndroidManifest.xml настроен FileProvider для безопасного доступа к PDF файлам:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

А в файле `file_paths.xml` определены доступные пути:

```xml
<paths>
    <external-path name="external_files" path="." />
    <external-files-path name="external_files_path" path="." />
    <cache-path name="cached_files" path="." />
    <files-path name="files" path="." />
    <external-cache-path name="external_cache" path="." />
</paths>
```

#### Взаимодействие с уведомлениями

При генерации PDF из уведомления:

1. Уведомление передает идентификатор объекта недвижимости
2. Система загружает данные объекта с помощью соответствующих репозиториев
3. Генерируется PDF с помощью `ExportPropertyToPdfUseCase`
4. Полученный URI (path) файла конвертируется в URI контент-провайдера
5. Intent для открытия PDF формируется с правильными флагами и типом

Это обеспечивает бесшовное взаимодействие между компонентами приложения и системными компонентами Android, гарантируя надежное открытие PDF файлов на всех поддерживаемых версиях Android.

## Система логирования и отладки

В приложении реализована комплексная система логирования и отладки, которая помогает обнаруживать и исправлять проблемы в различных средах выполнения.

### Timber для управления логами

Проект использует библиотеку Timber для упрощения управления логами:

```kotlin
// Инициализация Timber в Application классе
override fun onCreate() {
    // ...
    
    // Инициализация логирования с Timber
    if (DEBUG) {
        Timber.plant(Timber.DebugTree())
    }
}
```

Основные преимущества использования Timber:

1. Автоматическое определение тегов на основе имен классов
2. Отсутствие логов в релизных версиях
3. Простой синтаксис для логирования

```kotlin
// Пример использования Timber в репозитории
override suspend fun addBooking(booking: Booking): Result<Booking> {
    return try {
        val bookingEntity = BookingMapper.toEntity(booking)
        bookingDao.insertBooking(bookingEntity)
        Result.success(BookingMapper.toDomain(bookingEntity))
    } catch (e: Exception) {
        Timber.e(e, "Ошибка при добавлении бронирования")
        Result.failure(e)
    }
}
```

### StrictMode для выявления проблем производительности

Приложение использует StrictMode для выявления потенциальных проблем производительности и потоков во время разработки:

```kotlin
/**
 * Включает строгий режим для обнаружения блокировок основного потока и других проблем
 * Используется только в режиме отладки
 */
private fun enableStrictModeForDebug() {
    Log.d(TAG, "Включение StrictMode для отладки потоков")
    
    // Настройка политики для потоков - обнаруживает блокирующие операции в основном потоке
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .detectCustomSlowCalls()
            .permitDiskReads() // Разрешаем чтение с диска для предотвращения блокировок UI
            .penaltyLog() // Записываем нарушения в logcat
            .build()
    )
    
    // Настройка политики для виртуальной машины - обнаруживает утечки ресурсов
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .detectLeakedRegistrationObjects()
            .detectActivityLeaks()
            .detectCleartextNetwork()
            .detectContentUriWithoutPermission()
            .detectUnsafeIntentLaunch()
            .penaltyLog()
            .build()
    )
}
```

### Правила логирования

В приложении применяются следующие правила для логирования:

1. Использование префикса `REA_` для тегов логов для простоты фильтрации
2. Разделение логов по уровням: DEBUG, INFO, WARNING, ERROR
3. Логирование ключевых событий и операций:
   - Критические ошибки и исключения
   - Важные пользовательские действия
   - Время загрузки экранов и компонентов
   - Использование основных функций

### Конфиденциальность в логировании

Для обеспечения безопасности данных применяются следующие практики:

1. Никогда не логировать личные данные пользователей и клиентов
2. Анонимизировать идентификаторы в логах, когда это необходимо
3. Предоставлять возможность отключения сбора аналитических данных
4. Использовать обобщенные сообщения для логирования ошибок аутентификации

### Асинхронная инициализация компонентов

Для ускорения запуска приложения и предотвращения блокировки основного потока в проекте используется асинхронная инициализация тяжелых компонентов с помощью Jetpack App Startup:

```kotlin
/**
 * Инициализатор приложения для Jetpack App Startup.
 * Выполняет инициализацию базы данных и других критических компонентов
 * в фоновом потоке при запуске приложения.
 */
class ApplicationInitializer : Initializer<Unit> {
    private val initializerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun create(context: Context) {
        // Запуск инициализации в фоновом потоке
        initializerScope.launch {
            // Инициализация базы данных
            val entryPoint = EntryPointAccessors.fromApplication(
                context, 
                ApplicationInitializerEntryPoint::class.java
            )
            
            try {
                // Получение и инициализация компонентов
                val database = AppDatabase.getInstance(
                    context,
                    entryPoint.databaseEncryption()
                )
                
                // Проверка связи с базой данных
                database.openHelper.readableDatabase
                Log.d(TAG, "База данных успешно инициализирована")
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при инициализации базы данных", e)
                // Обработка ошибок инициализации
            }
        }
    }
    
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
```

Эти инструменты и практики обеспечивают стабильную и безопасную работу приложения, упрощают процесс отладки и позволяют оперативно выявлять потенциальные проблемы на этапе разработки.

## Технический долг и планы развития

### Технический долг

1. **Оптимизация производительности**
   - Оптимизация загрузки и кэширования изображений для более быстрого отображения
   - Внедрение пагинации для больших списков объектов и клиентов
   - Оптимизация главного экрана для улучшения скорости загрузки и уменьшения использования ресурсов

2. **Улучшение UI/UX**
   - Доработка отзывчивости UI для разных размеров экранов
   - Улучшение доступности приложения
   - Добавление анимаций для улучшения UX

3. **Тесты**
   - Покрытие основных компонентов UI тестами
   - Написание unit-тестов для репозиториев и юзкейсов
   - Интеграционные тесты для проверки работы базы данных

### Планы развития

1. **Разработка полноценных модулей для клиентов и показов**
   - Реализация полного функционала раздела клиентов по аналогии с объектами:
     - Создание детальной формы добавления/редактирования клиентов
     - Разработка удобных карточек клиентов для списка
     - Детальная страница с информацией о клиенте
     - Система связи клиентов с подходящими объектами
   - Разработка модуля показов/встреч:
     - Форма создания и редактирования показов
     - Календарь показов с разными представлениями
     - Карточки показов с возможностью быстрого доступа
     - Система уведомлений о предстоящих показах

2. **Новые функции**
   - Добавление аналитического модуля для отслеживания эффективности работы
   - Экспорт данных в различные форматы (PDF, Excel)

3. **Улучшение существующих функций**
   - Расширенный поиск и фильтрация объектов
   - Интеллектуальный поиск по всем сущностям приложения
   - Система тегов для объектов, клиентов и встреч
   - Продвинутая сортировка с возможностью настройки пользователем
   - Улучшенный календарь встреч с напоминаниями
   - Система заметок для объектов и клиентов
   - Чек-листы для просмотров объектов
   - Расширение функциональности карт с возможностью поиска объектов на карте

4. **Технические улучшения**
   - Реализация синхронизации данных с облаком (Supabase):
     - Настройка API для синхронизации
     - Механизм разрешения конфликтов при синхронизации
     - Возможность работы в офлайн-режиме с последующей синхронизацией
   - Внедрение многопользовательского режима
   - Улучшение безопасности и защиты данных

## Безопасность и шифрование данных

В приложении реализована система безопасности для защиты конфиденциальных данных пользователей и объектов недвижимости.

### Шифрование базы данных

Основой системы безопасности является шифрование локальной базы данных с использованием SQLCipher:

```kotlin
// Пример конфигурации шифрованной базы данных в AppDatabase
val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
val factory = SupportFactory(passphrase)

Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    DATABASE_NAME
)
.openHelperFactory(factory)
.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
.build()
```

### Компоненты безопасности

- `DatabaseEncryption` - класс для управления ключами шифрования и паролями базы данных
- Интеграция с Android Keystore для безопасного хранения криптографических ключей
- Механизмы восстановления при повреждении ключей шифрования

```kotlin
class DatabaseEncryption @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val KEY_ALIAS = "database_encryption_key"
        private const val PREF_NAME = "database_security_prefs"
        private const val PREF_PASSWORD = "database_password"
        private const val DEFAULT_PASSWORD = "default_secure_password"
    }
    
    private val keyStore by lazy {
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }
    
    private val securePreferences by lazy {
        EncryptedSharedPreferences.create(
            PREF_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    /**
     * Получает пароль для шифрования базы данных
     */
    fun getDatabasePassword(): String {
        // Если пароль уже сохранен, возвращаем его
        if (securePreferences.contains(PREF_PASSWORD)) {
            return securePreferences.getString(PREF_PASSWORD, DEFAULT_PASSWORD) ?: DEFAULT_PASSWORD
        }
        
        // Генерируем новый пароль и сохраняем его
        val password = generateSecurePassword()
        securePreferences.edit().putString(PREF_PASSWORD, password).apply()
        return password
    }
    
    /**
     * Генерирует криптографически стойкий пароль
     */
    private fun generateSecurePassword(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    /**
     * Сбрасывает пароль и ключи шифрования в случае проблем
     */
    fun resetSecurityKeys() {
        try {
            // Удаляем ключ из KeyStore
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
            
            // Сбрасываем пароль
            securePreferences.edit().remove(PREF_PASSWORD).apply()
            
            // Генерируем новый пароль
            val newPassword = generateSecurePassword()
            securePreferences.edit().putString(PREF_PASSWORD, newPassword).apply()
        } catch (e: Exception) {
            Log.e("DatabaseEncryption", "Ошибка при сбросе ключей безопасности", e)
            
            // В случае фатальной ошибки пытаемся сбросить все настройки
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply()
        }
    }
}
```

### Защита конфиденциальных данных

Приложение обеспечивает безопасность через:

- Шифрование всех конфиденциальных данных в базе данных с использованием SQLCipher
- Использование EncryptedSharedPreferences для хранения чувствительных настроек
- Механизмы очистки кэша и временных файлов при выходе из приложения
- Защиту от экспорта незашифрованных данных

### Безопасность медиафайлов

Для защиты фотографий и документов применяются следующие меры:

- Хранение в приватной директории приложения, недоступной другим приложениям
- Контролируемый доступ к файлам через FileProvider
- Безопасное удаление файлов при удалении связанных с ними записей
- Шифрование особо важных документов с помощью AES шифрования

### Аварийное восстановление

В приложении реализован механизм восстановления при нарушении целостности криптографических ключей или повреждении базы данных:

```kotlin
try {
    // Используем зашифрованную базу данных
    database.openHelper.readableDatabase
} catch (e: Exception) {
    // Обрабатываем ошибки целостности ключей
    if (e is AEADBadTagException || e.cause is AEADBadTagException) {
        // Сброс повреждённых ключей и восстановление хранилища
        databaseEncryption.resetSecurityKeys()
        
        // Пытаемся пересоздать базу данных
        resetDatabaseFiles(context)
        
        // Создаем новый экземпляр базы данных с новыми ключами
        createDatabaseWithNewKeys(context, databaseEncryption)
    } else {
        // Обработка других ошибок базы данных
        Log.e(TAG, "Ошибка при доступе к базе данных", e)
        
        // Регистрируем ошибку в системе аналитики
        reportDatabaseError(e)
        
        // Уведомляем пользователя о проблеме
        showDatabaseErrorNotification(context)
    }
}
```

## Бронирования и календарь

Модуль бронирований предоставляет функциональность для управления бронированиями объектов недвижимости как для долгосрочной, так и для посуточной аренды.

### Модель бронирования

Модель `Booking` объединяет информацию о бронировании, включая даты, статус, клиента и финансовую информацию:

```kotlin
data class Booking(
    val id: String = "",
    val propertyId: String,
    val clientId: String? = null,
    val startDate: Long,
    val endDate: Long,
    val status: BookingStatus = BookingStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val totalAmount: Double,
    val depositAmount: Double? = null,
    val notes: String? = null,
    val guestsCount: Int? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val includedServices: List<String> = emptyList(),
    val additionalServices: List<AdditionalService> = emptyList(),
    val rentPeriodMonths: Int? = null,
    val monthlyPaymentAmount: Double? = null,
    val utilityPayments: Boolean? = null,
    val contractType: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Статусы бронирования

```kotlin
enum class BookingStatus {
    PENDING,    // Ожидает подтверждения
    CONFIRMED,  // Подтверждено
    CANCELLED,  // Отменено
    COMPLETED,  // Завершено
    NO_SHOW     // Клиент не явился
}

enum class PaymentStatus {
    UNPAID,         // Не оплачено
    PARTIALLY_PAID, // Частично оплачено
    FULLY_PAID,     // Полностью оплачено
    REFUNDED,       // Возвращено
    DEPOSIT_PAID    // Оплачен только депозит
}
```

### Репозиторий бронирований

Интерфейс `BookingRepository` предоставляет методы для работы с бронированиями:

```kotlin
interface BookingRepository {
    suspend fun addBooking(booking: Booking): Result<Booking>
    suspend fun updateBooking(booking: Booking): Result<Unit>
    suspend fun deleteBooking(bookingId: String): Result<Unit>
    suspend fun getBooking(bookingId: String): Result<Booking>
    suspend fun getAllBookings(): Result<List<Booking>>
    fun observeAllBookings(): Flow<List<Booking>>
    fun observeBookingsByProperty(propertyId: String): Flow<List<Booking>>
    fun observeBookingsByClient(clientId: String): Flow<List<Booking>>
    fun observeBookingsInDateRange(fromDate: Long, toDate: Long): Flow<List<Booking>>
    fun observeBookingsForPropertyInDateRange(propertyId: String, fromDate: Long, toDate: Long): Flow<List<Booking>>
    suspend fun hasBookingConflicts(propertyId: String, fromDate: Long, toDate: Long): Result<Boolean>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
    suspend fun updatePaymentStatus(bookingId: String, status: PaymentStatus): Result<Unit>
    suspend fun autoUpdateBookingStatuses(): Result<Int>
}
```

### Календарь бронирований

Календарь бронирований позволяет визуализировать занятые и свободные даты для объектов недвижимости, а также создавать и управлять бронированиями.

#### BookingCalendarScreen

```kotlin
@Composable
fun BookingCalendarScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    viewModel: BookingCalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Загружаем бронирования для выбранного объекта
    LaunchedEffect(propertyId) {
        viewModel.handleEvent(BookingCalendarEvent.SelectProperty(propertyId))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Календарь бронирований") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Отображение календаря с отмеченными датами бронирований
            BookingCalendarView(
                bookings = state.bookings,
                selectedStartDate = state.selectedStartDate,
                selectedEndDate = state.selectedEndDate,
                onDateSelected = { date ->
                    viewModel.handleEvent(BookingCalendarEvent.SelectDate(date))
                },
                onDateRangeConfirmed = {
                    viewModel.handleEvent(BookingCalendarEvent.ConfirmDateSelection)
                }
            )
            
            // Отображение доступных окон для бронирования
            if (state.availableTimeSlots.isNotEmpty()) {
                Text(
                    text = "Доступные периоды для бронирования:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.availableTimeSlots) { slot ->
                        AvailableTimeSlotItem(
                            slot = slot,
                            onClick = {
                                viewModel.handleEvent(
                                    BookingCalendarEvent.UpdateDates(
                                        start = slot.startDate,
                                        end = slot.endDate
                                    )
                                )
                            }
                        )
                    }
                }
            }
            
            // Список бронирований для выбранного объекта
            Text(
                text = "Бронирования:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            if (state.bookings.isEmpty()) {
                Text(
                    text = "Нет активных бронирований",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.bookings) { booking ->
                        BookingListItem(
                            booking = booking,
                            onClick = {
                                viewModel.handleEvent(BookingCalendarEvent.SelectBooking(booking))
                                viewModel.handleEvent(BookingCalendarEvent.ShowBookingDialog)
                            }
                        )
                    }
                }
            }
            
            // Кнопка создания бронирования
            Button(
                onClick = {
                    viewModel.handleEvent(BookingCalendarEvent.ShowBookingDialog)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Создать бронирование")
            }
        }
        
        // Диалог создания/редактирования бронирования
        if (state.isBookingDialogVisible) {
            BookingFormDialog(
                propertyId = propertyId,
                startDate = state.selectedStartDate,
                endDate = state.selectedEndDate,
                clients = state.clients,
                selectedClient = state.selectedClient,
                editing = !state.isInfoMode,
                booking = state.selectedBooking,
                onClientSelected = { client ->
                    viewModel.handleEvent(BookingCalendarEvent.SelectClient(client))
                },
                onSave = { clientId, startDate, endDate, amount, guestsCount, notes ->
                    if (state.selectedBooking != null && !state.isInfoMode) {
                        viewModel.handleEvent(
                            BookingCalendarEvent.UpdateBooking(
                                state.selectedBooking.id,
                                clientId,
                                startDate,
                                endDate,
                                amount,
                                guestsCount,
                                notes
                            )
                        )
                    } else {
                        viewModel.handleEvent(
                            BookingCalendarEvent.CreateBooking(
                                propertyId,
                                clientId,
                                startDate,
                                endDate,
                                amount,
                                guestsCount,
                                notes
                            )
                        )
                    }
                },
                onDelete = {
                    state.selectedBooking?.let { booking ->
                        viewModel.handleEvent(BookingCalendarEvent.DeleteBooking(booking.id))
                    }
                },
                onDismiss = {
                    viewModel.handleEvent(BookingCalendarEvent.HideBookingDialog)
                }
            )
        }
    }
}
```

#### BookingCalendarView

```kotlin
@Composable
fun BookingCalendarView(
    bookings: List<Booking>,
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDateRangeConfirmed: () -> Unit
) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Заголовок с навигацией по месяцам
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    currentMonth.value = currentMonth.value.minusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Предыдущий месяц"
                )
            }
            
            Text(
                text = currentMonth.value.format(DateTimeFormatter.ofPattern("LLLL yyyy")),
                style = MaterialTheme.typography.titleMedium
            )
            
            IconButton(
                onClick = {
                    currentMonth.value = currentMonth.value.plusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Следующий месяц"
                )
            }
        }
        
        // Дни недели
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Календарная сетка
        val firstDayOfMonth = currentMonth.value.atDay(1)
        val lastDayOfMonth = currentMonth.value.atEndOfMonth()
        
        // Определяем первый день для отображения (может быть из предыдущего месяца)
        val firstDay = firstDayOfMonth.minusDays(
            (firstDayOfMonth.dayOfWeek.value - 1).toLong()
        )
        
        // Создаем список всех дней для отображения
        val calendarDays = mutableListOf<LocalDate>()
        var day = firstDay
        while (day.isBefore(lastDayOfMonth) || day.isEqual(lastDayOfMonth) || calendarDays.size % 7 != 0) {
            calendarDays.add(day)
            day = day.plusDays(1)
        }
        
        // Преобразуем бронирования в множество дат
        val bookedDates = bookings.flatMap { booking ->
            val start = LocalDate.ofEpochDay(booking.startDate / (24 * 60 * 60 * 1000))
            val end = LocalDate.ofEpochDay(booking.endDate / (24 * 60 * 60 * 1000))
            start.datesUntil(end.plusDays(1)).collect(Collectors.toList())
        }.toSet()
        
        // Отображаем календарь
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(16.dp)
        ) {
            items(calendarDays) { date ->
                val isCurrentMonth = date.month == currentMonth.value.month
                val isBooked = date in bookedDates
                val isSelected = (selectedStartDate != null && selectedEndDate != null && 
                    (date.isEqual(selectedStartDate) || date.isEqual(selectedEndDate) || 
                    (date.isAfter(selectedStartDate) && date.isBefore(selectedEndDate))))
                val isSelectionStart = date == selectedStartDate
                val isSelectionEnd = date == selectedEndDate
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                isBooked -> MaterialTheme.colorScheme.errorContainer
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (isSelectionStart || isSelectionEnd) 2.dp else 0.dp,
                            color = if (isSelectionStart || isSelectionEnd) 
                                MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            enabled = !isBooked,
                            onClick = {
                                onDateSelected(date)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            !isCurrentMonth -> MaterialTheme.colorScheme.outline
                            isBooked -> MaterialTheme.colorScheme.onErrorContainer
                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
        
        // Кнопки для подтверждения выбора дат
        if (selectedStartDate != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        onDateSelected(LocalDate.now()) // Сбрасываем выбор
                    }
                ) {
                    Text("Отменить")
                }
                
                Button(
                    onClick = onDateRangeConfirmed,
                    enabled = selectedStartDate != null && selectedEndDate != null
                ) {
                    Text("Подтвердить")
                }
            }
        }
    }
}
```

### Автоматическое обновление статусов

Приложение включает в себя механизм автоматического обновления статусов бронирований в зависимости от текущей даты:

```kotlin
class AutoUpdateBookingStatusesUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return repository.autoUpdateBookingStatuses()
    }
}

// Реализация в репозитории
override suspend fun autoUpdateBookingStatuses(): Result<Int> = withContext(Dispatchers.IO) {
    try {
        val allBookings = bookingDao.getAllBookings()
        val currentTime = System.currentTimeMillis()
        var updatedCount = 0
        
        allBookings.forEach { bookingEntity ->
            val newStatus = when {
                // Если дата окончания в прошлом и статус не COMPLETED или CANCELLED, меняем на COMPLETED
                bookingEntity.endDate < currentTime && 
                bookingEntity.status != BookingStatus.COMPLETED.name && 
                bookingEntity.status != BookingStatus.CANCELLED.name -> {
                    updatedCount++
                    BookingStatus.COMPLETED.name
                }
                // Если дата начала в прошлом, но до даты окончания, и статус PENDING, меняем на CONFIRMED
                bookingEntity.startDate < currentTime && 
                bookingEntity.endDate > currentTime && 
                bookingEntity.status == BookingStatus.PENDING.name -> {
                    updatedCount++
                    BookingStatus.CONFIRMED.name
                }
                else -> null
            }
            
            newStatus?.let { status ->
                bookingDao.updateBookingStatus(bookingEntity.id, status)
            }
        }
        
        Result.success(updatedCount)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

## Управление зависимостями

Для управления зависимостями в проекте используется Dagger Hilt. Основные модули:

### AppModule

Предоставляет основные зависимости приложения:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideClientRepository(clientDao: ClientDao): ClientRepository {
        return ClientRepositoryImpl(clientDao)
    }
    
    @Provides
    @Singleton
    fun providePropertyRepository(propertyDao: PropertyDao): PropertyRepository {
        return PropertyRepositoryImpl(propertyDao)
    }
    
    @Provides
    @Singleton
    fun provideAppointmentRepository(appointmentDao: AppointmentDao): AppointmentRepository {
        return AppointmentRepositoryImpl(appointmentDao)
    }
    
    @Provides
    @Singleton
    fun provideBookingRepository(bookingDao: BookingDao): BookingRepository {
        return BookingRepositoryImpl(bookingDao)
    }
    
    @Provides
    @Singleton
    fun provideImageRepository(@ApplicationContext context: Context): ImageRepository {
        return ImageRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideDocumentRepository(@ApplicationContext context: Context): DocumentRepository {
        return DocumentRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideDatabaseEncryption(@ApplicationContext context: Context): DatabaseEncryption {
        return DatabaseEncryption(context)
    }
    
    // Предоставление юзкейсов
    @Provides
    @Singleton
    fun provideClientUseCases(repository: ClientRepository): ClientUseCases {
        return ClientUseCases(
            addClient = AddClientUseCase(repository),
            updateClient = UpdateClientUseCase(repository),
            deleteClient = DeleteClientUseCase(repository),
            getClient = GetClientUseCase(repository),
            getAllClients = GetAllClientsUseCase(repository),
            getClientsByRentalType = GetClientsByRentalTypeUseCase(repository)
        )
    }
    
    @Provides
    @Singleton
    fun providePropertyUseCases(repository: PropertyRepository): PropertyUseCases {
        return PropertyUseCases(
            addProperty = AddPropertyUseCase(repository),
            updateProperty = UpdatePropertyUseCase(repository),
            deleteProperty = DeletePropertyUseCase(repository),
            getProperty = GetPropertyUseCase(repository),
            getAllProperties = GetAllPropertiesUseCase(repository),
            observeAllProperties = ObserveAllPropertiesUseCase(repository),
            observePropertiesByType = ObservePropertiesByTypeUseCase(repository)
        )
    }
    
    // Остальные юзкейсы...
}
```

### DatabaseModule

Предоставляет компоненты базы данных:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "realestate_assistant_db"
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseEncryption: DatabaseEncryption
    ): AppDatabase {
        try {
            val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
            val factory = SupportFactory(passphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .openHelperFactory(factory)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .build()
        } catch (e: Exception) {
            // Обработка ошибок инициализации базы данных
            Log.e("DatabaseModule", "Ошибка при инициализации базы данных", e)
            
            // Сброс ключей шифрования и повторная попытка
            databaseEncryption.resetSecurityKeys()
            
            // Создаем новую базу данных с новыми ключами
            val newPassphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
            val newFactory = SupportFactory(newPassphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .openHelperFactory(newFactory)
            .fallbackToDestructiveMigration()
            .build()
        }
    }
    
    @Provides
    @Singleton
    fun providePropertyDao(database: AppDatabase): PropertyDao {
        return database.propertyDao()
    }
    
    @Provides
    @Singleton
    fun provideClientDao(database: AppDatabase): ClientDao {
        return database.clientDao()
    }
    
    @Provides
    @Singleton
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao {
        return database.appointmentDao()
    }
    
    @Provides
    @Singleton
    fun provideBookingDao(database: AppDatabase): BookingDao {
        return database.bookingDao()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
```

## Технический долг и планы развития

### Технический долг

1. **Оптимизация производительности**
   - Оптимизация загрузки и кэширования изображений для более быстрого отображения
   - Внедрение пагинации для больших списков объектов и клиентов
   - Оптимизация главного экрана для улучшения скорости загрузки и уменьшения использования ресурсов

2. **Улучшение UI/UX**
   - Доработка отзывчивости UI для разных размеров экранов
   - Улучшение доступности приложения
   - Добавление анимаций для улучшения UX

3. **Тесты**
   - Покрытие основных компонентов UI тестами
   - Написание unit-тестов для репозиториев и юзкейсов
   - Интеграционные тесты для проверки работы базы данных

### Планы развития

1. **Разработка полноценных модулей для клиентов и показов**
   - Реализация полного функционала раздела клиентов по аналогии с объектами:
     - Создание детальной формы добавления/редактирования клиентов
     - Разработка удобных карточек клиентов для списка
     - Детальная страница с информацией о клиенте
     - Система связи клиентов с подходящими объектами
   - Разработка модуля показов/встреч:
     - Форма создания и редактирования показов
     - Календарь показов с разными представлениями
     - Карточки показов с возможностью быстрого доступа
     - Система уведомлений о предстоящих показах

2. **Новые функции**
   - Добавление аналитического модуля для отслеживания эффективности работы
   - Экспорт данных в различные форматы (PDF, Excel)

3. **Улучшение существующих функций**
   - Расширенный поиск и фильтрация объектов
   - Интеллектуальный поиск по всем сущностям приложения
   - Система тегов для объектов, клиентов и встреч
   - Продвинутая сортировка с возможностью настройки пользователем
   - Улучшенный календарь встреч с напоминаниями
   - Система заметок для объектов и клиентов
   - Чек-листы для просмотров объектов
   - Расширение функциональности карт с возможностью поиска объектов на карте

4. **Технические улучшения**
   - Реализация синхронизации данных с облаком (Supabase):
     - Настройка API для синхронизации
     - Механизм разрешения конфликтов при синхронизации
     - Возможность работы в офлайн-режиме с последующей синхронизацией
   - Внедрение многопользовательского режима
   - Улучшение безопасности и защиты данных

## Безопасность и шифрование данных

В приложении реализована система безопасности для защиты конфиденциальных данных пользователей и объектов недвижимости.

### Шифрование базы данных

Основой системы безопасности является шифрование локальной базы данных с использованием SQLCipher:

```kotlin
// Пример конфигурации шифрованной базы данных в AppDatabase
val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
val factory = SupportFactory(passphrase)

Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    DATABASE_NAME
)
.openHelperFactory(factory)
.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
.build()
```

### Компоненты безопасности

- `DatabaseEncryption` - класс для управления ключами шифрования и паролями базы данных
- Интеграция с Android Keystore для безопасного хранения криптографических ключей
- Механизмы восстановления при повреждении ключей шифрования

```kotlin
class DatabaseEncryption @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val KEY_ALIAS = "database_encryption_key"
        private const val PREF_NAME = "database_security_prefs"
        private const val PREF_PASSWORD = "database_password"
        private const val DEFAULT_PASSWORD = "default_secure_password"
    }
    
    private val keyStore by lazy {
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }
    
    private val securePreferences by lazy {
        EncryptedSharedPreferences.create(
            PREF_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    /**
     * Получает пароль для шифрования базы данных
     */
    fun getDatabasePassword(): String {
        // Если пароль уже сохранен, возвращаем его
        if (securePreferences.contains(PREF_PASSWORD)) {
            return securePreferences.getString(PREF_PASSWORD, DEFAULT_PASSWORD) ?: DEFAULT_PASSWORD
        }
        
        // Генерируем новый пароль и сохраняем его
        val password = generateSecurePassword()
        securePreferences.edit().putString(PREF_PASSWORD, password).apply()
        return password
    }
    
    /**
     * Генерирует криптографически стойкий пароль
     */
    private fun generateSecurePassword(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    /**
     * Сбрасывает пароль и ключи шифрования в случае проблем
     */
    fun resetSecurityKeys() {
        try {
            // Удаляем ключ из KeyStore
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
            
            // Сбрасываем пароль
            securePreferences.edit().remove(PREF_PASSWORD).apply()
            
            // Генерируем новый пароль
            val newPassword = generateSecurePassword()
            securePreferences.edit().putString(PREF_PASSWORD, newPassword).apply()
        } catch (e: Exception) {
            Log.e("DatabaseEncryption", "Ошибка при сбросе ключей безопасности", e)
            
            // В случае фатальной ошибки пытаемся сбросить все настройки
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply()
        }
    }
}
```

### Защита конфиденциальных данных

Приложение обеспечивает безопасность через:

- Шифрование всех конфиденциальных данных в базе данных с использованием SQLCipher
- Использование EncryptedSharedPreferences для хранения чувствительных настроек
- Механизмы очистки кэша и временных файлов при выходе из приложения
- Защиту от экспорта незашифрованных данных

### Безопасность медиафайлов

Для защиты фотографий и документов применяются следующие меры:

- Хранение в приватной директории приложения, недоступной другим приложениям
- Контролируемый доступ к файлам через FileProvider
- Безопасное удаление файлов при удалении связанных с ними записей
- Шифрование особо важных документов с помощью AES шифрования

### Аварийное восстановление

В приложении реализован механизм восстановления при нарушении целостности криптографических ключей или повреждении базы данных:

```kotlin
try {
    // Используем зашифрованную базу данных
    database.openHelper.readableDatabase
} catch (e: Exception) {
    // Обрабатываем ошибки целостности ключей
    if (e is AEADBadTagException || e.cause is AEADBadTagException) {
        // Сброс повреждённых ключей и восстановление хранилища
        databaseEncryption.resetSecurityKeys()
        
        // Пытаемся пересоздать базу данных
        resetDatabaseFiles(context)
        
        // Создаем новый экземпляр базы данных с новыми ключами
        createDatabaseWithNewKeys(context, databaseEncryption)
    } else {
        // Обработка других ошибок базы данных
        Log.e(TAG, "Ошибка при доступе к базе данных", e)
        
        // Регистрируем ошибку в системе аналитики
        reportDatabaseError(e)
        
        // Уведомляем пользователя о проблеме
        showDatabaseErrorNotification(context)
    }
}
```

## Бронирования и календарь

Модуль бронирований предоставляет функциональность для управления бронированиями объектов недвижимости как для долгосрочной, так и для посуточной аренды.

### Модель бронирования

Модель `Booking` объединяет информацию о бронировании, включая даты, статус, клиента и финансовую информацию:

```kotlin
data class Booking(
    val id: String = "",
    val propertyId: String,
    val clientId: String? = null,
    val startDate: Long,
    val endDate: Long,
    val status: BookingStatus = BookingStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val totalAmount: Double,
    val depositAmount: Double? = null,
    val notes: String? = null,
    val guestsCount: Int? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val includedServices: List<String> = emptyList(),
    val additionalServices: List<AdditionalService> = emptyList(),
    val rentPeriodMonths: Int? = null,
    val monthlyPaymentAmount: Double? = null,
    val utilityPayments: Boolean? = null,
    val contractType: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Статусы бронирования

```kotlin
enum class BookingStatus {
    PENDING,    // Ожидает подтверждения
    CONFIRMED,  // Подтверждено
    CANCELLED,  // Отменено
    COMPLETED,  // Завершено
    NO_SHOW     // Клиент не явился
}

enum class PaymentStatus {
    UNPAID,         // Не оплачено
    PARTIALLY_PAID, // Частично оплачено
    FULLY_PAID,     // Полностью оплачено
    REFUNDED,       // Возвращено
    DEPOSIT_PAID    // Оплачен только депозит
}
```

### Репозиторий бронирований

Интерфейс `BookingRepository` предоставляет методы для работы с бронированиями:

```kotlin
interface BookingRepository {
    suspend fun addBooking(booking: Booking): Result<Booking>
    suspend fun updateBooking(booking: Booking): Result<Unit>
    suspend fun deleteBooking(bookingId: String): Result<Unit>
    suspend fun getBooking(bookingId: String): Result<Booking>
    suspend fun getAllBookings(): Result<List<Booking>>
    fun observeAllBookings(): Flow<List<Booking>>
    fun observeBookingsByProperty(propertyId: String): Flow<List<Booking>>
    fun observeBookingsByClient(clientId: String): Flow<List<Booking>>
    fun observeBookingsInDateRange(fromDate: Long, toDate: Long): Flow<List<Booking>>
    fun observeBookingsForPropertyInDateRange(propertyId: String, fromDate: Long, toDate: Long): Flow<List<Booking>>
    suspend fun hasBookingConflicts(propertyId: String, fromDate: Long, toDate: Long): Result<Boolean>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
    suspend fun updatePaymentStatus(bookingId: String, status: PaymentStatus): Result<Unit>
    suspend fun autoUpdateBookingStatuses(): Result<Int>
}
```

### Календарь бронирований

Календарь бронирований позволяет визуализировать занятые и свободные даты для объектов недвижимости, а также создавать и управлять бронированиями.

#### BookingCalendarScreen

```kotlin
@Composable
fun BookingCalendarScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    viewModel: BookingCalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Загружаем бронирования для выбранного объекта
    LaunchedEffect(propertyId) {
        viewModel.handleEvent(BookingCalendarEvent.SelectProperty(propertyId))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Календарь бронирований") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Отображение календаря с отмеченными датами бронирований
            BookingCalendarView(
                bookings = state.bookings,
                selectedStartDate = state.selectedStartDate,
                selectedEndDate = state.selectedEndDate,
                onDateSelected = { date ->
                    viewModel.handleEvent(BookingCalendarEvent.SelectDate(date))
                },
                onDateRangeConfirmed = {
                    viewModel.handleEvent(BookingCalendarEvent.ConfirmDateSelection)
                }
            )
            
            // Отображение доступных окон для бронирования
            if (state.availableTimeSlots.isNotEmpty()) {
                Text(
                    text = "Доступные периоды для бронирования:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.availableTimeSlots) { slot ->
                        AvailableTimeSlotItem(
                            slot = slot,
                            onClick = {
                                viewModel.handleEvent(
                                    BookingCalendarEvent.UpdateDates(
                                        start = slot.startDate,
                                        end = slot.endDate
                                    )
                                )
                            }
                        )
                    }
                }
            }
            
            // Список бронирований для выбранного объекта
            Text(
                text = "Бронирования:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            if (state.bookings.isEmpty()) {
                Text(
                    text = "Нет активных бронирований",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.bookings) { booking ->
                        BookingListItem(
                            booking = booking,
                            onClick = {
                                viewModel.handleEvent(BookingCalendarEvent.SelectBooking(booking))
                                viewModel.handleEvent(BookingCalendarEvent.ShowBookingDialog)
                            }
                        )
                    }
                }
            }
            
            // Кнопка создания бронирования
            Button(
                onClick = {
                    viewModel.handleEvent(BookingCalendarEvent.ShowBookingDialog)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Создать бронирование")
            }
        }
        
        // Диалог создания/редактирования бронирования
        if (state.isBookingDialogVisible) {
            BookingFormDialog(
                propertyId = propertyId,
                startDate = state.selectedStartDate,
                endDate = state.selectedEndDate,
                clients = state.clients,
                selectedClient = state.selectedClient,
                editing = !state.isInfoMode,
                booking = state.selectedBooking,
                onClientSelected = { client ->
                    viewModel.handleEvent(BookingCalendarEvent.SelectClient(client))
                },
                onSave = { clientId, startDate, endDate, amount, guestsCount, notes ->
                    if (state.selectedBooking != null && !state.isInfoMode) {
                        viewModel.handleEvent(
                            BookingCalendarEvent.UpdateBooking(
                                state.selectedBooking.id,
                                clientId,
                                startDate,
                                endDate,
                                amount,
                                guestsCount,
                                notes
                            )
                        )
                    } else {
                        viewModel.handleEvent(
                            BookingCalendarEvent.CreateBooking(
                                propertyId,
                                clientId,
                                startDate,
                                endDate,
                                amount,
                                guestsCount,
                                notes
                            )
                        )
                    }
                },
                onDelete = {
                    state.selectedBooking?.let { booking ->
                        viewModel.handleEvent(BookingCalendarEvent.DeleteBooking(booking.id))
                    }
                },
                onDismiss = {
                    viewModel.handleEvent(BookingCalendarEvent.HideBookingDialog)
                }
            )
        }
    }
}
```

#### BookingCalendarView

```kotlin
@Composable
fun BookingCalendarView(
    bookings: List<Booking>,
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDateRangeConfirmed: () -> Unit
) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Заголовок с навигацией по месяцам
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    currentMonth.value = currentMonth.value.minusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Предыдущий месяц"
                )
            }
            
            Text(
                text = currentMonth.value.format(DateTimeFormatter.ofPattern("LLLL yyyy")),
                style = MaterialTheme.typography.titleMedium
            )
            
            IconButton(
                onClick = {
                    currentMonth.value = currentMonth.value.plusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Следующий месяц"
                )
            }
        }
        
        // Дни недели
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Календарная сетка
        val firstDayOfMonth = currentMonth.value.atDay(1)
        val lastDayOfMonth = currentMonth.value.atEndOfMonth()
        
        // Определяем первый день для отображения (может быть из предыдущего месяца)
        val firstDay = firstDayOfMonth.minusDays(
            (firstDayOfMonth.dayOfWeek.value - 1).toLong()
        )
        
        // Создаем список всех дней для отображения
        val calendarDays = mutableListOf<LocalDate>()
        var day = firstDay
        while (day.isBefore(lastDayOfMonth) || day.isEqual(lastDayOfMonth) || calendarDays.size % 7 != 0) {
            calendarDays.add(day)
            day = day.plusDays(1)
        }
        
        // Преобразуем бронирования в множество дат
        val bookedDates = bookings.flatMap { booking ->
            val start = LocalDate.ofEpochDay(booking.startDate / (24 * 60 * 60 * 1000))
            val end = LocalDate.ofEpochDay(booking.endDate / (24 * 60 * 60 * 1000))
            start.datesUntil(end.plusDays(1)).collect(Collectors.toList())
        }.toSet()
        
        // Отображаем календарь
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(16.dp)
        ) {
            items(calendarDays) { date ->
                val isCurrentMonth = date.month == currentMonth.value.month
                val isBooked = date in bookedDates
                val isSelected = (selectedStartDate != null && selectedEndDate != null && 
                    (date.isEqual(selectedStartDate) || date.isEqual(selectedEndDate) || 
                    (date.isAfter(selectedStartDate) && date.isBefore(selectedEndDate))))
                val isSelectionStart = date == selectedStartDate
                val isSelectionEnd = date == selectedEndDate
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                isBooked -> MaterialTheme.colorScheme.errorContainer
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (isSelectionStart || isSelectionEnd) 2.dp else 0.dp,
                            color = if (isSelectionStart || isSelectionEnd) 
                                MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            enabled = !isBooked,
                            onClick = {
                                onDateSelected(date)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            !isCurrentMonth -> MaterialTheme.colorScheme.outline
                            isBooked -> MaterialTheme.colorScheme.onErrorContainer
                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
        
        // Кнопки для подтверждения выбора дат
        if (selectedStartDate != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        onDateSelected(LocalDate.now()) // Сбрасываем выбор
                    }
                ) {
                    Text("Отменить")
                }
                
                Button(
                    onClick = onDateRangeConfirmed,
                    enabled = selectedStartDate != null && selectedEndDate != null
                ) {
                    Text("Подтвердить")
                }
            }
        }
    }
}
```

### Автоматическое обновление статусов

Приложение включает в себя механизм автоматического обновления статусов бронирований в зависимости от текущей даты:

```kotlin
class AutoUpdateBookingStatusesUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return repository.autoUpdateBookingStatuses()
    }
}

// Реализация в репозитории
override suspend fun autoUpdateBookingStatuses(): Result<Int> = withContext(Dispatchers.IO) {
    try {
        val allBookings = bookingDao.getAllBookings()
        val currentTime = System.currentTimeMillis()
        var updatedCount = 0
        
        allBookings.forEach { bookingEntity ->
            val newStatus = when {
                // Если дата окончания в прошлом и статус не COMPLETED или CANCELLED, меняем на COMPLETED
                bookingEntity.endDate < currentTime && 
                bookingEntity.status != BookingStatus.COMPLETED.name && 
                bookingEntity.status != BookingStatus.CANCELLED.name -> {
                    updatedCount++
                    BookingStatus.COMPLETED.name
                }
                // Если дата начала в прошлом, но до даты окончания, и статус PENDING, меняем на CONFIRMED
                bookingEntity.startDate < currentTime && 
                bookingEntity.endDate > currentTime && 
                bookingEntity.status == BookingStatus.PENDING.name -> {
                    updatedCount++
                    BookingStatus.CONFIRMED.name
                }
                else -> null
            }
            
            newStatus?.let { status ->
                bookingDao.updateBookingStatus(bookingEntity.id, status)
            }
        }
        
        Result.success(updatedCount)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

## Управление зависимостями

Для управления зависимостями в проекте используется Dagger Hilt. Основные модули:

### AppModule

Предоставляет основные зависимости приложения:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideClientRepository(clientDao: ClientDao): ClientRepository {
        return ClientRepositoryImpl(clientDao)
    }
    
    @Provides
    @Singleton
    fun providePropertyRepository(propertyDao: PropertyDao): PropertyRepository {
        return PropertyRepositoryImpl(propertyDao)
    }
    
    @Provides
    @Singleton
    fun provideAppointmentRepository(appointmentDao: AppointmentDao): AppointmentRepository {
        return AppointmentRepositoryImpl(appointmentDao)
    }
    
    @Provides
    @Singleton
    fun provideBookingRepository(bookingDao: BookingDao): BookingRepository {
        return BookingRepositoryImpl(bookingDao)
    }
    
    @Provides
    @Singleton
    fun provideImageRepository(@ApplicationContext context: Context): ImageRepository {
        return ImageRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideDocumentRepository(@ApplicationContext context: Context): DocumentRepository {
        return DocumentRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideDatabaseEncryption(@ApplicationContext context: Context): DatabaseEncryption {
        return DatabaseEncryption(context)
    }
    
    // Предоставление юзкейсов
    @Provides
    @Singleton
    fun provideClientUseCases(repository: ClientRepository): ClientUseCases {
        return ClientUseCases(
            addClient = AddClientUseCase(repository),
            updateClient = UpdateClientUseCase(repository),
            deleteClient = DeleteClientUseCase(repository),
            getClient = GetClientUseCase(repository),
            getAllClients = GetAllClientsUseCase(repository),
            getClientsByRentalType = GetClientsByRentalTypeUseCase(repository)
        )
    }
    
    @Provides
    @Singleton
    fun providePropertyUseCases(repository: PropertyRepository): PropertyUseCases {
        return PropertyUseCases(
            addProperty = AddPropertyUseCase(repository),
            updateProperty = UpdatePropertyUseCase(repository),
            deleteProperty = DeletePropertyUseCase(repository),
            getProperty = GetPropertyUseCase(repository),
            getAllProperties = GetAllPropertiesUseCase(repository),
            observeAllProperties = ObserveAllPropertiesUseCase(repository),
            observePropertiesByType = ObservePropertiesByTypeUseCase(repository)
        )
    }
    
    // Остальные юзкейсы...
}
```

### DatabaseModule

Предоставляет компоненты базы данных:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "realestate_assistant_db"
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseEncryption: DatabaseEncryption
    ): AppDatabase {
        try {
            val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
            val factory = SupportFactory(passphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .openHelperFactory(factory)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .build()
        } catch (e: Exception) {
            // Обработка ошибок инициализации базы данных
            Log.e("DatabaseModule", "Ошибка при инициализации базы данных", e)
            
            // Сброс ключей шифрования и повторная попытка
            databaseEncryption.resetSecurityKeys()
            
            // Создаем новую базу данных с новыми ключами
            val newPassphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
            val newFactory = SupportFactory(newPassphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .openHelperFactory(newFactory)
            .fallbackToDestructiveMigration()
            .build()
        }
    }
    
    @Provides
    @Singleton
    fun providePropertyDao(database: AppDatabase): PropertyDao {
        return database.propertyDao()
    }
    
    @Provides
    @Singleton
    fun provideClientDao(database: AppDatabase): ClientDao {
        return database.clientDao()
    }
    
    @Provides
    @Singleton
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao {
        return database.appointmentDao()
    }
    
    @Provides
    @Singleton
    fun provideBookingDao(database: AppDatabase): BookingDao {
        return database.bookingDao()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
```

## Технический долг и планы развития

### Технический долг

1. **Оптимизация производительности**
   - Оптимизация загрузки и кэширования изображений для более быстрого отображения
   - Внедрение пагинации для больших списков объектов и клиентов
   - Оптимизация главного экрана для улучшения скорости загрузки и уменьшения использования ресурсов

2. **Улучшение UI/UX**
   - Доработка отзывчивости UI для разных размеров экранов
   - Улучшение доступности приложения
   - Добавление анимаций для улучшения UX

3. **Тесты**
   - Покрытие основных компонентов UI тестами
   - Написание unit-тестов для репозиториев и юзкейсов
   - Интеграционные тесты для проверки работы базы данных

### Планы развития

1. **Разработка полноценных модулей для клиентов и показов**
   - Реализация полного функционала раздела клиентов по аналогии с объектами:
     - Создание детальной формы добавления/редактирования клиентов
     - Разработка удобных карточек клиентов для списка
     - Детальная страница с информацией о клиенте
     - Система связи клиентов с подходящими объектами
   - Разработка модуля показов/встреч:
     - Форма создания и редактирования показов
     - Календарь показов с разными представлениями
     - Карточки показов с возможностью быстрого доступа
     - Система уведомлений о предстоящих показах

2. **Новые функции**
   - Добавление аналитического модуля для отслеживания эффективности работы
   - Экспорт данных в различные форматы (PDF, Excel)

3. **Улучшение существующих функций**
   - Расширенный поиск и фильтрация объектов
   - Интеллектуальный поиск по всем сущностям приложения
   - Система тегов для объектов, клиентов и встреч
   - Продвинутая сортировка с возможностью настройки пользователем
   - Улучшенный календарь встреч с напоминаниями
   - Система заметок для объектов и клиентов
   - Чек-листы для просмотров объектов
   - Расширение функциональности карт с возможностью поиска объектов на карте

4. **Технические улучшения**
   - Реализация синхронизации данных с облаком (Supabase):
     - Настройка API для синхронизации
     - Механизм разрешения конфликтов при синхронизации
     - Возможность работы в офлайн-режиме с последующей синхронизацией
   - Внедрение многопользовательского режима
   - Улучшение безопасности и защиты данных

## Безопасность и шифрование данных

В приложении реализована система безопасности для защиты конфиденциальных данных пользователей и объектов недвижимости.

### Шифрование базы данных

Основой системы безопасности является шифрование локальной базы данных с использованием SQLCipher:

```kotlin
// Пример конфигурации шифрованной базы данных в AppDatabase
val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
val factory = SupportFactory(passphrase)

Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    DATABASE_NAME
)
.openHelperFactory(factory)
.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
.build()
```

### Компоненты безопасности

- `DatabaseEncryption` - класс для управления ключами шифрования и паролями базы данных
- Интеграция с Android Keystore для безопасного хранения криптографических ключей
- Механизмы восстановления при повреждении ключей шифрования

```kotlin
class DatabaseEncryption @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val KEY_ALIAS = "database_encryption_key"
        private const val PREF_NAME = "database_security_prefs"
        private const val PREF_PASSWORD = "database_password"
        private const val DEFAULT_PASSWORD = "default_secure_password"
    }
    
    private val keyStore by lazy {
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }
    
    private val securePreferences by lazy {
        EncryptedSharedPreferences.create(
            PREF_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    /**
     * Получает пароль для шифрования базы данных
     */
    fun getDatabasePassword(): String {
        // Если пароль уже сохранен, возвращаем его
        if (securePreferences.contains(PREF_PASSWORD)) {
            return securePreferences.getString(PREF_PASSWORD, DEFAULT_PASSWORD) ?: DEFAULT_PASSWORD
        }
        
        // Генерируем новый пароль и сохраняем его
        val password = generateSecurePassword()
        securePreferences.edit().putString(PREF_PASSWORD, password).apply()
        return password
    }
    
    /**
     * Генерирует криптографически стойкий пароль
     */
    private fun generateSecurePassword(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    /**
     * Сбрасывает пароль и ключи шифрования в случае проблем
     */
    fun resetSecurityKeys() {
        try {
            // Удаляем ключ из KeyStore
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
            
            // Сбрасываем пароль
            securePreferences.edit().remove(PREF_PASSWORD).apply()
            
            // Генерируем новый пароль
            val newPassword = generateSecurePassword()
            securePreferences.edit().putString(PREF_PASSWORD, newPassword).apply()
        } catch (e: Exception) {
            Log.e("DatabaseEncryption", "Ошибка при сбросе ключей безопасности", e)
            
            // В случае фатальной ошибки пытаемся сбросить все настройки
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply()
        }
    }
}
```

### Защита конфиденциальных данных

Приложение обеспечивает безопасность через:

- Шифрование всех конфиденциальных данных в базе данных с использованием SQLCipher
- Использование EncryptedSharedPreferences для хранения чувствительных настроек
- Механизмы очистки кэша и временных файлов при выходе из приложения
- Защиту от экспорта незашифрованных данных

### Безопасность медиафайлов

Для защиты фотографий и документов применяются следующие меры:

- Хранение в приватной директории приложения, недоступной другим приложениям
- Контролируемый доступ к файлам через FileProvider
- Безопасное удаление файлов при удалении связанных с ними записей
- Шифрование особо важных документов с помощью AES шифрования

### Аварийное восстановление

В приложении реализован механизм восстановления при нарушении целостности криптографических ключей или повреждении базы данных:

```kotlin
try {
    // Используем зашифрованную базу данных
    database.openHelper.readableDatabase
} catch (e: Exception) {
    // Обрабатываем ошибки целостности ключей
    if (e is AEADBadTagException || e.cause is AEADBadTagException) {
        // Сброс повреждённых ключей и восстановление хранилища
        databaseEncryption.resetSecurityKeys()
        
        // Пытаемся пересоздать базу данных
        resetDatabaseFiles(context)
        
        // Создаем новый экземпляр базы данных с новыми ключами
        createDatabaseWithNewKeys(context, databaseEncryption)
    } else {
        // Обработка других ошибок базы данных
        Log.e(TAG, "Ошибка при доступе к базе данных", e)
        
        // Регистрируем ошибку в системе аналитики
        reportDatabaseError(e)
        
        // Уведомляем пользователя о проблеме
        showDatabaseErrorNotification(context)
    }
}
```

## Бронирования и календарь

Модуль бронирований предоставляет функциональность для управления бронированиями объектов недвижимости как для долгосрочной, так и для посуточной аренды.

### Модель бронирования

Модель `Booking` объединяет информацию о бронировании, включая даты, статус, клиента и финансовую информацию:

```kotlin
data class Booking(
    val id: String = "",
    val propertyId: String,
    val clientId: String? = null,
    val startDate: Long,
    val endDate: Long,
    val status: BookingStatus = BookingStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val totalAmount: Double,
    val depositAmount: Double? = null,
    val notes: String? = null,
    val guestsCount: Int? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val includedServices: List<String> = emptyList(),
    val additionalServices: List<AdditionalService> = emptyList(),
    val rentPeriodMonths: Int? = null,
    val monthlyPaymentAmount: Double? = null,
    val utilityPayments: Boolean? = null,
    val contractType: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Статусы бронирования

```kotlin
enum class BookingStatus {
    PENDING,    // Ожидает подтверждения
    CONFIRMED,  // Подтверждено
    CANCELLED,  // Отменено
    COMPLETED,  // Завершено
    NO_SHOW     // Клиент не явился
}

enum class PaymentStatus {
    UNPAID,         // Не оплачено
    PARTIALLY_PAID, // Частично оплачено
    FULLY_PAID,     // Полностью оплачено
    REFUNDED,       // Возвращено
    DEPOSIT_PAID    // Оплачен только депозит
}
```

### Репозиторий бронирований

Интерфейс `BookingRepository` предоставляет методы для работы с бронированиями:

```kotlin
interface BookingRepository {
    suspend fun addBooking(booking: Booking): Result<Booking>
    suspend fun updateBooking(booking: Booking): Result<Unit>
    suspend fun deleteBooking(bookingId: String): Result<Unit>
    suspend fun getBooking(bookingId: String): Result<Booking>
    suspend fun getAllBookings(): Result<List<Booking>>
    fun observeAllBookings(): Flow<List<Booking>>
    fun observeBookingsByProperty(propertyId: String): Flow<List<Booking>>
    fun observeBookingsByClient(clientId: String): Flow<List<Booking>>
    fun observeBookingsInDateRange(fromDate: Long, toDate: Long): Flow<List<Booking>>
    fun observeBookingsForPropertyInDateRange(propertyId: String, fromDate: Long, toDate: Long): Flow<List<Booking>>
    suspend fun hasBookingConflicts(propertyId: String, fromDate: Long, toDate: Long): Result<Boolean>
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit>
    suspend fun updatePaymentStatus(bookingId: String, status: PaymentStatus): Result<Unit>
    suspend fun autoUpdateBookingStatuses(): Result<Int>
}
```

### Календарь бронирований

Календарь бронирований позволяет визуализировать занятые и свободные даты для объектов недвижимости, а также создавать и управлять бронированиями.

#### BookingCalendarScreen

```kotlin
@Composable
fun BookingCalendarScreen(
    propertyId: String,
    onNavigateBack: () -> Unit,
    viewModel: BookingCalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Загружаем бронирования для выбранного объекта
    LaunchedEffect(propertyId) {
        viewModel.handleEvent(BookingCalendarEvent.SelectProperty(propertyId))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Календарь бронирований") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Отображение календаря с отмеченными датами бронирований
            BookingCalendarView(
                bookings = state.bookings,
                selectedStartDate = state.selectedStartDate,
                selectedEndDate = state.selectedEndDate,
                onDateSelected = { date ->
                    viewModel.handleEvent(BookingCalendarEvent.SelectDate(date))
                },
                onDateRangeConfirmed = {
                    viewModel.handleEvent(BookingCalendarEvent.ConfirmDateSelection)
                }
            )
            
            // Отображение доступных окон для бронирования
            if (state.availableTimeSlots.isNotEmpty()) {
                Text(
                    text = "Доступные периоды для бронирования:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.availableTimeSlots) { slot ->
                        AvailableTimeSlotItem(
                            slot = slot,
                            onClick = {
                                viewModel.handleEvent(
                                    BookingCalendarEvent.UpdateDates(
                                        start = slot.startDate,
                                        end = slot.endDate
                                    )
                                )
                            }
                        )
                    }
                }
            }
            
            // Список бронирований для выбранного объекта
            Text(
                text = "Бронирования:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            if (state.bookings.isEmpty()) {
                Text(
                    text = "Нет активных бронирований",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(state.bookings) { booking ->
                        BookingListItem(
                            booking = booking,
                            onClick = {
                                viewModel.handleEvent(BookingCalendarEvent.SelectBooking(booking))
                                viewModel.handleEvent(BookingCalendarEvent.ShowBookingDialog)
                            }
                        )
                    }
                }
            }
            
            // Кнопка создания бронирования
            Button(
                onClick = {
                    viewModel.handleEvent(BookingCalendarEvent.ShowBookingDialog)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Создать бронирование")
            }
        }
        
        // Диалог создания/редактирования бронирования
        if (state.isBookingDialogVisible) {
            BookingFormDialog(
                propertyId = propertyId,
                startDate = state.selectedStartDate,
                endDate = state.selectedEndDate,
                clients = state.clients,
                selectedClient = state.selectedClient,
                editing = !state.isInfoMode,
                booking = state.selectedBooking,
                onClientSelected = { client ->
                    viewModel.handleEvent(BookingCalendarEvent.SelectClient(client))
                },
                onSave = { clientId, startDate, endDate, amount, guestsCount, notes ->
                    if (state.selectedBooking != null && !state.isInfoMode) {
                        viewModel.handleEvent(
                            BookingCalendarEvent.UpdateBooking(
                                state.selectedBooking.id,
                                clientId,
                                startDate,
                                endDate,
                                amount,
                                guestsCount,
                                notes
                            )
                        )
                    } else {
                        viewModel.handleEvent(
                            BookingCalendarEvent.CreateBooking(
                                propertyId,
                                clientId,
                                startDate,
                                endDate,
                                amount,
                                guestsCount,
                                notes
                            )
                        )
                    }
                },
                onDelete = {
                    state.selectedBooking?.let { booking ->
                        viewModel.handleEvent(BookingCalendarEvent.DeleteBooking(booking.id))
                    }
                },
                onDismiss = {
                    viewModel.handleEvent(BookingCalendarEvent.HideBookingDialog)
                }
            )
        }
    }
}
```

#### BookingCalendarView

```kotlin
@Composable
fun BookingCalendarView(
    bookings: List<Booking>,
    selectedStartDate: LocalDate?,
    selectedEndDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDateRangeConfirmed: () -> Unit
) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Заголовок с навигацией по месяцам
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    currentMonth.value = currentMonth.value.minusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Предыдущий месяц"
                )
            }
            
            Text(
                text = currentMonth.value.format(DateTimeFormatter.ofPattern("LLLL yyyy")),
                style = MaterialTheme.typography.titleMedium
            )
            
            IconButton(
                onClick = {
                    currentMonth.value = currentMonth.value.plusMonths(1)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Следующий месяц"
                )
            }
        }
        
        // Дни недели
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Календарная сетка
        val firstDayOfMonth = currentMonth.value.atDay(1)
        val lastDayOfMonth = currentMonth.value.atEndOfMonth()
        
        // Определяем первый день для отображения (может быть из предыдущего месяца)
        val firstDay = firstDayOfMonth.minusDays(
            (firstDayOfMonth.dayOfWeek.value - 1).toLong()
        )
        
        // Создаем список всех дней для отображения
        val calendarDays = mutableListOf<LocalDate>()
        var day = firstDay
        while (day.isBefore(lastDayOfMonth) || day.isEqual(lastDayOfMonth) || calendarDays.size % 7 != 0) {
            calendarDays.add(day)
            day = day.plusDays(1)
        }
        
        // Преобразуем бронирования в множество дат
        val bookedDates = bookings.flatMap { booking ->
            val start = LocalDate.ofEpochDay(booking.startDate / (24 * 60 * 60 * 1000))
            val end = LocalDate.ofEpochDay(booking.endDate / (24 * 60 * 60 * 1000))
            start.datesUntil(end.plusDays(1)).collect(Collectors.toList())
        }.toSet()
        
        // Отображаем календарь
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(16.dp)
        ) {
            items(calendarDays) { date ->
                val isCurrentMonth = date.month == currentMonth.value.month
                val isBooked = date in bookedDates
                val isSelected = (selectedStartDate != null && selectedEndDate != null && 
                    (date.isEqual(selectedStartDate) || date.isEqual(selectedEndDate) || 
                    (date.isAfter(selectedStartDate) && date.isBefore(selectedEndDate))))
                val isSelectionStart = date == selectedStartDate
                val isSelectionEnd = date == selectedEndDate
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                isBooked -> MaterialTheme.colorScheme.errorContainer
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (isSelectionStart || isSelectionEnd) 2.dp else 0.dp,
                            color = if (isSelectionStart || isSelectionEnd) 
                                MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(
                            enabled = !isBooked,
                            onClick = {
                                onDateSelected(date)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            !isCurrentMonth -> MaterialTheme.colorScheme.outline
                            isBooked -> MaterialTheme.colorScheme.onErrorContainer
                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
        
        // Кнопки для подтверждения выбора дат
        if (selectedStartDate != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        onDateSelected(LocalDate.now()) // Сбрасываем выбор
                    }
                ) {
                    Text("Отменить")
                }
                
                Button(
                    onClick = onDateRangeConfirmed,
                    enabled = selectedStartDate != null && selectedEndDate != null
                ) {
                    Text("Подтвердить")
                }
            }
        }
    }
}
```

### Автоматическое обновление статусов

Приложение включает в себя механизм автоматического обновления статусов бронирований в зависимости от текущей даты:

```kotlin
class AutoUpdateBookingStatusesUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(): Result<Int> {
        return repository.autoUpdateBookingStatuses()
    }
}

// Реализация в репозитории
override suspend fun autoUpdateBookingStatuses(): Result<Int> = withContext(Dispatchers.IO) {
    try {
        val allBookings = bookingDao.getAllBookings()
        val currentTime = System.currentTimeMillis()
        var updatedCount = 0
        
        allBookings.forEach { bookingEntity ->
            val newStatus = when {
                // Если дата окончания в прошлом и статус не COMPLETED или CANCELLED, меняем на COMPLETED
                bookingEntity.endDate < currentTime && 
                bookingEntity.status != BookingStatus.COMPLETED.name && 
                bookingEntity.status != BookingStatus.CANCELLED.name -> {
                    updatedCount++
                    BookingStatus.COMPLETED.name
                }
                // Если дата начала в прошлом, но до даты окончания, и статус PENDING, меняем на CONFIRMED
                bookingEntity.startDate < currentTime && 
                bookingEntity.endDate > currentTime && 
                bookingEntity.status == BookingStatus.PENDING.name -> {
                    updatedCount++
                    BookingStatus.CONFIRMED.name
                }
                else -> null
            }
            
            newStatus?.let { status ->
                bookingDao.updateBookingStatus(bookingEntity.id, status)
            }
        }
        
        Result.success(updatedCount)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

## Управление зависимостями

Для управления зависимостями в проекте используется Dagger Hilt. Основные модули:

### AppModule

Предоставляет основные зависимости приложения:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideClientRepository(clientDao: ClientDao): ClientRepository {
        return ClientRepositoryImpl(clientDao)
    }
    
    @Provides
    @Singleton
    fun providePropertyRepository(propertyDao: PropertyDao): PropertyRepository {
        return PropertyRepositoryImpl(propertyDao)
    }
    
    @Provides
    @Singleton
    fun provideAppointmentRepository(appointmentDao: AppointmentDao): AppointmentRepository {
        return AppointmentRepositoryImpl(appointmentDao)
    }
    
    @Provides
    @Singleton
    fun provideBookingRepository(bookingDao: BookingDao): BookingRepository {
        return BookingRepositoryImpl(bookingDao)
    }
    
    @Provides
    @Singleton
    fun provideImageRepository(@ApplicationContext context: Context): ImageRepository {
        return ImageRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideDocumentRepository(@ApplicationContext context: Context): DocumentRepository {
        return DocumentRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideDatabaseEncryption(@ApplicationContext context: Context): DatabaseEncryption {
        return DatabaseEncryption(context)
    }
    
    // Предоставление юзкейсов
    @Provides
    @Singleton
    fun provideClientUseCases(repository: ClientRepository): ClientUseCases {
        return ClientUseCases(
            addClient = AddClientUseCase(repository),
            updateClient = UpdateClientUseCase(repository),
            deleteClient = DeleteClientUseCase(repository),
            getClient = GetClientUseCase(repository),
            getAllClients = GetAllClientsUseCase(repository),
            getClientsByRentalType = GetClientsByRentalTypeUseCase(repository)
        )
    }
    
    @Provides
    @Singleton
    fun providePropertyUseCases(repository: PropertyRepository): PropertyUseCases {
        return PropertyUseCases(
            addProperty = AddPropertyUseCase(repository),
            updateProperty = UpdatePropertyUseCase(repository),
            deleteProperty = DeletePropertyUseCase(repository),
            getProperty = GetPropertyUseCase(repository),
            getAllProperties = GetAllPropertiesUseCase(repository),
            observeAllProperties = ObserveAllPropertiesUseCase(repository),
            observePropertiesByType = ObservePropertiesByTypeUseCase(repository)
        )
    }
    
    // Остальные юзкейсы...
}
```

### DatabaseModule

Предоставляет компоненты базы данных:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "realestate_assistant_db"
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseEncryption: DatabaseEncryption
    ): AppDatabase {
        try {
            val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
            val factory = SupportFactory(passphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .openHelperFactory(factory)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .build()
        } catch (e: Exception) {
            // Обработка ошибок инициализации базы данных
            Log.e("DatabaseModule", "Ошибка при инициализации базы данных", e)
            
            // Сброс ключей шифрования и повторная попытка
            databaseEncryption.resetSecurityKeys()
            
            // Создаем новую базу данных с новыми ключами
            val newPassphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
            val newFactory = SupportFactory(newPassphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .openHelperFactory(newFactory)
            .fallbackToDestructiveMigration()
            .build()
        }
    }
    
    @Provides
    @Singleton
    fun providePropertyDao(database: AppDatabase): PropertyDao {
        return database.propertyDao()
    }
    
    @Provides
    @Singleton
    fun provideClientDao(database: AppDatabase): ClientDao {
        return database.clientDao()
    }
    
    @Provides
    @Singleton
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao {
        return database.appointmentDao()
    }
    
    @Provides
    @Singleton
    fun provideBookingDao(database: AppDatabase): BookingDao {
        return database.bookingDao()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
```

## Технический долг и планы развития

### Технический долг

1. **Оптимизация производительности**
   - Оптимизация загрузки и кэширования изображений для более быстрого отображения
   - Внедрение пагинации для больших списков объектов и клиентов
   - Оптимизация главного экрана для улучшения скорости загрузки и уменьшения использования ресурсов

2. **Улучшение UI/UX**
   - Доработка отзывчивости UI для разных размеров экранов
   - Улучшение доступности приложения
   - Добавление анимаций для улучшения UX

3. **Тесты**
   - Покрытие основных компонентов UI тестами
   - Написание unit-тестов для репозиториев и юзкейсов
   - Интеграционные тесты для проверки работы базы данных

### Планы развития

1. **Разработка полноценных модулей для клиентов и показов**
   - Реализация полного функционала раздела клиентов по аналогии с объектами:
     - Создание детальной формы добавления/редактирования клиентов
     - Разработка удобных карточек клиентов для списка
     - Детальная страница с информацией о клиенте
     - Система связи клиентов с подходящими объектами
   - Разработка модуля показов/встреч:
     - Форма создания и редактирования показов
     - Календарь показов с разными представлениями
     - Карточки показов с возможностью быстрого доступа
     - Система уведомлений о предстоящих показах

2. **Новые функции**
   - Добавление аналитического модуля для отслеживания эффективности работы
   - Экспорт данных в различные форматы (PDF, Excel)

3. **Улучшение существующих функций**
   - Расширенный поиск и фильтрация объектов
   - Интеллектуальный поиск по всем сущностям приложения
   - Система тегов для объектов, клиентов и встреч
   - Продвинутая сортировка с возможностью настройки пользователем
   - Улучшенный календарь встреч с напоминаниями
   - Система заметок для объектов и клиентов
   - Чек-листы для просмотров объектов
   - Расширение функциональности карт с возможностью поиска объектов на карте

4. **Технические улучшения**
   - Реализация синхронизации данных с облаком (Supabase):
     - Настройка API для синхронизации
     - Механизм разрешения конфликтов при синхронизации
     - Возможность работы в офлайн-режиме с последующей синхронизацией
   - Внедрение многопользовательского режима
   - Улучшение безопасности и защиты данных

## Безопасность и шифрование данных

В приложении реализована система безопасности для защиты конфиденциальных данных пользователей и объектов недвижимости.

### Шифрование базы данных

Основой системы безопасности является шифрование локальной базы данных с использованием SQLCipher:

```kotlin
// Пример конфигурации шифрованной базы данных в AppDatabase
val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
val factory = SupportFactory(passphrase)

Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    DATABASE_NAME
)
.openHelperFactory(factory)
.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
.build()
```

### Компоненты безопасности

- `DatabaseEncryption` - класс для управления ключами шифрования и паролями базы данных
- Интеграция с Android Keystore для безопасного хранения криптографических ключей
- Механизмы восстановления при повреждении ключей шифрования

```kotlin
class DatabaseEncryption @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val KEY_ALIAS = "database_encryption_key"
        private const val PREF_NAME = "database_security_prefs"
        private const val PREF_PASSWORD = "database_password"
        private const val DEFAULT_PASSWORD = "default_secure_password"
    }
    
    private val keyStore by lazy {
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }
    
    private val securePreferences by lazy {
        EncryptedSharedPreferences.create(
            PREF_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    /**
     * Получает пароль для шифрования базы данных
     */
    fun getDatabasePassword(): String {
        // Если пароль уже сохранен, возвращаем его
        if (securePreferences.contains(PREF_PASSWORD)) {
            return securePreferences.getString(PREF_PASSWORD, DEFAULT_PASSWORD) ?: DEFAULT_PASSWORD
        }
        
        // Генерируем новый пароль и сохраняем его
        val password = generateSecurePassword()
        securePreferences.edit().putString(PREF_PASSWORD, password).apply()
        return password
    }
    
    /**
     * Генерирует криптографически стойкий пароль
     */
    private fun generateSecurePassword(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    /**
     * Сбрасывает пароль и ключи шифрования в случае проблем
     */
    fun resetSecurityKeys() {
        try {
            // Удаляем ключ из KeyStore
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
            
            // Сбрасываем пароль
            securePreferences.edit().remove(PREF_PASSWORD).apply()
            
            // Генерируем новый пароль
            val newPassword = generateSecurePassword()
            securePreferences.edit().putString(PREF_PASSWORD, newPassword).apply()
        } catch (e: Exception) {
            Log.e("DatabaseEncryption", "Ошибка при сбросе ключей безопасности", e)
            
            // В случае фатальной ошибки пытаемся сбросить все настройки
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply()
        }
    }
}
```

### Защита конфиденциальных данных

Приложение обеспечивает безопасность через:

- Шифрование всех конфиденциальных данных в базе данных с использованием SQLCipher
- Использование EncryptedSharedPreferences для хранения чувствительных настроек
- Механизмы очистки кэша и временных файлов при выходе из приложения
- Защиту от экспорта незашифрованных данных

### Безопасность медиафайлов

Для защиты фотографий и документов применяются следующие меры:

- Хранение в приватной директории приложения, недоступной другим приложениям
- Контролируемый доступ к файлам через FileProvider
- Безопасное удаление файлов при удалении связанных с ними записей
- Шифрование особо важных документов с помощью AES шифрования

### Аварийное восстановление

В приложении реализован механизм восстановления при нарушении целостности криптографических ключей или повреждении базы данных:

```kotlin
try {
    // Используем зашифрованную базу данных
    database.openHelper.readableDatabase
} catch (e: Exception) {
    // Обрабатываем ошибки целостности ключей
    if (e is AEADBadTagException || e.cause is AEADBadTagException) {
        // Сброс повреждённых ключей и восстановление хранилища
        databaseEncryption.resetSecurityKeys()
        
        // Пытаемся пересоздать базу данных
        resetDatabaseFiles(context)
        
        // Создаем новый экземпляр базы данных с новыми ключами
        createDatabaseWithNewKeys(context, databaseEncryption)
    } else {