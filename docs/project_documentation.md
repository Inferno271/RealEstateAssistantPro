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
13. [Технический долг и планы развития](#технический-долг-и-планы-развития)

## Общее описание приложения

**RealEstateAssistant PRO** - это мобильное приложение для реелторов, разработанное для эффективного управления объектами недвижимости, клиентами и встречами. Приложение позволяет создавать и управлять объектами недвижимости для аренды (долгосрочной и посуточной), вести базу клиентов и организовывать просмотры недвижимости.

### Основные функции

- Создание и редактирование карточек объектов недвижимости с детальной информацией
- Управление фотографиями и документами для объектов недвижимости
- Ведение базы данных клиентов
- Планирование и отслеживание встреч и просмотров
- Быстрый поиск и фильтрация объектов и клиентов

## Архитектура проекта

Проект реализован с использованием архитектурного паттерна **Clean Architecture** и **MVVM**. Приложение разделено на следующие основные слои:

### Слои приложения

1. **Presentation layer (UI)**
   - `presentation` - содержит компоненты пользовательского интерфейса, написанные с использованием Jetpack Compose
   - ViewModel-компоненты для связи UI и бизнес-логики

2. **Domain layer**
   - `domain.model` - бизнес-модели данных
   - `domain.usecase` - интерактеры (юзкейсы) для бизнес-логики
   - `domain.repository` - интерфейсы репозиториев

3. **Data layer**
   - `data.repository` - имплементации репозиториев
   - `data.local` - источники данных (Room Database)
   - `data.mapper` - маппинги между слоями

### Инфраструктура

- **Dependency Injection**: Dagger Hilt
- **UI Framework**: Jetpack Compose
- **Persistence**: Room Database
- **Асинхронность**: Kotlin Coroutines и Flow
- **Обработка изображений**: Coil

## Модули и основные компоненты

### Модуль недвижимости

#### Модели
- `Property` - модель данных объекта недвижимости
- `PropertyFormState` - состояние формы редактирования/создания объекта

#### Use Cases
- Создание, редактирование и удаление объектов
- Получение списков объектов по фильтрам
- Получение детальной информации об объекте

#### Репозитории
- `PropertyRepository` - интерфейс репозитория для работы с базой данных объектов
- `PropertyRepositoryImpl` - имплементация репозитория

#### UI компоненты
- `PropertyListScreen` - экран списка объектов недвижимости
- `PropertyDetailScreen` - экран детальной информации об объекте
- `PropertyFormScreen` - экран формы добавления/редактирования
- `PropertyCard` - компонент для отображения карточки объекта в списке
- `PropertyFilter` - компонент для фильтрации списка объектов
- `PhotoGallery` - компонент для отображения галереи фотографий

### Модуль клиентов

#### Модели
- `Client` - модель данных клиента
- `ClientFormState` - состояние формы редактирования/создания клиента

#### Use Cases
- Добавление, редактирование, удаление клиентов
- Поиск клиентов по параметрам
- Получение информации о клиенте

#### Репозитории
- `ClientRepository` - интерфейс репозитория для работы с базой данных клиентов
- `ClientRepositoryImpl` - имплементация репозитория

#### UI компоненты
- `ClientListScreen` - экран списка клиентов
- `ClientDetailScreen` - экран детальной информации о клиенте
- `ClientFormScreen` - экран формы добавления/редактирования
- `ClientCard` - компонент карточки клиента

### Модуль встреч

#### Модели
- `Appointment` - модель данных встречи
- `AppointmentFormState` - состояние формы редактирования/создания встречи

#### Use Cases
- Создание, редактирование, удаление встреч
- Получение встреч по дате, объекту, клиенту
- Управление календарем

#### Репозитории
- `AppointmentRepository` - интерфейс репозитория для работы с базой данных встреч
- `AppointmentRepositoryImpl` - имплементация репозитория

#### UI компоненты
- `AppointmentListScreen` - экран списка встреч
- `AppointmentDetailScreen` - экран детальной информации о встрече
- `AppointmentFormScreen` - экран формы добавления/редактирования
- `CalendarView` - календарь для выбора даты встречи

### Модуль изображений

#### Модели и компоненты
- `ImageRepository` - интерфейс для работы с изображениями
- `ImageRepositoryImpl` - реализация репозитория изображений
- `ImageUseCases` - юзкейсы для работы с изображениями
- `ImageViewModel` - ViewModel для управления изображениями
- `CoilUtils` - утилиты для оптимизации загрузки изображений
- Компоненты UI для отображения и управления изображениями

### Модуль документов

#### Модели и компоненты
- `DocumentRepository` - интерфейс для работы с документами
- `DocumentRepositoryImpl` - реализация репозитория документов
- `DocumentUseCases` - юзкейсы для работы с документами:
  - `SaveDocument` - сохранение документа
  - `GetDocumentUri` - получение URI документа для открытия
  - `DeleteDocument` - удаление документа
  - `OpenDocument` - открытие документа во внешнем приложении
- `DocumentViewModel` - ViewModel для управления документами
- `DocumentItem` - UI компонент для отображения документа в списке

## Модель данных

### Property (Объект недвижимости)

```kotlin
data class Property(
    val id: String = UUID.randomUUID().toString(),
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
    val id: String = UUID.randomUUID().toString(),
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String? = null,
    val rentType: String = "", // "long_term", "short_term", "both"
    val propertyTypeInterests: List<String> = emptyList(),
    val districtInterests: List<String> = emptyList(),
    val priceRange: PriceRange? = null,
    val minArea: Double? = null,
    val minRooms: Int? = null,
    val additionalRequirements: String? = null,
    val source: String? = null, // Откуда пришел клиент
    val status: String = "active", // active, inactive, deal_completed
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Appointment (Встреча)

```kotlin
data class Appointment(
    val id: String = UUID.randomUUID().toString(),
    val clientId: String = "",
    val propertyId: String = "",
    val title: String = "",
    val date: Long = 0L,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val address: String = "",
    val notes: String? = null,
    val status: String = "scheduled", // scheduled, completed, cancelled
    val reminderTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

## Управление состоянием

Управление состоянием в приложении реализовано с использованием ViewModel и StateFlow из библиотеки Jetpack. Это обеспечивает реактивное обновление UI при изменении данных.

### Основные ViewModel компоненты

1. **PropertyViewModel**
   - Управляет списком и деталями объектов недвижимости
   - Содержит состояние формы редактирования объекта
   - Обеспечивает фильтрацию и поиск объектов

2. **ClientViewModel**
   - Управляет списком и деталями клиентов
   - Содержит состояние формы редактирования клиента

3. **AppointmentViewModel**
   - Управляет списком и деталями встреч
   - Содержит состояние формы редактирования встречи
   - Обеспечивает функционал календаря

4. **ImageViewModel**
   - Управляет загрузкой, сохранением и удалением изображений
   - Обрабатывает состояния загрузки и ошибок для изображений
   
5. **DocumentViewModel**
   - Управляет сохранением, открытием и удалением документов
   - Обеспечивает интеграцию с внешними приложениями для работы с документами
   - Предоставляет API для взаимодействия с документами из UI-слоя

### Пример управления состоянием в ViewModel:

```kotlin
@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageUseCases: ImageUseCases
) : ViewModel() {
    
    // Состояние загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    // Состояние ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error
    
    // Последнее загруженное изображение
    private val _loadedImage = MutableStateFlow<Bitmap?>(null)
    val loadedImage: StateFlow<Bitmap?> get() = _loadedImage
    
    // Методы управления изображениями
    suspend fun saveImage(uri: Uri, compress: Boolean = true, quality: Int = 80): Result<String> { ... }
    suspend fun saveImages(uris: List<Uri>, compress: Boolean = true, quality: Int = 80): Result<List<String>> { ... }
    fun loadImage(path: String) { ... }
    fun deleteImage(path: String) { ... }
    suspend fun isValidImageUri(uri: Uri): Boolean { ... }
    fun clearImageCache() { ... }
    fun clearError() { ... }
    fun clearLoadedImage() { ... }
}
```

## Пользовательский интерфейс

Пользовательский интерфейс приложения полностью реализован с использованием Jetpack Compose - современного инструментария для разработки UI на Android. 

### Основные UI-компоненты

#### Экраны

- `PropertyListScreen` - список объектов недвижимости с фильтрацией
- `PropertyDetailScreen` - детальная информация об объекте
- `PropertyFormScreen` - форма создания/редактирования объекта
- `ClientListScreen` - список клиентов с поиском
- `ClientDetailScreen` - детальная информация о клиенте
- `ClientFormScreen` - форма создания/редактирования клиента
- `AppointmentListScreen` - список встреч
- `AppointmentFormScreen` - форма создания/редактирования встречи
- `CalendarScreen` - календарь встреч

#### Повторно используемые компоненты

- **PropertyCard** - карточка объекта в списке
  ```kotlin
  @Composable
  fun PropertyCard(
      property: Property,
      onClick: () -> Unit = {}
  ) { ... }
  ```

- **PhotoGallery** - галерея фотографий объекта
  ```kotlin
  @Composable
  fun PhotoGallery(
      photos: List<String>,
      modifier: Modifier = Modifier
  ) { ... }
  ```
  
- **PropertyFilter** - компонент фильтрации объектов недвижимости
  ```kotlin
  @Composable
  fun PropertyFilterTabs(
      selectedFilter: RentalFilter,
      onFilterSelected: (RentalFilter) -> Unit
  ) { ... }
  ```

- **MediaSection** - секция для управления медиафайлами объекта
  ```kotlin
  @Composable
  fun MediaSection(
      formState: PropertyFormState,
      onFormStateChange: (PropertyFormState) -> Unit,
      expandedSections: MutableMap<PropertySection, Boolean>,
      imageViewModel: ImageViewModel = hiltViewModel()
  ) { ... }
  ```
  
- **ContactSection** - секция для контактной информации
  ```kotlin
  @Composable
  fun ContactSection(
      formState: PropertyFormState,
      onFormStateChange: (PropertyFormState) -> Unit,
      expandedSections: MutableMap<PropertySection, Boolean>
  ) { ... }
  ```

### Темизация

Приложение поддерживает светлую и темную темы с использованием Material 3. Цветовая схема определена в файле `Theme.kt`:

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
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) { ... }
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

### Оптимизация загрузки изображений

Класс `CoilUtils` содержит утилиты для оптимизации загрузки изображений с помощью библиотеки Coil:

```kotlin
object CoilUtils {
    fun createImageLoader(
        context: Context,
        diskCacheSize: Long = 100 * 1024 * 1024,
        memoryCacheSize: Int? = null
    ): ImageLoader { ... }
    
    fun createImageRequest(
        context: Context,
        data: Any,
        size: Size = Size.ORIGINAL,
        diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
        memoryCachePolicy: CachePolicy = CachePolicy.ENABLED
    ): ImageRequest.Builder { ... }
}
```

### UI компоненты для работы с изображениями

- `PhotoGallery` - галерея фотографий с возможностью просмотра в полноэкранном режиме
- `FullscreenPhotoViewer` - компонент для полноэкранного просмотра фотографий
- `PropertyThumbnail` - миниатюра объекта недвижимости
- `PhotoThumbnail` - миниатюра фотографии в редакторе объекта
- `MediaSection` - раздел для управления медиафайлами в форме объекта

## Работа с документами

В приложении реализована система работы с документами, которая обеспечивает возможность сохранения, открытия и удаления различных типов документов (договоры, описания, и т.д.) для объектов недвижимости.

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

В приложении определены следующие юзкейсы для работы с документами:

```kotlin
data class DocumentUseCases @Inject constructor(
    val saveDocument: SaveDocument,
    val getDocumentUri: GetDocumentUri,
    val deleteDocument: DeleteDocument,
    val openDocument: OpenDocument
)
```

- `SaveDocument` - сохранение документа в хранилище приложения
- `GetDocumentUri` - получение URI документа для открытия
- `DeleteDocument` - удаление документа
- `OpenDocument` - открытие документа во внешнем приложении (получает URI и MIME-тип)

### ViewModel для работы с документами

`DocumentViewModel` предоставляет API для работы с документами из UI-слоя:

```kotlin
@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val documentUseCases: DocumentUseCases
) : ViewModel() {
    suspend fun saveDocument(uri: Uri): Result<String>
    suspend fun openDocument(path: String): Result<Pair<Uri, String?>>
    suspend fun deleteDocument(path: String): Result<Unit>
}
```

### UI компоненты для работы с документами

- `DocumentItem` - компонент для отображения документа в списке с возможностью удаления
  ```kotlin
  @Composable
  fun DocumentItem(
      documentUri: String,
      onDeleteClick: () -> Unit,
      index: Int
  ) { ... }
  ```

- Интеграция с формой редактирования объекта недвижимости для управления прикрепленными документами
- Функции для открытия документов во внешних приложениях

## Интеграция с картами

В приложении реализована интеграция с картами для отображения местоположения объектов недвижимости, что повышает наглядность и удобство работы с географической информацией.

### Компоненты системы карт

- `PropertyMapView` - Compose-компонент для отображения интерактивной карты с местоположением объекта недвижимости
- `YandexGeocoderService` - сервис для преобразования текстовых адресов в географические координаты
- `PropertyAddressSection` - компонент для отображения адреса объекта и его местоположения на карте

### Функциональность карт

- Отображение местоположения объекта на карте с маркером
- Автоматическое геокодирование адресов в географические координаты
- Сохранение и отображение точных координат (широта, долгота) объектов недвижимости
- Оптимизированная работа с жизненным циклом для экономии ресурсов (активация/деактивация при изменении состояния экрана)
- Настраиваемый стиль отображения маркеров объектов

### Технологическая реализация

Интеграция карт основана на Yandex MapKit:

```kotlin
// Инициализация в классе Application
override fun onCreate() {
    super.onCreate()
    // Инициализация Yandex MapKit
    MapKitFactory.setApiKey("80b06c04-6156-4dfd-a086-2cee0b05fdb8")
}

// Компонент карты
@Composable
fun PropertyMapView(
    address: String,
    latitude: Double,
    longitude: Double,
    markerColor: Color = Color(0xFF2196F3),
    modifier: Modifier = Modifier
) {
    // ... реализация отображения карты
}
```

Геокодирование адресов реализовано через API Яндекс Геокодера:

```kotlin
object YandexGeocoderService {
    suspend fun getCoordinates(address: String): Pair<Double, Double>? {
        // ... реализация запроса к геокодеру и обработка ответа
    }
}
```

### Оптимизация производительности

Для оптимизации работы с картами в приложении реализованы следующие механизмы:
- Ленивая инициализация карт (только при фактическом отображении)
- Корректная обработка жизненного цикла для предотвращения утечек памяти
- Очистка ресурсов при уничтожении компонентов
- Кэширование данных геокодирования для снижения количества сетевых запросов

## Безопасность и шифрование данных

В приложении реализована система безопасности для защиты конфиденциальных данных пользователей и объектов недвижимости.

### Шифрование базы данных

Основой системы безопасности является шифрование локальной базы данных с использованием SQLCipher:

```kotlin
// Пример конфигурации шифрованной базы данных
Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
    .openHelperFactory(SupportFactory(encryptionManager.getDatabasePassword().toByteArray()))
    .fallbackToDestructiveMigration()
    .build()
```

### Компоненты безопасности

- `DatabaseEncryption` - класс для управления ключами шифрования и паролями базы данных
- Интеграция с Android Keystore для безопасного хранения криптографических ключей
- Механизмы восстановления при повреждении ключей шифрования

### Защита конфиденциальных данных

Приложение обеспечивает безопасность через:

- Шифрование всех конфиденциальных данных в базе данных
- Использование EncryptedSharedPreferences для хранения чувствительных настроек
- Механизмы очистки кэша и временных файлов
- Защиту от экспорта незашифрованных данных

### Безопасность медиафайлов

Для защиты фотографий и документов применяются следующие меры:

- Хранение в приватной директории приложения, недоступной другим приложениям
- Контролируемый доступ к файлам через FileProvider
- Безопасное удаление файлов при удалении связанных с ними записей

### Аварийное восстановление

Реализован механизм восстановления при нарушении целостности криптографических ключей:

```kotlin
// Обработка ошибок целостности ключей
try {
    // Использование зашифрованного хранилища
} catch (e: Exception) {
    if (e is AEADBadTagException || e.cause is AEADBadTagException) {
        // Сброс повреждённых ключей и восстановление хранилища
        resetEncryptedStorage()
    }
    
    // Использование запасного метода
    isUsingFallback = true
    fallbackPreferences
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
    fun provideClientRepository(clientDao: ClientDao): ClientRepository { ... }
    
    @Provides
    @Singleton
    fun provideAppointmentRepository(appointmentDao: AppointmentDao): AppointmentRepository { ... }
    
    @Provides
    @Singleton
    fun provideImageRepository(@ApplicationContext context: Context): ImageRepository { ... }
    
    @Provides
    @Singleton
    fun provideClientUseCases(repository: ClientRepository): ClientUseCases { ... }
    
    @Provides
    @Singleton
    fun provideAppointmentUseCases(repository: AppointmentRepository): AppointmentUseCases { ... }
    
    @Provides
    @Singleton
    fun provideImageUseCases(repository: ImageRepository): ImageUseCases { ... }
}
```

### DatabaseModule

Предоставляет компоненты базы данных:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase { ... }
    
    @Provides
    @Singleton
    fun providePropertyDao(database: AppDatabase): PropertyDao { ... }
    
    @Provides
    @Singleton
    fun provideClientDao(database: AppDatabase): ClientDao { ... }
    
    @Provides
    @Singleton
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao { ... }
}
```

## Навигация

Навигация в приложении реализована с использованием Jetpack Navigation Compose:

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "property_list") {
        // Экраны для работы с объектами недвижимости
        composable("property_list") { PropertyListScreen(navController) }
        composable("property_detail/{propertyId}") { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            PropertyDetailScreen(propertyId = propertyId, navController = navController)
        }
        composable("property_form?propertyId={propertyId}") { backStackEntry ->
            val propertyId = backStackEntry.arguments?.getString("propertyId")
            PropertyFormScreen(propertyId = propertyId, navController = navController)
        }
        
        // Экраны для работы с клиентами
        composable("client_list") { ClientListScreen(navController) }
        composable("client_detail/{clientId}") { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            ClientDetailScreen(clientId = clientId, navController = navController)
        }
        composable("client_form?clientId={clientId}") { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId")
            ClientFormScreen(clientId = clientId, navController = navController)
        }
        
        // Экраны для работы с встречами
        composable("appointment_list") { AppointmentListScreen(navController) }
        composable("appointment_detail/{appointmentId}") { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")
            AppointmentDetailScreen(appointmentId = appointmentId, navController = navController)
        }
        composable("appointment_form?appointmentId={appointmentId}") { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")
            AppointmentFormScreen(appointmentId = appointmentId, navController = navController)
        }
        
        // Календарь встреч
        composable("calendar") { CalendarScreen(navController) }
    }
}
```

## Технический долг и планы развития

### Технический долг

1. **Оптимизация производительности**
   - Оптимизация загрузки и кэширования изображений
   - Пагинация для больших списков объектов и клиентов
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
