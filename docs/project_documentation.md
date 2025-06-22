# RealEstateAssistant PRO - Техническая документация проекта

## 1. Обзор проекта

### 1.1. Назначение приложения
RealEstateAssistant PRO - это профессиональное мобильное приложение для агентов по недвижимости, которое помогает управлять объектами недвижимости, клиентами и встречами. Приложение предназначено для оптимизации рабочих процессов риелторов и повышения эффективности их работы.

### 1.2. Архитектура
Приложение разработано с использованием принципов Clean Architecture в сочетании с паттерном MVVM (Model-View-ViewModel), что обеспечивает четкое разделение ответственности и упрощает тестирование. Архитектура состоит из трех основных слоев:
- **Presentation** (`app/src/main/java/com/realestateassistant/pro/presentation/`): пользовательский интерфейс, экраны, компоненты и ViewModels
- **Domain** (`app/src/main/java/com/realestateassistant/pro/domain/`): бизнес-логика, модели предметной области и use cases
- **Data** (`app/src/main/java/com/realestateassistant/pro/data/`): источники данных, репозитории и локальное хранилище

### 1.3. Структура проекта
```
app/src/main/java/com/realestateassistant/pro/
├── core/                 # Базовые компоненты и утилиты
│   ├── analytics/        # Аналитика
│   ├── date/             # Утилиты для работы с датами
│   ├── error/            # Обработка ошибок
│   ├── file/             # Работа с файлами
│   ├── image/            # Работа с изображениями
│   ├── logging/          # Логирование
│   ├── navigation/       # Базовые компоненты навигации
│   ├── network/          # Сетевые компоненты
│   ├── permission/       # Управление разрешениями
│   ├── resource/         # Работа с ресурсами
│   ├── result/           # Результаты операций
│   ├── security/         # Компоненты безопасности
│   ├── settings/         # Настройки приложения
│   ├── state/            # Управление состоянием
│   ├── theme/            # Темизация
│   ├── util/             # Общие утилиты
│   └── viewmodel/        # Базовый функционал ViewModel
├── data/                 # Слой данных
│   ├── api/              # API интерфейсы и реализации
│   ├── local/            # Локальное хранилище
│   │   ├── converter/    # Конвертеры типов Room
│   │   ├── dao/          # Data Access Objects
│   │   ├── database/     # Конфигурация базы данных
│   │   ├── entity/       # Entity классы
│   │   ├── mapper/       # Мапперы для преобразования Entity в Domain
│   │   ├── preferences/  # Настройки приложения
│   │   └── security/     # Шифрование базы данных
│   ├── mapper/           # Преобразование моделей между слоями
│   ├── model/            # Модели данных для слоя Data
│   ├── remote/           # Удаленные источники данных
│   ├── repository/       # Реализации репозиториев
│   ├── service/          # Сервисы для работы с данными
│   ├── sync/             # Синхронизация данных
│   └── worker/           # Фоновые задачи
├── di/                   # Dependency Injection
│   ├── AppModule.kt
│   ├── AppointmentModule.kt
│   ├── BookingModule.kt
│   ├── ClientModule.kt
│   ├── DatabaseModule.kt
│   ├── DocumentModule.kt
│   ├── PropertyModule.kt
│   ├── RepositoryModule.kt
│   ├── UseCaseModule.kt
│   ├── ValidationModule.kt
│   └── WorkerModule.kt
├── domain/               # Доменный слой
│   ├── model/            # Доменные модели
│   │   ├── dashboard/    # Модели для дашборда
│   │   └── validation/   # Модели для валидации
│   ├── repository/       # Интерфейсы репозиториев
│   └── usecase/          # Use cases
│       ├── appointment/  # Use cases для встреч
│       ├── booking/      # Use cases для бронирований
│       ├── client/       # Use cases для клиентов
│       ├── dashboard/    # Use cases для дашборда
│       ├── document/     # Use cases для документов
│       ├── property/     # Use cases для объектов недвижимости
│       └── recommendation/ # Use cases для рекомендаций
├── navigation/           # Навигация
│   ├── components/       # Компоненты навигации
│   ├── graphs/           # Навигационные графы
│   ├── model/            # Модели для навигации
│   ├── routes/           # Маршруты
│   └── utils/            # Утилиты для навигации
├── presentation/         # Слой представления
│   ├── components/       # UI-компоненты
│   │   ├── appointment/  # Компоненты для встреч
│   │   ├── calendar/     # Календарные компоненты
│   │   ├── client/       # Компоненты для клиентов
│   │   ├── common/       # Общие компоненты
│   │   ├── dashboard/    # Компоненты для дашборда
│   │   ├── drawer/       # Компоненты для бокового меню
│   │   ├── map/          # Компоненты для карты
│   │   ├── photo/        # Компоненты для работы с фото
│   │   └── property/     # Компоненты для объектов недвижимости
│   ├── model/            # Модели UI слоя
│   ├── screens/          # Экраны приложения
│   │   ├── about/        # Экран "О приложении"
│   │   ├── appointment/  # Экраны для встреч
│   │   ├── booking/      # Экраны для бронирований
│   │   ├── calendar/     # Экраны календаря
│   │   ├── client/       # Экраны для клиентов
│   │   ├── dashboard/    # Экран дашборда
│   │   ├── help/         # Экран помощи
│   │   ├── property/     # Экраны для объектов недвижимости
│   │   ├── recommendation/ # Экраны рекомендаций
│   │   └── settings/     # Экран настроек
│   ├── state/            # Состояния UI
│   ├── theme/            # Темы приложения
│   ├── utils/            # Утилиты для UI
│   └── viewmodel/        # ViewModels
└── ui/                   # Базовые UI-компоненты
    ├── icons/            # Иконки
    └── theme/            # Базовые темы и стили
```

### 1.4. Основные модули
1. **Управление объектами недвижимости**: добавление, редактирование, просмотр и фильтрация объектов
2. **Управление клиентами**: работа с базой данных клиентов, их предпочтениями и требованиями
3. **Встречи и показы**: планирование и управление встречами с клиентами
4. **Бронирования**: управление бронированиями для краткосрочной аренды
5. **Дэшборд**: обзорная панель с ключевыми метриками и предстоящими событиями

### 1.5. Технологический стек
- **Язык программирования**: Kotlin 1.9.0
- **Минимальная версия SDK**: 26 (Android 8.0)
- **Целевая версия SDK**: 35 (Android 15)
- **UI Framework**: Jetpack Compose 1.6.0
- **Архитектура**: Clean Architecture + MVVM
- **Dependency Injection**: Dagger Hilt 2.48
- **База данных**: Room 2.6.1 + SQLCipher 4.5.4
- **Асинхронное программирование**: Kotlin Coroutines и Flow
- **Навигация**: Navigation Compose 2.8.5
- **Постоянное хранилище**: DataStore Preferences 1.0.0
- **Работа с изображениями**: Coil 2.5.0
- **Геолокация и карты**: Yandex MapKit 4.15.0-lite
- **Работа с файлами**: Android FileProvider, Security-Crypto 1.1.0-alpha06
- **Логирование**: Timber 4.7.1
- **Фоновые задачи**: WorkManager 2.9.0
- **Календарь**: Calendar Compose 2.5.0
- **Компоненты Jetpack**: 
  - Core-KTX
  - Lifecycle Runtime KTX
  - Activity Compose
  - Material 3

## 2. Подробная архитектура

### 2.1. Слой представления (Presentation)
Расположен в `app/src/main/java/com/realestateassistant/pro/presentation/` и отвечает за отображение данных и взаимодействие с пользователем. Разработан на основе Jetpack Compose.

#### 2.1.1. MVVM паттерн
- **ViewModel** (`app/src/main/java/com/realestateassistant/pro/presentation/viewmodels/`): 
  - Содержит бизнес-логику для управления состоянием UI
  - Наследуется от базового класса `BaseViewModel` (`app/src/main/java/com/realestateassistant/pro/core/viewmodel/BaseViewModel.kt`)
  - Получает данные через use cases и публикует их в виде состояний через StateFlow
  - Пример: `PropertyViewModel` (`app/src/main/java/com/realestateassistant/pro/presentation/viewmodels/PropertyViewModel.kt`)
  
- **Screens** (`app/src/main/java/com/realestateassistant/pro/presentation/screens/`): 
  - Экраны приложения, реализованные с помощью Jetpack Compose
  - Организованы по функциональным модулям
  - Пример: `PropertyDetailScreen` (`app/src/main/java/com/realestateassistant/pro/presentation/screens/PropertyDetailScreen.kt`)
  
- **Components** (`app/src/main/java/com/realestateassistant/pro/presentation/components/`):
  - Переиспользуемые UI-компоненты
  - Организованы по функциональным модулям
  - Пример: `PropertyCard` (`app/src/main/java/com/realestateassistant/pro/presentation/components/property/PropertyCard.kt`)

- **State** (`app/src/main/java/com/realestateassistant/pro/presentation/state/`):
  - Классы, представляющие состояние UI
  - Пример: `AppointmentState` (`app/src/main/java/com/realestateassistant/pro/presentation/state/appointment/AppointmentState.kt`)

#### 2.1.2. Навигация
Реализована с использованием Navigation Compose. Структура включает:

- **Маршруты** (`app/src/main/java/com/realestateassistant/pro/navigation/routes/AppRoutes.kt`):
  - Определяют пути навигации в приложении
  - Включают паттерн для параметризованных маршрутов

- **Навигационные графы** (`app/src/main/java/com/realestateassistant/pro/navigation/graphs/`):
  - Организуют экраны в логические группы
  - Основной граф: `AppNavHost` (`app/src/main/java/com/realestateassistant/pro/navigation/AppNavHost.kt`)

- **Навигационные компоненты** (`app/src/main/java/com/realestateassistant/pro/navigation/components/`):
  - Включают боковое меню (`DrawerContent.kt`)
  - Нижнюю навигацию (`BottomNavBar.kt`)
  - Верхнюю панель приложения (`AppTopBar.kt`)

#### 2.1.3. Состояния экранов
Для каждого экрана определены специальные классы состояний:

- **Состояния форм**:
  - `PropertyFormState` (`app/src/main/java/com/realestateassistant/pro/presentation/state/property/PropertyFormState.kt`) - состояние формы редактирования объекта
  - `ClientFormState` (`app/src/main/java/com/realestateassistant/pro/presentation/state/client/ClientFormState.kt`) - состояние формы редактирования клиента

- **Состояния списков**:
  - `PropertyListState` (`app/src/main/java/com/realestateassistant/pro/presentation/state/property/PropertyListState.kt`) - состояние списка объектов
  - `ClientListState` (`app/src/main/java/com/realestateassistant/pro/presentation/state/client/ClientListState.kt`) - состояние списка клиентов
  
- **Состояния детальных экранов**:
  - `PropertyDetailState` (`app/src/main/java/com/realestateassistant/pro/presentation/state/property/PropertyDetailState.kt`) - состояние детального просмотра объекта
  - `AppointmentState` (`app/src/main/java/com/realestateassistant/pro/presentation/state/appointment/AppointmentState.kt`) - состояние встречи

Состояния публикуются через StateFlow в ViewModel:
```kotlin
// Пример из PropertyViewModel.kt
private val _propertyDetailState = MutableStateFlow(PropertyDetailState())
val propertyDetailState: StateFlow<PropertyDetailState> = _propertyDetailState.asStateFlow()
```

И собираются экранами с помощью `collectAsState()`:
```kotlin
// Пример из PropertyDetailScreen.kt
val state by viewModel.propertyDetailState.collectAsState()
```

### 2.2. Доменный слой (Domain)
Расположен в `app/src/main/java/com/realestateassistant/pro/domain/`. Содержит бизнес-логику и правила приложения, не зависит от конкретных реализаций фреймворков.

#### 2.2.1. Модели (Models)
Основные сущности предметной области (`app/src/main/java/com/realestateassistant/pro/domain/model/`):

- **Property** (`app/src/main/java/com/realestateassistant/pro/domain/model/Property.kt`): 
```kotlin
data class Property(
    val id: String = "",
      val ownerName: String = "",
      val ownerContacts: List<String> = emptyList(),
    val address: String = "",
    val district: String = "",
      val coordinates: LatLng? = null,
      val propertyType: PropertyType = PropertyType.APARTMENT,
      val rentalType: RentalType = RentalType.LONG_TERM,
      val status: PropertyStatus = PropertyStatus.AVAILABLE,
      // ... другие свойства объекта недвижимости
  )
  ```

- **Client** (`app/src/main/java/com/realestateassistant/pro/domain/model/Client.kt`):
```kotlin
data class Client(
    val id: String = "",
    val fullName: String = "",
      val phones: List<String> = emptyList(),
      val email: String = "",
      val preferredCommunication: CommunicationType = CommunicationType.PHONE,
      val rentalType: RentalType = RentalType.LONG_TERM,
      val desiredPropertyTypes: List<PropertyType> = emptyList(),
      val preferredDistricts: List<String> = emptyList(),
      // ... другие свойства клиента
  )
  ```

- **Appointment** (`app/src/main/java/com/realestateassistant/pro/domain/model/Appointment.kt`):
```kotlin
data class Appointment(
    val id: String = "",
    val title: String = "",
      val description: String = "",
      val startTime: Long = 0,  // Unix timestamp
      val endTime: Long = 0,    // Unix timestamp
      val location: String = "",
      val clientId: String? = null,
      val propertyId: String? = null,
      val status: AppointmentStatus = AppointmentStatus.PLANNED,
    val type: AppointmentType = AppointmentType.SHOWING,
      // ... другие свойства встречи
  )
  ```

- **Booking** (`app/src/main/java/com/realestateassistant/pro/domain/model/Booking.kt`):
```kotlin
data class Booking(
    val id: String = "",
      val propertyId: String = "",
      val clientId: String? = null,
      val clientName: String = "",
      val startDate: Long = 0,  // Unix timestamp
      val endDate: Long = 0,    // Unix timestamp
    val status: BookingStatus = BookingStatus.PENDING,
      val totalPrice: Double = 0.0,
      // ... другие свойства бронирования
  )
  ```

- Дополнительные типы и константы:
  - `PropertyType` (`app/src/main/java/com/realestateassistant/pro/domain/model/Property.kt`)
  - `RentalType` (`app/src/main/java/com/realestateassistant/pro/domain/model/Property.kt`)
  - `PropertyStatus` (`app/src/main/java/com/realestateassistant/pro/domain/model/PropertyStatus.kt`)
  - `AppointmentStatus` (`app/src/main/java/com/realestateassistant/pro/domain/model/AppointmentStatus.kt`)
  - `AppointmentType` (`app/src/main/java/com/realestateassistant/pro/domain/model/AppointmentType.kt`)
  - `BookingStatus` (`app/src/main/java/com/realestateassistant/pro/domain/model/Booking.kt`)

#### 2.2.2. Use Cases
Бизнес-логика, инкапсулированная в виде отдельных операций (`app/src/main/java/com/realestateassistant/pro/domain/usecase/`):

- **PropertyUseCases** (`app/src/main/java/com/realestateassistant/pro/domain/usecase/property/`):
  - `AddPropertyUseCase` - добавление нового объекта
```kotlin
    class AddPropertyUseCase @Inject constructor(
        private val repository: PropertyRepository,
        private val imageStorage: ImageStorage
    ) {
        suspend operator fun invoke(property: Property, images: List<Uri>): Result<String> {
            return try {
                // Логика сохранения изображений и объекта недвижимости
                val savedImagePaths = imageStorage.saveImages(images, "properties/${property.id}")
                val propertyWithImages = property.copy(photos = savedImagePaths)
                val id = repository.addProperty(propertyWithImages)
                Result.Success(id)
            } catch (e: Exception) {
                Result.Error(e)
        }
    }
}
```
  - `UpdatePropertyUseCase` - обновление информации об объекте
  - `GetPropertyUseCase` - получение объекта по ID
  - `DeletePropertyUseCase` - удаление объекта
  - `GetAllPropertiesUseCase` - получение всех объектов
  - `ObserveAllPropertiesUseCase` - наблюдение за изменениями в списке объектов
  - `GetPropertiesByStatusUseCase` - получение объектов по статусу
  - `GetPropertiesWithBookingsUseCase` - получение объектов с бронированиями
  - `UpdatePropertyStatusesUseCase` - обновление статусов объектов
  - `FilterPropertiesUseCase` - фильтрация объектов

- **ClientUseCases** (`app/src/main/java/com/realestateassistant/pro/domain/usecase/client/`):
  - `AddClientUseCase` - добавление нового клиента
  - `UpdateClientUseCase` - обновление информации о клиенте
  - `GetClientUseCase` - получение клиента по ID
  - `GetAllClientsUseCase` - получение всех клиентов
  - `SearchClientsUseCase` - поиск клиентов
  - `DeleteClientUseCase` - удаление клиента

- **AppointmentUseCases** (`app/src/main/java/com/realestateassistant/pro/domain/usecase/appointment/`):
  - `AddAppointmentUseCase` - создание новой встречи
  - `UpdateAppointmentUseCase` - обновление информации о встрече
  - `GetAppointmentsForDateUseCase` - получение встреч на определенную дату
  - `GetClientAppointmentsUseCase` - получение встреч для клиента
  - `GetPropertyAppointmentsUseCase` - получение встреч для объекта
  - `UpdateAppointmentStatusUseCase` - обновление статуса встречи

- **BookingUseCases** (`app/src/main/java/com/realestateassistant/pro/domain/usecase/booking/`):
  - `CreateBookingUseCase` - создание нового бронирования
  - `UpdateBookingUseCase` - обновление информации о бронировании
  - `GetBookingsForPropertyUseCase` - получение бронирований для объекта
  - `CheckAvailabilityUseCase` - проверка доступности объекта
  - `CancelBookingUseCase` - отмена бронирования

- **RСecommendationUseCases** (`app/src/main/java/com/realestateassistant/pro/domain/usecase/recommendation/`):
  - `GetPropertiesForClientUseCase` - получение подходящих объектов для клиента
  - `CalculateMatchScoreUseCase` - расчет оценки соответствия

#### 2.2.3. Репозитории (Interfaces)
Абстракции для доступа к данным (`app/src/main/java/com/realestateassistant/pro/domain/repository/`):

- **PropertyRepository** (`app/src/main/java/com/realestateassistant/pro/domain/repository/PropertyRepository.kt`):
```kotlin
  interface PropertyRepository {
      suspend fun addProperty(property: Property): String
      suspend fun updateProperty(property: Property)
      suspend fun getProperty(id: String): Property?
      suspend fun deleteProperty(id: String)
      fun getAllProperties(): Flow<List<Property>>
      fun getPropertiesByStatus(status: PropertyStatus): Flow<List<Property>>
      suspend fun updatePropertyStatus(id: String, status: PropertyStatus)
      // ... другие методы
  }
  ```

- **ClientRepository** (`app/src/main/java/com/realestateassistant/pro/domain/repository/ClientRepository.kt`):
```kotlin
  interface ClientRepository {
      suspend fun addClient(client: Client): String
      suspend fun updateClient(client: Client)
      suspend fun getClient(id: String): Client?
      suspend fun deleteClient(id: String)
      fun getAllClients(): Flow<List<Client>>
      fun searchClients(query: String): Flow<List<Client>>
      // ... другие методы
  }
  ```

- **AppointmentRepository** (`app/src/main/java/com/realestateassistant/pro/domain/repository/AppointmentRepository.kt`):
```kotlin
  interface AppointmentRepository {
      suspend fun addAppointment(appointment: Appointment): String
      suspend fun updateAppointment(appointment: Appointment)
      suspend fun getAppointment(id: String): Appointment?
      suspend fun deleteAppointment(id: String)
      fun getAllAppointments(): Flow<List<Appointment>>
      fun getAppointmentsForDate(date: Long): Flow<List<Appointment>>
      fun getAppointmentsForClient(clientId: String): Flow<List<Appointment>>
      // ... другие методы
  }
  ```

- **BookingRepository** (`app/src/main/java/com/realestateassistant/pro/domain/repository/BookingRepository.kt`):
```kotlin
  interface BookingRepository {
      suspend fun createBooking(booking: Booking): String
      suspend fun updateBooking(booking: Booking)
      suspend fun getBooking(id: String): Booking?
      suspend fun deleteBooking(id: String)
      fun getBookingsForProperty(propertyId: String): Flow<List<Booking>>
      fun getActiveBookings(): Flow<List<Booking>>
      // ... другие методы
  }
  ```

### 2.3. Слой данных (Data)
Расположен в `app/src/main/java/com/realestateassistant/pro/data/`. Отвечает за получение и сохранение данных, реализует интерфейсы репозиториев из доменного слоя.

#### 2.3.1. Локальное хранилище
Локальное хранилище организовано с использованием Room и шифрования SQLCipher:

- **Room Database** (`app/src/main/java/com/realestateassistant/pro/data/local/AppDatabase.kt`):
```kotlin
  @Database(
      entities = [
          PropertyEntity::class,
          ClientEntity::class,
          AppointmentEntity::class,
          BookingEntity::class
      ],
      version = 12,
      exportSchema = true
  )
  @TypeConverters(
      ListStringConverter::class,
      SeasonalPriceListConverter::class,
      AppointmentStatusConverter::class,
      AppointmentTypeConverter::class,
      AppointmentConverters::class,
      BookingConverters::class
  )
  abstract class AppDatabase : RoomDatabase() {
      abstract fun propertyDao(): PropertyDao
      abstract fun clientDao(): ClientDao
      abstract fun appointmentDao(): AppointmentDao
      abstract fun bookingDao(): BookingDao
      
      // ... инициализация и вспомогательные методы
  }
  ```

- **Data Access Objects (DAO)** (`app/src/main/java/com/realestateassistant/pro/data/local/dao/`):
  - `PropertyDao` (`app/src/main/java/com/realestateassistant/pro/data/local/dao/PropertyDao.kt`):
```kotlin
    @Dao
    interface PropertyDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertProperty(property: PropertyEntity): Long
        
        @Update
        suspend fun updateProperty(property: PropertyEntity)
        
        @Query("SELECT * FROM properties WHERE id = :propertyId")
        suspend fun getProperty(propertyId: String): PropertyEntity?
        
        @Query("DELETE FROM properties WHERE id = :propertyId")
        suspend fun deleteProperty(propertyId: String)
        
        @Query("SELECT * FROM properties")
        fun getAllProperties(): Flow<List<PropertyEntity>>
        
        @Query("SELECT * FROM properties WHERE status = :status")
        fun getPropertiesByStatus(status: PropertyStatus): Flow<List<PropertyEntity>>
        
        // ... другие методы
    }
    ```
  
  - `ClientDao` (`app/src/main/java/com/realestateassistant/pro/data/local/dao/ClientDao.kt`)
  - `AppointmentDao` (`app/src/main/java/com/realestateassistant/pro/data/local/dao/AppointmentDao.kt`)
  - `BookingDao` (`app/src/main/java/com/realestateassistant/pro/data/local/dao/BookingDao.kt`)

- **Entity классы** (`app/src/main/java/com/realestateassistant/pro/data/local/entity/`):
  - `PropertyEntity` (`app/src/main/java/com/realestateassistant/pro/data/local/entity/PropertyEntity.kt`):
```kotlin
    @Entity(tableName = "properties")
    data class PropertyEntity(
        @PrimaryKey
        val id: String = UUID.randomUUID().toString(),
        val ownerName: String = "",
        val ownerContacts: List<String> = emptyList(),
        val address: String = "",
        val district: String = "",
        val latitude: Double? = null,
        val longitude: Double? = null,
        val propertyType: PropertyType = PropertyType.APARTMENT,
        val rentalType: RentalType = RentalType.LONG_TERM,
        val status: PropertyStatus = PropertyStatus.AVAILABLE,
        // ... другие поля объекта недвижимости
    )
    ```
  
  - `ClientEntity` (`app/src/main/java/com/realestateassistant/pro/data/local/entity/ClientEntity.kt`)
  - `AppointmentEntity` (`app/src/main/java/com/realestateassistant/pro/data/local/entity/AppointmentEntity.kt`)
  - `BookingEntity` (`app/src/main/java/com/realestateassistant/pro/data/local/entity/BookingEntity.kt`)
  - `SeasonalPriceEntity` (`app/src/main/java/com/realestateassistant/pro/data/local/entity/SeasonalPriceEntity.kt`)

- **Конвертеры типов** (`app/src/main/java/com/realestateassistant/pro/data/local/converter/` и `app/src/main/java/com/realestateassistant/pro/data/local/converters/`):
  - `ListStringConverter` - конвертация списков строк
  - `SeasonalPriceListConverter` - конвертация списков сезонных цен
  - `AppointmentStatusConverter` - конвертация статусов встреч
  - `AppointmentTypeConverter` - конвертация типов встреч
  - `BookingConverters` - конвертация типов бронирований

- **Шифрование базы данных** (`app/src/main/java/com/realestateassistant/pro/data/local/security/`):
  - `DatabaseEncryption` (`app/src/main/java/com/realestateassistant/pro/data/local/security/DatabaseEncryption.kt`) - класс для управления ключами шифрования

- **Preferences** (`app/src/main/java/com/realestateassistant/pro/data/local/preferences/`):
  - `AppPreferences` - интерфейс для работы с настройками приложения
  - `AppPreferencesImpl` - реализация на DataStore Preferences

#### 2.3.2. Репозитории (Implementations)
Реализации интерфейсов репозиториев из доменного слоя (`app/src/main/java/com/realestateassistant/pro/data/repository/`):

- **PropertyRepositoryImpl** (`app/src/main/java/com/realestateassistant/pro/data/repository/PropertyRepositoryImpl.kt`):
```kotlin
  @Singleton
  class PropertyRepositoryImpl @Inject constructor(
      private val propertyDao: PropertyDao,
      private val propertyMapper: PropertyMapper
  ) : PropertyRepository {
      
      override suspend fun addProperty(property: Property): String {
          val entity = propertyMapper.toEntity(property)
          propertyDao.insertProperty(entity)
          return entity.id
      }
      
      override suspend fun updateProperty(property: Property) {
          propertyDao.updateProperty(propertyMapper.toEntity(property))
      }
      
      override suspend fun getProperty(id: String): Property? {
          return propertyDao.getProperty(id)?.let { propertyMapper.mapToDomain(it) }
      }
      
      override suspend fun deleteProperty(id: String) {
          propertyDao.deleteProperty(id)
      }
      
      override fun getAllProperties(): Flow<List<Property>> {
          return propertyDao.getAllProperties().map { entities ->
              entities.map { propertyMapper.mapToDomain(it) }
          }
      }
      
      override fun getPropertiesByStatus(status: PropertyStatus): Flow<List<Property>> {
          return propertyDao.getPropertiesByStatus(status).map { entities ->
              entities.map { propertyMapper.mapToDomain(it) }
          }
      }
      
      // ... другие методы
  }
  ```
  
- **ClientRepositoryImpl** (`app/src/main/java/com/realestateassistant/pro/data/repository/ClientRepositoryImpl.kt`)
- **AppointmentRepositoryImpl** (`app/src/main/java/com/realestateassistant/pro/data/repository/AppointmentRepositoryImpl.kt`)
- **BookingRepositoryImpl** (`app/src/main/java/com/realestateassistant/pro/data/repository/BookingRepositoryImpl.kt`)

#### 2.3.3. Мапперы
Классы для преобразования между Entity и Domain моделями (`app/src/main/java/com/realestateassistant/pro/data/mapper/`):

- **PropertyMapper** (`app/src/main/java/com/realestateassistant/pro/data/mapper/PropertyMapper.kt`):
```kotlin
  @Singleton
  class PropertyMapper @Inject constructor() {
      fun mapToDomain(entity: PropertyEntity): Property {
          return Property(
              id = entity.id,
              ownerName = entity.ownerName,
              ownerContacts = entity.ownerContacts,
              address = entity.address,
              district = entity.district,
              coordinates = if (entity.latitude != null && entity.longitude != null) {
                  LatLng(entity.latitude, entity.longitude)
              } else null,
              propertyType = entity.propertyType,
              rentalType = entity.rentalType,
              status = entity.status,
              // ... маппинг других полей
          )
      }
      
      fun toEntity(domain: Property): PropertyEntity {
          return PropertyEntity(
              id = domain.id.ifEmpty { UUID.randomUUID().toString() },
              ownerName = domain.ownerName,
              ownerContacts = domain.ownerContacts,
              address = domain.address,
              district = domain.district,
              latitude = domain.coordinates?.latitude,
              longitude = domain.coordinates?.longitude,
              propertyType = domain.propertyType,
              rentalType = domain.rentalType,
              status = domain.status,
              // ... маппинг других полей
          )
    }
}
```

- **ClientMapper** (`app/src/main/java/com/realestateassistant/pro/data/mapper/ClientMapper.kt`)
- **AppointmentMapper** (`app/src/main/java/com/realestateassistant/pro/data/mapper/AppointmentMapper.kt`)
- **BookingMapper** (`app/src/main/java/com/realestateassistant/pro/data/mapper/BookingMapper.kt`)

#### 2.3.4. API и сетевые сервисы
Сервисы для работы с внешними API (`app/src/main/java/com/realestateassistant/pro/data/api/` и `app/src/main/java/com/realestateassistant/pro/data/remote/`):

- **YandexGeocoderService** (`app/src/main/java/com/realestateassistant/pro/data/api/YandexGeocoderService.kt`):
```kotlin
  @Singleton
  class YandexGeocoderService @Inject constructor(
      private val client: OkHttpClient,
      private val gson: Gson,
      @Named("yandex_api_key") private val apiKey: String
  ) {
      private val baseUrl = "https://geocode-maps.yandex.ru/1.x/"
      
      suspend fun geocodeAddress(address: String): Result<LatLng> {
          // Запрос к Yandex Geocoder API для получения координат по адресу
      }
      
      suspend fun reverseGeocode(latitude: Double, longitude: Double): Result<String> {
          // Запрос к Yandex Geocoder API для получения адреса по координатам
    }
}
```

- **YandexSuggestService** (`app/src/main/java/com/realestateassistant/pro/data/api/YandexSuggestService.kt`):
```kotlin
@Singleton
  class YandexSuggestService @Inject constructor(
      private val client: OkHttpClient,
      private val gson: Gson,
      @Named("yandex_api_key") private val apiKey: String
  ) {
      private val baseUrl = "https://suggest-maps.yandex.ru/v1/suggest"
      
      suspend fun getSuggestions(query: String): Result<List<AddressSuggestion>> {
          // Запрос к Yandex Suggest API для получения подсказок адресов
    }
}
```

#### 2.3.5. Фоновые задачи и синхронизация
Классы для выполнения фоновых задач (`app/src/main/java/com/realestateassistant/pro/data/worker/`):

- **PropertyStatusUpdateWorker** (`app/src/main/java/com/realestateassistant/pro/data/worker/PropertyStatusUpdateWorker.kt`):
```kotlin
  class PropertyStatusUpdateWorker @AssistedInject constructor(
      @Assisted context: Context,
      @Assisted params: WorkerParameters,
      private val updatePropertyStatusesUseCase: UpdatePropertyStatusesUseCase
  ) : CoroutineWorker(context, params) {
  
      override suspend fun doWork(): Result {
    return try {
              // Обновление статусов объектов на основе текущих бронирований
              updatePropertyStatusesUseCase()
              Result.success()
    } catch (e: Exception) {
              Log.e(TAG, "Error updating property statuses", e)
              Result.retry()
          }
      }
      
      companion object {
          private const val TAG = "PropertyStatusWorker"
          
          fun schedulePeriodicWork(workManager: WorkManager) {
              val constraints = Constraints.Builder()
                  .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                  .setRequiresBatteryNotLow(true)
            .build()
              
              val request = PeriodicWorkRequestBuilder<PropertyStatusUpdateWorker>(
                  4, TimeUnit.HOURS,  // Периодичность выполнения
                  30, TimeUnit.MINUTES // Гибкий интервал
              )
              .setConstraints(constraints)
                    .build()
              
              workManager.enqueueUniquePeriodicWork(
                  "property_status_update",
                  ExistingPeriodicWorkPolicy.UPDATE,
                  request
              )
          }
      }
      
      @Factory
      interface PropertyStatusUpdateWorkerFactory {
          fun create(context: Context, params: WorkerParameters): PropertyStatusUpdateWorker
    }
}
```

### 2.4. Система безопасности и шифрования
Расположена в различных директориях, в основном в `app/src/main/java/com/realestateassistant/pro/data/local/security/` и `app/src/main/java/com/realestateassistant/pro/core/security/`.

#### 2.4.1. Шифрование базы данных
Приложение использует SQLCipher для шифрования локальной базы данных, что обеспечивает защиту конфиденциальной информации о клиентах и объектах недвижимости:

- **DatabaseEncryption** (`app/src/main/java/com/realestateassistant/pro/data/local/security/DatabaseEncryption.kt`):
```kotlin
  @Singleton
class DatabaseEncryption @Inject constructor(
      private val context: Context,
      private val securityManager: SecurityManager
  ) {
      private val securePrefs by lazy {
          try {
        EncryptedSharedPreferences.create(
                  "secure_database_prefs",
                  MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
                } catch (e: Exception) {
              // Если криптографические ключи повреждены, используем обычные SharedPreferences
              // как запасной вариант
              Log.e(TAG, "Failed to create EncryptedSharedPreferences, using regular SharedPreferences", e)
              context.getSharedPreferences("database_backup_prefs", Context.MODE_PRIVATE)
          }
      }
      
      /**
       * Получает пароль для базы данных
       * При первом запуске генерирует надежный пароль и сохраняет его
     */
    fun getDatabasePassword(): String {
          var password = securePrefs.getString(KEY_DATABASE_PASSWORD, null)
          
          if (password == null) {
              // Генерация нового пароля при первом запуске
              password = generateSecurePassword()
              securePrefs.edit().putString(KEY_DATABASE_PASSWORD, password).apply()
              
              // Сохраняем копию пароля в обычных SharedPreferences как резервный вариант
              val backupPrefs = context.getSharedPreferences("database_backup_prefs", Context.MODE_PRIVATE)
              backupPrefs.edit().putString(KEY_DATABASE_PASSWORD_BACKUP, password).apply()
          }
          
        return password
    }
    
    /**
       * Сбрасывает ключи шифрования при критических ошибках
     */
    fun resetSecurityKeys() {
        try {
              securePrefs.edit().clear().apply()
            
              val backupPrefs = context.getSharedPreferences("database_backup_prefs", Context.MODE_PRIVATE)
              backupPrefs.edit().clear().apply()
            
              // Генерация нового пароля
            val newPassword = generateSecurePassword()
              securePrefs.edit().putString(KEY_DATABASE_PASSWORD, newPassword).apply()
              backupPrefs.edit().putString(KEY_DATABASE_PASSWORD_BACKUP, newPassword).apply()
              
              Log.i(TAG, "Security keys reset successfully")
                                    } catch (e: Exception) {
              Log.e(TAG, "Error resetting security keys", e)
          }
      }
      
      /**
       * Генерирует криптографически стойкий пароль
       */
      private fun generateSecurePassword(): String {
          val random = SecureRandom()
          val bytes = ByteArray(32)
          random.nextBytes(bytes)
          return Base64.encodeToString(bytes, Base64.NO_WRAP)
      }
      
      companion object {
          private const val TAG = "DatabaseEncryption"
          private const val KEY_DATABASE_PASSWORD = "db_password"
          private const val KEY_DATABASE_PASSWORD_BACKUP = "db_password_backup"
    }
}
```

- **Интеграция SQLCipher** в `DatabaseModule` (`app/src/main/java/com/realestateassistant/pro/di/DatabaseModule.kt`):
```kotlin
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
          .openHelperFactory(factory)  // Интеграция SQLCipher
          .addMigrations(MIGRATION_1_2, ..., MIGRATION_11_12)
            .build()
        } catch (e: Exception) {
            // Обработка ошибок инициализации базы данных
          Log.e(TAG, "Ошибка при инициализации базы данных", e)
            
          // В случае критической ошибки, сбрасываем ключи и создаем новую БД
            databaseEncryption.resetSecurityKeys()
            
          // Создаем БД без шифрования как запасной вариант
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }
}
```

#### 2.4.2. Защищенное хранилище
Компоненты для безопасного хранения данных:

- **EncryptedSharedPreferences** (`app/src/main/java/com/realestateassistant/pro/data/local/preferences/SecureAppPreferences.kt`):
```kotlin
  @Singleton
  class SecureAppPreferences @Inject constructor(
    private val context: Context
  ) : AppPreferences {
      private val encryptedPreferences by lazy {
          try {
              val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
              
        EncryptedSharedPreferences.create(
                  "secure_app_preferences",
                  masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
                            } catch (e: Exception) {
              Log.e(TAG, "Failed to create encrypted preferences", e)
              // Резервное хранилище
              context.getSharedPreferences("fallback_preferences", Context.MODE_PRIVATE)
          }
      }
      
      // Реализация методов интерфейса AppPreferences
      override suspend fun saveString(key: String, value: String) {
          withContext(Dispatchers.IO) {
              encryptedPreferences.edit().putString(key, value).apply()
          }
      }
      
      override fun getString(key: String, defaultValue: String): Flow<String> {
          return callbackFlow {
              val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
                  if (k == key) {
                      trySend(encryptedPreferences.getString(key, defaultValue) ?: defaultValue)
                  }
              }
              
              encryptedPreferences.registerOnSharedPreferenceChangeListener(listener)
              trySend(encryptedPreferences.getString(key, defaultValue) ?: defaultValue)
              
              awaitClose {
                  encryptedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
              }
          }.flowOn(Dispatchers.IO)
      }
      
      // Другие методы для различных типов данных
      
      companion object {
          private const val TAG = "SecureAppPreferences"
    }
}
```

- **Security-Crypto** для файлов (`app/src/main/java/com/realestateassistant/pro/core/file/FileEncryptionManager.kt`):
```kotlin
  @Singleton
  class FileEncryptionManager @Inject constructor(
      private val context: Context
  ) {
      private val securityManager = SecurityManager()
      
      /**
       * Шифрует файл и сохраняет его в приватной директории приложения
       */
      suspend fun encryptFile(inputFile: File, fileName: String): Result<File> = withContext(Dispatchers.IO) {
          try {
              val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
              val encryptedFile = EncryptedFile.Builder(
                  File(context.filesDir, fileName),
                            context,
                  masterKeyAlias,
                  EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
              ).build()
              
              encryptedFile.openFileOutput().use { outputStream ->
                  inputFile.inputStream().use { inputStream ->
                      inputStream.copyTo(outputStream)
                  }
              }
              
              Result.Success(encryptedFile.file)
                } catch (e: Exception) {
              Log.e(TAG, "Error encrypting file", e)
              Result.Error(e)
          }
      }
      
      /**
       * Расшифровывает файл
       */
      suspend fun decryptFile(encryptedFileName: String): Result<ByteArray> = withContext(Dispatchers.IO) {
          try {
              val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
              val encryptedFile = EncryptedFile.Builder(
                  File(context.filesDir, encryptedFileName),
                  context,
                  masterKeyAlias,
                  EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
              ).build()
              
              val bytes = encryptedFile.openFileInput().use { inputStream ->
                  inputStream.readBytes()
              }
              
              Result.Success(bytes)
                                    } catch (e: Exception) {
              Log.e(TAG, "Error decrypting file", e)
              Result.Error(e)
          }
      }
      
      companion object {
          private const val TAG = "FileEncryptionManager"
    }
}
```

#### 2.4.3. Механизмы восстановления
Реализованы в основном в классах, упомянутых выше:

- **Автоматическое восстановление ключей** при повреждении криптографических ключей:
  - Резервное копирование ключей в обычных SharedPreferences
  - Автоматическое восстановление при проблемах с EncryptedSharedPreferences
  - Обработка исключений для минимизации риска потери данных

- **Полный сброс хранилища** при критических ошибках:
  - Метод `resetDatabaseFiles()` в `DatabaseModule`
  - Метод `resetSecurityKeys()` в `DatabaseEncryption`
  - Резервный режим без шифрования для крайних случаев

- **Механизмы миграции базы данных**:
  - Набор миграций для обновления схемы данных
  - Обработка случаев, когда миграция невозможна
  - Сохранение и восстановление данных при необходимости пересоздания базы

### 2.5 Dependency Injection
Внедрение зависимостей реализовано с использованием Dagger Hilt и расположено в `app/src/main/java/com/realestateassistant/pro/di/`.

#### 2.5.1. Основные модули DI
- **AppModule** (`app/src/main/java/com/realestateassistant/pro/di/AppModule.kt`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
      fun provideContext(@ApplicationContext context: Context): Context {
          return context
    }
    
    @Provides
    @Singleton
      fun provideOkHttpClient(): OkHttpClient {
          return OkHttpClient.Builder()
              .connectTimeout(30, TimeUnit.SECONDS)
              .readTimeout(30, TimeUnit.SECONDS)
              .writeTimeout(30, TimeUnit.SECONDS)
              .build()
    }
    
    @Provides
    @Singleton
      fun provideGson(): Gson {
          return GsonBuilder()
              .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
              .create()
    }
    
    @Provides
      @Named("yandex_api_key")
      fun provideYandexApiKey(): String {
          return BuildConfig.YANDEX_API_KEY
      }
      
      // ... другие зависимости
  }
  ```

- **DatabaseModule** (`app/src/main/java/com/realestateassistant/pro/di/DatabaseModule.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  object DatabaseModule {
      private const val DATABASE_NAME = "realestate_assistant_db"
      
      // ... предоставление базы данных (код приведен выше)
    
    @Provides
    @Singleton
      fun providePropertyDao(appDatabase: AppDatabase): PropertyDao {
          return appDatabase.propertyDao()
    }
    
    @Provides
    @Singleton
      fun provideClientDao(appDatabase: AppDatabase): ClientDao {
          return appDatabase.clientDao()
    }
    
    @Provides
    @Singleton
      fun provideAppointmentDao(appDatabase: AppDatabase): AppointmentDao {
          return appDatabase.appointmentDao()
    }
    
    @Provides
    @Singleton
      fun provideBookingDao(appDatabase: AppDatabase): BookingDao {
          return appDatabase.bookingDao()
      }
      
      // ... миграции и другие зависимости
  }
  ```

- **RepositoryModule** (`app/src/main/java/com/realestateassistant/pro/di/RepositoryModule.kt`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
  abstract class RepositoryModule {
      @Binds
      @Singleton
      abstract fun bindPropertyRepository(
          impl: PropertyRepositoryImpl
      ): PropertyRepository
      
      @Binds
    @Singleton
      abstract fun bindClientRepository(
          impl: ClientRepositoryImpl
      ): ClientRepository
      
      @Binds
      @Singleton
      abstract fun bindAppointmentRepository(
          impl: AppointmentRepositoryImpl
      ): AppointmentRepository
      
      @Binds
      @Singleton
      abstract fun bindBookingRepository(
          impl: BookingRepositoryImpl
      ): BookingRepository
      
      // ... другие привязки репозиториев
  }
  ```

- **UseCaseModule** (`app/src/main/java/com/realestateassistant/pro/di/UseCaseModule.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  object UseCaseModule {
      // Используем @Provides вместо @Binds, так как некоторые use cases могут требовать
      // дополнительную логику при создании
    
    @Provides
    @Singleton
      fun provideAddPropertyUseCase(
          repository: PropertyRepository,
          imageStorage: ImageStorage
      ): AddPropertyUseCase {
          return AddPropertyUseCase(repository, imageStorage)
      }
      
      // ... другие use cases
  }
  ```

- **PropertyModule** (`app/src/main/java/com/realestateassistant/pro/di/PropertyModule.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  object PropertyModule {
    @Provides
    @Singleton
      fun providePropertyValidator(): PropertyValidator {
          return PropertyValidator()
    }
    
    @Provides
    @Singleton
      fun providePropertyImageProcessor(
          context: Context,
          fileManager: FileManager
      ): PropertyImageProcessor {
          return PropertyImageProcessor(context, fileManager)
      }
      
      // ... другие зависимости для работы с объектами недвижимости
  }
  ```

- **ClientModule** (`app/src/main/java/com/realestateassistant/pro/di/ClientModule.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  object ClientModule {
      @Provides
@Singleton
      fun provideClientValidator(): ClientValidator {
          return ClientValidator()
      }
      
      @Provides
      @Singleton
      fun provideClientMatcher(
          propertyRepository: PropertyRepository
      ): ClientPropertyMatcher {
          return ClientPropertyMatcher(propertyRepository)
      }
      
      // ... другие зависимости для работы с клиентами
  }
  ```

- **AppointmentModule** (`app/src/main/java/com/realestateassistant/pro/di/AppointmentModule.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  object AppointmentModule {
      @Provides
      @Singleton
      fun provideAppointmentScheduler(
          context: Context,
          repository: AppointmentRepository
      ): AppointmentScheduler {
          return AppointmentScheduler(context, repository)
      }
      
      @Provides
      @Singleton
      fun provideAppointmentReminder(
          context: Context,
          notificationManager: NotificationManager
      ): AppointmentReminder {
          return AppointmentReminder(context, notificationManager)
      }
      
      // ... другие зависимости для работы с встречами
  }
  ```

#### 2.5.2. Специфичные модули
- **WorkerModule** (`app/src/main/java/com/realestateassistant/pro/di/WorkerModule.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  interface WorkerModule {
      @Binds
      @IntoMap
      @WorkerKey(PropertyStatusUpdateWorker::class)
      fun bindPropertyStatusUpdateWorkerFactory(
          factory: PropertyStatusUpdateWorker.PropertyStatusUpdateWorkerFactory
      ): ChildWorkerFactory
  }
  
  // Вспомогательные классы для интеграции Hilt с WorkManager
  @MapKey
  @Target(AnnotationTarget.FUNCTION)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class WorkerKey(val value: KClass<out CoroutineWorker>)
  
  interface ChildWorkerFactory {
      fun create(context: Context, params: WorkerParameters): CoroutineWorker
  }
  
  class PropertyWorkerFactory @Inject constructor(
      private val workerFactories: Map<Class<out CoroutineWorker>, @JvmSuppressWildcards ChildWorkerFactory>
  ) : WorkerFactory() {
      override fun createWorker(
          context: Context,
          workerClassName: String,
          workerParameters: WorkerParameters
      ): ListenableWorker? {
          // ... логика создания рабочих на основе переданного имени класса
      }
  }
  ```

- **ValidationModule** (`app/src/main/java/com/realestateassistant/pro/di/ValidationModule.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  object ValidationModule {
      @Provides
      @Singleton
      fun provideAddressValidator(): AddressValidator {
          return AddressValidator()
      }
      
      @Provides
      @Singleton
      fun providePhoneValidator(): PhoneValidator {
          return PhoneValidator()
      }
      
      @Provides
      @Singleton
      fun provideEmailValidator(): EmailValidator {
          return EmailValidator()
      }
      
      // ... другие валидаторы
  }
  ```

- **DocumentModule** (`app/src/main/java/com/realestateassistant/pro/di/DocumentModule.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  object DocumentModule {
      @Provides
      @Singleton
      fun providePdfGenerator(
          context: Context
      ): PdfGenerator {
          return PdfGenerator(context)
      }
      
      @Provides
      @Singleton
      fun provideDocumentStorage(
          context: Context,
          fileEncryptionManager: FileEncryptionManager
      ): DocumentStorage {
          return DocumentStorage(context, fileEncryptionManager)
      }
      
      // ... другие зависимости для работы с документами
  }
  ```

- **InitializationModule** (`app/src/main/java/com/realestateassistant/pro/di/ApplicationInitializer.kt`):
```kotlin
  @Module
  @InstallIn(SingletonComponent::class)
  class ApplicationInitializerImpl @Inject constructor(
      @ApplicationContext private val context: Context,
      private val workManager: WorkManager,
      private val databaseEncryption: DatabaseEncryption
  ) : Initializer<AppDatabase> {
      override fun create(context: Context): AppDatabase {
          // Настройка Timber
          if (BuildConfig.DEBUG) {
              Timber.plant(Timber.DebugTree())
          }
          
          // Инициализация фоновых задач
          PropertyStatusUpdateWorker.schedulePeriodicWork(workManager)
          
          // Создание базы данных
          return createDatabaseAsync(context, databaseEncryption)
      }
      
      override fun dependencies(): List<Class<out Initializer<*>>> {
          // Зависимости, которые должны быть инициализированы перед этим инициализатором
          return emptyList()
      }
      
      private suspend fun createDatabaseAsync(context: Context, databaseEncryption: DatabaseEncryption): AppDatabase {
          // Асинхронная инициализация базы данных
    }
}
```

### 2.6. Фоновые задачи
Приложение использует WorkManager для выполнения фоновых задач:

#### 2.6.1. Основные фоновые задачи
- **PropertyStatusUpdateWorker**: периодическая задача для обновления статусов объектов
  - Анализ текущих бронирований и изменение статусов объектов
  - Планирование с периодичностью в 4 часа
  - Ограничения для экономии батареи
  - Поддержка повторных попыток при ошибках

### 2.6.2. Настройка WorkManager
Конфигурация фоновых задач:

#### 2.6.2.1. Ограничения выполнения
- Требования к сети (NetworkType.NOT_REQUIRED)
- Требования к заряду батареи (setRequiresBatteryNotLow)
- Гибкий интервал запуска для оптимизации энергопотребления

#### 2.6.2.2. Интеграция с Hilt
- Использование HiltWorkerFactory для внедрения зависимостей в воркеры
- Регистрация фабрики в конфигурации приложения

## 3. Модели и сущности

### 3.1. Property (Объект недвижимости)
Основная модель объекта недвижимости содержит:

#### 3.1.1. Общие атрибуты:
- **id**: уникальный идентификатор
- **propertyType**: тип недвижимости (квартира, дом, комната и т.д.)
- **address**: адрес объекта
- **district**: район или ориентир
- **area**: площадь (кв.м)
- **roomsCount**: количество комнат
- **floor**: этаж
- **totalFloors**: этажность здания
- **repairState**: состояние ремонта
- **latitude/longitude**: координаты для отображения на карте
- **photos**: фотографии объекта
- **status**: статус объекта (доступен, сдан и т.д.)

#### 3.1.2. Атрибуты для долгосрочной аренды:
- **monthlyRent**: стоимость аренды за месяц
- **winterMonthlyRent**: зимняя цена аренды
- **summerMonthlyRent**: летняя цена аренды
- **securityDeposit**: залог
- **utilitiesIncluded**: включены ли коммунальные платежи
- **minRentPeriod**: минимальный срок аренды

#### 3.1.3. Атрибуты для краткосрочной аренды:
- **dailyPrice**: стоимость аренды за сутки
- **weekendPrice**: цена в выходные дни
- **minStayDays**: минимальное количество дней проживания
- **maxGuests**: максимальное количество гостей
- **checkInTime/checkOutTime**: время заезда и выезда
- **cleaningService**: услуги уборки
- **seasonalPrices**: сезонные цены с указанием периодов

#### 3.1.4. Дополнительные характеристики
- **amenities**: удобства (список строк)
- **views**: виды из окна (список строк)
- **nearbyObjects**: близлежащие объекты инфраструктуры
- **parkingType**: тип парковки
- **hasGarage/garageSpaces**: наличие гаража и количество мест
- **bathroomsCount**: количество санузлов
- **bathroomType**: тип санузла
- **heatingType**: тип отопления
- **balconiesCount**: количество балконов

### 3.2. Client (Клиент)
Модель клиента включает:

#### 3.2.1. Личные данные:
- **id**: уникальный идентификатор
- **fullName**: ФИО клиента
- **phone**: контактные телефоны
- **familyComposition**: состав семьи
- **peopleCount**: количество проживающих
- **hasPets**: наличие домашних животных
- **petTypes**: типы животных
- **petCount**: количество животных
- **occupation**: род занятий
- **isSmokingClient**: является ли клиент курящим

#### 3.2.2. Предпочтения:
- **rentalType**: тип аренды (длительная/посуточная)
- **preferredDistrict**: предпочитаемый район
- **budgetFlexibility**: гибкость по бюджету
- **preferredAmenities**: желаемые удобства
- **priorityCriteria**: приоритетные критерии
- **urgencyLevel**: срочность поиска

#### 3.2.3. Бюджет:
- **longTermBudgetMin/Max**: диапазон бюджета для длительной аренды
- **shortTermBudgetMin/Max**: диапазон бюджета для краткосрочной аренды

#### 3.2.4. Детальные предпочтения по жилью
- **desiredPropertyType**: желаемый тип недвижимости
- **desiredRoomsCount**: желаемое количество комнат
- **desiredArea**: желаемая площадь
- **preferredFloorMin/Max**: предпочитаемый диапазон этажей
- **needsElevator**: требуется ли лифт
- **needsFurniture**: требуется ли мебель
- **needsAppliances**: требуется ли бытовая техника
- **needsParking**: требуется ли парковка
- **preferredViews**: предпочитаемые виды из окна

### 3.3. Appointment (Встреча)
Модель встречи или показа:
- **id**: уникальный идентификатор
- **title**: название встречи
- **dateTime**: дата и время
- **duration**: продолжительность
- **status**: статус (запланирована, завершена, отменена, в процессе)
- **type**: тип встречи (показ, консультация, подписание договора, прочее)
- **clientId**: связанный клиент
- **propertyId**: связанный объект недвижимости
- **notes**: заметки по встрече
- **location**: место встречи
- **reminder**: время напоминания (за сколько времени до встречи)
- **isRecurring**: является ли встреча повторяющейся
- **recurringPattern**: шаблон повторения (еженедельно, ежемесячно и т.д.)

### 3.4. Booking (Бронирование)
Модель бронирования объекта:
- **id**: уникальный идентификатор
- **propertyId**: ID объекта недвижимости
- **clientId**: ID клиента
- **startDate**: дата заезда
- **endDate**: дата выезда
- **status**: статус бронирования (подтверждено, в ожидании, отменено, завершено)
- **guestsCount**: количество гостей
- **totalPrice**: общая стоимость
- **notes**: дополнительные примечания
- **paymentStatus**: статус оплаты
- **depositPaid**: внесен ли депозит
- **checkInTime**: время заезда
- **checkOutTime**: время выезда
- **hasSpecialRequests**: наличие особых пожеланий гостя

### 3.5. Статусы и перечисления
- **PropertyStatus**: Available, Rented, Maintenance, Reserved, Hidden
- **AppointmentStatus**: Planned, Completed, Canceled, InProgress
- **AppointmentType**: Showing, Consultation, Contract, Other
- **BookingStatus**: Confirmed, Pending, Canceled, Completed

### 3.6. Маппинг данных
Для преобразования данных между слоями используются специализированные мапперы:

- **PropertyMapper**: маппинг между `PropertyEntity` и `Property`
  - `mapToDomain`: преобразование из Entity в Domain модель
  - `toEntity`: преобразование из Domain в Entity модель
  - Дополнительные методы преобразования для вложенных объектов

- **BookingMapper**: маппинг между `BookingEntity` и `Booking`
- **ClientMapper**: маппинг между `ClientEntity` и `Client`
- **AppointmentMapper**: маппинг между `AppointmentEntity` и `Appointment`

## 4. UI и навигация

### 4.1. Структура навигации
Приложение использует Navigation Compose для маршрутизации между экранами. Основные маршруты определены в `AppRoutes`.

#### 4.1.1. Основные экраны:
- **DASHBOARD**: основная панель управления
- **PROPERTIES**: список объектов недвижимости
- **PROPERTY_DETAIL**: детали объекта
- **ADD_PROPERTY**: добавление нового объекта
- **EDIT_PROPERTY**: редактирование объекта
- **CLIENTS**: список клиентов
- **CLIENT_DETAIL**: детали клиента
- **ADD_CLIENT**: добавление нового клиента
- **EDIT_CLIENT**: редактирование информации о клиенте
- **APPOINTMENTS**: список встреч
- **APPOINTMENT_DETAIL**: детали встречи
- **BOOKING_CALENDAR**: календарь бронирований
- **PROPERTY_RECOMMENDATIONS**: рекомендации объектов для клиентов
- **SETTINGS**: настройки приложения
- **HELP**: справка
- **ABOUT**: информация о приложении

#### 4.1.2. Параметры маршрутов:
- **PropertyId**: идентификатор объекта недвижимости
- **ClientId**: идентификатор клиента
- **AppointmentId**: идентификатор встречи

### 4.2. Основные экраны приложения

#### 4.2.1. Дэшборд (Dashboard)
Главный экран приложения, отображающий ключевые метрики и предстоящие события:
- Статистика объектов недвижимости по статусам
- Предстоящие встречи и показы
- Новые клиенты
- Новые бронирования
- Графики с аналитикой
- Карта с расположением объектов недвижимости

#### 4.2.2. Экраны объектов недвижимости
- **PropertyListScreen**: список объектов с фильтрацией
  - Фильтрация по типам аренды (долгосрочная/краткосрочная)
  - Фильтрация по статусу, цене, типу недвижимости, району и другим параметрам
  - Сортировка по различным критериям
  - Поиск по ключевым словам
  - Возможность просмотра в виде списка или плиток

- **PropertyDetailScreen**: детальная информация об объекте
  - Галерея фотографий с возможностью масштабирования
  - Детальная информация об объекте
  - Местоположение на карте
  - Календарь бронирований (для краткосрочной аренды)
  - Контактная информация владельца
  - Возможность поделиться информацией об объекте
  - Формирование PDF-отчета

- **AddPropertyScreen**: форма добавления нового объекта
  - Многоэтапная форма с пошаговым заполнением
  - Загрузка и обрезка фотографий
  - Выбор местоположения на карте
  - Подсказки при вводе адреса с использованием Yandex API
  - Валидация форм с отображением ошибок

- **EditPropertyScreen**: редактирование существующего объекта
  - Аналогично AddPropertyScreen, но с предзаполненными полями

#### 4.2.3. Экраны клиентов
- **ClientListScreen**: список клиентов с возможностью поиска
  - Фильтрация клиентов по различным критериям
  - Поиск по имени и контактной информации
  - Различные режимы отображения

- **ClientDetailScreen**: детальная информация о клиенте
  - Личная информация
  - Предпочтения по аренде
  - История встреч и показов
  - Подходящие объекты недвижимости
  - Возможность назначить встречу

- **AddClientScreen**: форма добавления нового клиента
  - Многоэтапная форма
  - Выбор предпочтений по аренде
  - Задание бюджета и критериев поиска
  - Автоматическое сохранение черновиков

- **EditClientScreen**: редактирование информации о клиенте
  - Аналогично AddClientScreen, но с предзаполненными полями

#### 4.2.4. Экраны встреч и бронирований
- **AppointmentListScreen**: список встреч и показов
  - Фильтрация по дате, статусу, типу
  - Календарный вид
  - Интеграция с системным календарем

- **BookingCalendarScreen**: календарь с бронированиями объекта
  - Визуальное отображение занятых и свободных дат
  - Возможность создания нового бронирования
  - Управление существующими бронированиями
  - Просмотр деталей бронирования

### 4.3. UI компоненты
Приложение использует Material Design 3 и реализовано на Jetpack Compose:

#### 4.3.1. Основные компоненты
- **ExpandablePropertyCard**: карточка объекта недвижимости с возможностью раскрытия дополнительной информации
- **ExpandableClientCard**: карточка клиента с возможностью раскрытия
- **PropertyFormComponents**: набор компонентов для форм редактирования объекта
- **ValidationComponents**: компоненты для валидации ввода с отображением ошибок

#### 4.3.2. Специализированные поля ввода
- **AddressTextField**: поле ввода адреса с подсказками
- **AddressSuggestField**: поле с автодополнением адреса
- **PhoneInputField**: поле ввода телефонного номера с форматированием
- **CountryCodePicker**: выбор кода страны для телефона
- **MultiSelectField**: поле множественного выбора
- **EditableDropdownField**: выпадающий список с возможностью добавления новых значений
- **NumericTextField**: поле для ввода числовых значений
- **DatePickerField**: поле выбора даты
- **TimeTextField**: поле выбора времени

#### 4.3.3. Диалоговые окна
- **DatePickerDialog**: диалог выбора даты
- **TimePickerDialog**: диалог выбора времени
- **CallLogSelector**: диалог выбора номера из журнала вызовов

#### 4.3.4. Медиа-компоненты
- **MediaComponents**: компоненты для работы с мультимедиа
  - Загрузка и отображение изображений
  - Галерея изображений с возможностью пролистывания
  - Обрезка изображений
  - Получение изображений с камеры и из галереи

#### 4.3.5. Картографические компоненты
- **MapPropertySelector**: карта для выбора местоположения объекта недвижимости
- **MapPropertiesDisplay**: отображение нескольких объектов на карте
- **MapComponent**: базовый компонент для работы с картой

### 4.4. Адаптивный дизайн
Интерфейс адаптируется под различные размеры экрана:

#### 4.4.1. Поддержка различных устройств
- Поддержка разных ориентаций экрана
- Адаптация под планшеты
- Поддержка различных плотностей экрана

#### 4.4.2. Особенности адаптивного дизайна
- Динамические размеры компонентов в зависимости от размера экрана
- Различная компоновка элементов для разных размеров экрана
- Режим многооконности для планшетов
- Использование составных компонентов

#### 4.4.3. Темная тема
- Полная поддержка темной темы
- Цветовая схема, оптимизированная для разных режимов
- Автоматическое переключение на основе системных настроек
- Опциональная поддержка динамических цветов (Material You)

### 4.5. Работа с картами и геолокацией
Приложение использует Yandex MapKit для работы с картами и геолокацией:

#### 4.5.1. Геокодирование
- **YandexGeocoderService**: сервис для преобразования адреса в координаты
  - Асинхронное получение координат по адресу
  - Обработка ошибок и некорректных адресов
  - Использование API Яндекс.Карт

#### 4.5.2. Автодополнение адресов
- **YandexSuggestService**: сервис для получения подсказок при вводе адреса
  - Интерактивное автодополнение при вводе
  - Фильтрация результатов по релевантности
  - Кэширование результатов для оптимизации

#### 4.5.3. Отображение на карте
- Отображение объектов недвижимости на карте
- Кластеризация маркеров при большом количестве объектов
- Интерактивное взаимодействие с маркерами
- Определение оптимального масштаба и центра карты

## 5. Система безопасности и хранения данных

### 5.1. Шифрование базы данных
Система обеспечивает безопасное хранение конфиденциальных данных:

#### 5.1.1. SQLCipher интеграция
- Полное шифрование базы данных Room с использованием SQLCipher
- Безопасное хранение пароля базы данных в EncryptedSharedPreferences
- Механизм автоматической генерации пароля при первом запуске
- Дополнительный механизм восстановления при повреждении криптографических ключей

#### 5.1.2. Механизмы восстановления
- Обнаружение повреждения криптографических ключей
- Резервное хранение ключей для восстановления доступа
- Полный сброс базы данных при критических ошибках

### 5.2. Работа с файлами
Приложение обеспечивает безопасный доступ к файловой системе:

#### 5.2.1. FileProvider
- Безопасный доступ к файлам приложения для других приложений
- Определение путей доступа в XML-конфигурации
- Интеграция с системной камерой и галереей

#### 5.2.2. Хранение изображений
- Оптимизация изображений перед сохранением
- Компрессия для уменьшения размера
- Кэширование для быстрого доступа
- Уникальные имена файлов для исключения коллизий

### 5.3. Управление данными
Приложение включает механизмы для эффективной работы с данными:

#### 5.3.1. Асинхронная инициализация
- Асинхронная инициализация базы данных при запуске приложения
- Использование App Startup для параллельной инициализации компонентов
- Оптимизация времени запуска приложения

#### 5.3.2. Flow для наблюдения за данными
- Использование Flow для реактивной работы с данными
- Трансформация потоков данных с помощью операторов Flow
- Отмена подписок при уничтожении ViewModel

## 6. Тема и стилизация приложения

### 6.1. Цветовая схема
Приложение использует собственную цветовую схему:

#### 6.1.1. Основные цвета
- **Primary**: основной синий цвет (#1A56DB)
- **Secondary**: вторичный синий цвет (#3F4DB0)
- **Tertiary**: третичный синий цвет (#00639D)
- **Background/Surface**: светлый фон с синим оттенком (#F8F9FF)

#### 6.1.2. Темная тема
- **DarkPrimary**: светло-синий для темной темы (#A1C6FF)
- **DarkBackground/DarkSurface**: темный фон (#1A1C1E)
- **DarkOnBackground/DarkOnSurface**: светлый текст на темном фоне (#E2E2E6)

#### 6.1.3. Семантические цвета
- **Error**: цвет ошибки (#BA1B1B)
- **Цвета для статусов встреч**: различные цвета для визуального обозначения статусов

### 6.2. Типография
Приложение использует систему типографии Material Design 3:

#### 6.2.1. Шрифты
- Использование системных шрифтов для единообразного отображения
- Масштабируемые размеры текста для поддержки доступности

#### 6.2.2. Стилизация текста
- Определение стилей для всех категорий текста (заголовки, подзаголовки, основной текст, метки)
- Настройка интерлиньяжа и плотности текста
- Поддержка выделения важной информации

## 7. Интеграции и API

### 7.1. Yandex MapKit интеграция
Приложение использует Yandex MapKit для отображения карт и геолокации объектов недвижимости:

#### 7.1.1. Инициализация и настройка
- Инициализация MapKit выполняется в классе `RealEstateApplication`:
  ```
  // Инициализация Yandex MapKit
  MapKitFactory.setApiKey("80b06c04-6156-4dfd-a086-2cee0b05fdb8")
  ```
- Настройка жизненного цикла карты с использованием `MapKitFactory.getInstance().onStart()/onStop()`
- Обработка событий lifecycle с использованием Compose DisposableEffect

#### 7.1.2. Компоненты для работы с картами
- `PropertyMapView`: Composable-компонент для отображения объекта недвижимости на карте
  - Добавление маркеров с настраиваемым цветом
  - Настройка камеры и масштаба
  - Добавление текстовых подписей к маркерам

### 7.2. PDF генерация
Приложение имеет функциональность экспорта данных объектов недвижимости в PDF-документы:

#### 7.2.1. ExportPropertyToPdfUseCase
- Генерация PDF-документов с информацией об объектах недвижимости
- Использование `android.graphics.pdf.PdfDocument` для создания многостраничных документов
- Сохранение во внутреннее или внешнее хранилище (с проверкой разрешений)
- Поддержка версий Android 10+ и более старых с разными подходами к сохранению

#### 7.2.2. PdfExportService
- Сервис для отрисовки содержимого PDF-документов
- Многостраничная верстка с информацией об объекте недвижимости
- Включение изображений объекта в документ
- Настройка шрифтов, размеров и цветов текста

### 7.3. Интеграция с телефонией
Приложение предоставляет удобные способы коммуникации с клиентами и владельцами недвижимости:

#### 7.3.1. Функции обработки и форматирования номеров
- Класс `PhoneUtils` предоставляет набор функций для работы с номерами телефонов:
  - Форматирование номеров для отображения с учетом региональных особенностей
  - Поддержка различных форматов для России, США, Великобритании, Германии
  - Очистка номеров от нецифровых символов

#### 7.3.2. Интеграции с коммуникационными сервисами
- Звонки: `Intent.ACTION_DIAL` для набора номера телефона
- SMS: `Intent.ACTION_SENDTO` для отправки текстовых сообщений
- WhatsApp: интеграция через URL-схему "https://api.whatsapp.com/send"
- Telegram: поддержка открытия приложения или веб-версии Telegram

### 7.4. Работа с файловой системой
Приложение имеет продуманную систему работы с файлами для безопасного хранения и доступа:

#### 7.4.1. StorageHelper
- Вспомогательный класс для безопасной работы с файловой системой Android
- Поддержка внутреннего и внешнего хранилища
- Обработка различий в API для разных версий Android
- Создание директорий для разных типов файлов (PDF, изображения, документы)

#### 7.4.2. FileProvider
- Безопасное предоставление доступа к файлам через `androidx.core.content.FileProvider`
- Конфигурация в AndroidManifest.xml:
  ```
  <provider
      android:name="androidx.core.content.FileProvider"
      android:authorities="${applicationId}.fileprovider"
      android:exported="false"
      android:grantUriPermissions="true">
      <!-- ... -->
  </provider>
  ```
- Механизмы для открытия файлов в других приложениях при экспорте

## 8. Защита данных и безопасность

### 8.1. Шифрование базы данных с SQLCipher
Приложение обеспечивает надежную защиту конфиденциальных данных:

#### 8.1.1. Реализация DatabaseEncryption
- Класс `DatabaseEncryption` управляет ключами шифрования для базы данных:
```kotlin
  class DatabaseEncryption @Inject constructor(
      private val context: Context,
      private val securityManager: SecurityManager
  ) {
      // ...
      
      fun getDatabasePassword(): String {
          var password = securePrefs.getString(KEY_DATABASE_PASSWORD, null)
          
          if (password == null) {
              password = generateSecurePassword()
              securePrefs.edit().putString(KEY_DATABASE_PASSWORD, password).apply()
              
              // Дополнительная копия для восстановления
              backupPrefs.edit().putString(KEY_DATABASE_PASSWORD_BACKUP, password).apply()
          }
          
          return password
      }
      
      // ...
  }
  ```

- Использование `EncryptedSharedPreferences` для безопасного хранения паролей
- Автоматическая генерация паролей с высокой криптографической стойкостью
- Резервный механизм восстановления ключей шифрования

#### 8.1.2. Интеграция с Room
- Настройка SQLCipher в `DatabaseModule`:
```kotlin
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseEncryption: DatabaseEncryption
    ): AppDatabase {
        try {
          // Получение ключа шифрования
            val passphrase = SQLiteDatabase.getBytes(databaseEncryption.getDatabasePassword().toCharArray())
            val factory = SupportFactory(passphrase)
            
          // Создание зашифрованной базы данных
          return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
              .fallbackToDestructiveMigration()
              .openHelperFactory(factory)  // Интеграция SQLCipher
            .build()
        } catch (e: Exception) {
          // Обработка ошибок шифрования
      }
  }
  ```
- Автоматический перевыпуск ключей шифрования при проблемах

### 8.2. Безопасное хранение настроек
Приложение обеспечивает безопасное хранение пользовательских настроек:

#### 8.2.1. SecureAppPreferences
- Использование `EncryptedSharedPreferences` для шифрования данных настроек:
  ```kotlin
  private val encryptedPreferences by lazy {
      try {
          EncryptedSharedPreferences.create(
              context,
              PREFERENCES_FILE_NAME,
              masterKey,
              EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
              EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
          )
      } catch (e: Exception) {
          // Fallback
      }
  }
  ```
- Шифрование как ключей, так и значений с использованием алгоритмов AES
- Использование Android Keystore для хранения мастер-ключа

### 8.3. Управление разрешениями
Приложение тщательно управляет системными разрешениями:

#### 8.3.1. Необходимые разрешения
- `READ_EXTERNAL_STORAGE` и `WRITE_EXTERNAL_STORAGE` для работы с файлами
- `CAMERA` для фотографирования объектов недвижимости
- `ACCESS_FINE_LOCATION` и `ACCESS_COARSE_LOCATION` для геолокации
- `READ_CALL_LOG` для интеграции с журналом звонков
- `READ_MEDIA_IMAGES` для современной обработки медиафайлов

#### 8.3.2. Runtime проверки разрешений
- Использование `ContextCompat.checkSelfPermission()` перед выполнением операций
- Реализация в `ExportPropertyToPdfUseCase`:
  ```kotlin
  private fun checkStoragePermission(): Boolean {
      // На Android 10 (API 29) и выше разрешение не требуется для записи
      // в директорию Download/RealEstateAssistant
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          return true
      }
      
      // Для более старых версий проверяем наличие разрешения WRITE_EXTERNAL_STORAGE
      return ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_GRANTED
  }
  ```
- Адаптация функциональности в зависимости от доступных разрешений

### 8.4. Шифрование файлов
Приложение применяет Security-Crypto для шифрования файлов:

#### 8.4.1. FileEncryptionManager
- Использование `EncryptedFile` для шифрования файлов:
  ```kotlin
  suspend fun encryptFile(inputFile: File, fileName: String): Result<File> = withContext(Dispatchers.IO) {
      try {
          val encryptedFile = EncryptedFile.Builder(
              File(context.filesDir, fileName),
              context,
              masterKey,
              EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
          ).build()
          
          encryptedFile.openFileOutput().use { outputStream ->
              inputFile.inputStream().use { it.copyTo(outputStream) }
          }
          
          Result.Success(encryptedFile.file)
      } catch (e: Exception) {
          Result.Error(e)
      }
  }
  ```
- Надежное шифрование с использованием AES-256-GCM
- Безопасное восстановление зашифрованных файлов 