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
  - `bookingRepositoryImpl` - имплементация репозитория с бизнес-логикой

## Модель данных

### Основные модели данных

### Property (Объект недвижимости)

```kotlin
data class Property(
    val id: String = "",
    val contactName: String = "",
    val contactPhone: String = "",
    val additionalContactInfo: String? = null,
    
    // Характеристики объекта
    val propertyType: String = "", // Дом, квартира, коммерческая, земля и т.д.
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val district: String = "",
    val nearbyObjects: String = "",
    val views: String = "",
    val area: Double = 0.0,
    val roomsCount: Int = 0,
    val isStudio: Boolean = false,
    val layout: String = "",
    val floor: Int = 0,
    val totalFloors: Int = 0,
    val description: String? = null,
    val management: String = "",
    
    // Дополнительные характеристики для домов и коттеджей
    val levelsCount: Int? = null,
    val landArea: Double? = null,
    val hasGarage: Boolean = false,
    val garageSpaces: Int? = null,
    val hasBathhouse: Boolean = false,
    val hasPool: Boolean = false,
    val poolType: String? = null,
    
    // Детали отделки и удобств
    val repairState: String = "",
    val bedsCount: Int? = null,
    val bathroomsCount: Int? = null,
    val bathroomType: String? = null,
    val noFurniture: Boolean = false,
    val hasAppliances: Boolean = false,
    val heatingType: String? = null,
    val balconiesCount: Int? = null,
    
    // Системы коммуникаций
    val hasElectricity: Boolean = true,
    val hasWater: Boolean = true,
    val hasSewerage: Boolean = true,
    val hasGas: Boolean = false,
    val hasInternet: Boolean = false,
    val gasType: String? = null,
    val waterType: String? = null,
    val sewerageType: String? = null,
    
    // Информация о сделке
    val saleStatus: Boolean = true,
    val rentStatus: Boolean = false,
    val rentalType: String? = null, // "long_term" или "daily"
    val salePrice: Double? = null,
    val rentalPrice: Double? = null,
    val rentalPriceDaily: Double? = null,
    val rentalConditions: String? = null,
    val utilityPayments: Boolean? = null,
    val commission: Double? = null,
    val minimumRentalPeriod: Int? = null,
    val seasonalPricing: Boolean = false,
    val seasonalPrices: List<SeasonalPrice> = emptyList(),
    
    // Финансовая и юридическая информация
    val cadastralNumber: String? = null,
    val ownershipType: String? = null,
    val documents: List<String>? = null,
    val encumbrances: Boolean = false,
    
    // Медиа
    val photos: List<String> = emptyList(),
    val featuredPhotoIndex: Int? = null,
    val videos: List<String> = emptyList(),
    val virtualTourUrl: String? = null,
    
    // Метаданные
    val notes: String? = null,
    val features: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastVisit: Long? = null,
    val isFavorite: Boolean = false,
    val isSynced: Boolean = false
)
```

### Client (Клиент)

```kotlin
data class Client(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String? = null,
    val notes: String? = null,
    
    // Информация о предпочтениях
    val leadSource: String? = null,
    val status: String = "active", // active, inactive, potential, archived
    val budget: Double? = null,
    val preferences: List<String> = emptyList(),
    val searchCriteria: String? = null,
    
    // Типы сделок, которые интересуют клиента
    val interestedInBuying: Boolean = false,
    val interestedInRenting: Boolean = false,
    val interestedInLongTerm: Boolean = false,
    val interestedInShortTerm: Boolean = false,
    
    // Предпочитаемые типы недвижимости
    val preferredPropertyTypes: List<String> = emptyList(),
    val preferredDistricts: List<String> = emptyList(),
    val minRooms: Int? = null,
    val maxRooms: Int? = null,
    val minArea: Double? = null,
    val maxArea: Double? = null,
    
    // Метаданные
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastContact: Long? = null,
    val isSynced: Boolean = false
)
```

### Appointment (Встреча/показ)

```kotlin
data class Appointment(
    val id: String = "",
    val clientId: String = "",
    val propertyId: String = "",
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .build()
        } catch (e: Exception) {
            // Обработка ошибок инициализации базы данных
            Log.e("DatabaseModule", "Ошибка при инициализации базы данных", e)
            
            // Если возникает проблема с шифрованием, пробуем сбросить ключи и создать новую БД
            databaseEncryption.resetSecurityKeys()
            
            // В случае критической ошибки, создаем БД без шифрования
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
            .fallbackToDestructiveMigration() // В крайнем случае, пересоздаем БД
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
   - Улучшение безопасности и защиты данных 