package com.realestateassistant.pro.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realestateassistant.pro.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для работы со справочниками опций.
 * Сохраняет списки опций в DataStore и предоставляет API для их получения и обновления.
 */
@HiltViewModel
class OptionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val preferencesManager = PreferencesManager(context)
    
    // Справочники для объектов недвижимости
    private val _propertyTypes = MutableStateFlow<List<String>>(emptyList())
    val propertyTypes: StateFlow<List<String>> = _propertyTypes.asStateFlow()
    
    private val _districts = MutableStateFlow<List<String>>(emptyList())
    val districts: StateFlow<List<String>> = _districts.asStateFlow()
    
    private val _layouts = MutableStateFlow<List<String>>(emptyList())
    val layouts: StateFlow<List<String>> = _layouts.asStateFlow()

    private val _repairStates = MutableStateFlow<List<String>>(emptyList())
    val repairStates: StateFlow<List<String>> = _repairStates.asStateFlow()

    private val _bathroomTypes = MutableStateFlow<List<String>>(emptyList())
    val bathroomTypes: StateFlow<List<String>> = _bathroomTypes.asStateFlow()

    private val _heatingTypes = MutableStateFlow<List<String>>(emptyList())
    val heatingTypes: StateFlow<List<String>> = _heatingTypes.asStateFlow()

    private val _parkingTypes = MutableStateFlow<List<String>>(emptyList())
    val parkingTypes: StateFlow<List<String>> = _parkingTypes.asStateFlow()

    private val _nearbyObjects = MutableStateFlow<List<String>>(emptyList())
    val nearbyObjects: StateFlow<List<String>> = _nearbyObjects.asStateFlow()
    
    private val _views = MutableStateFlow<List<String>>(emptyList())
    val views: StateFlow<List<String>> = _views.asStateFlow()

    private val _amenities = MutableStateFlow<List<String>>(emptyList())
    val amenities: StateFlow<List<String>> = _amenities.asStateFlow()

    private val _poolTypes = MutableStateFlow<List<String>>(emptyList())
    val poolTypes: StateFlow<List<String>> = _poolTypes.asStateFlow()
    
    private val _management = MutableStateFlow<List<String>>(emptyList())
    val management: StateFlow<List<String>> = _management.asStateFlow()
    
    // Справочники для клиентов
    private val _familyCompositions = MutableStateFlow<List<String>>(emptyList())
    val familyCompositions: StateFlow<List<String>> = _familyCompositions.asStateFlow()
    
    private val _petTypes = MutableStateFlow<List<String>>(emptyList())
    val petTypes: StateFlow<List<String>> = _petTypes.asStateFlow()
    
    private val _childAgeCategories = MutableStateFlow<List<String>>(emptyList())
    val childAgeCategories: StateFlow<List<String>> = _childAgeCategories.asStateFlow()
    
    private val _occupations = MutableStateFlow<List<String>>(emptyList())
    val occupations: StateFlow<List<String>> = _occupations.asStateFlow()

    // Новый справочник для налоговых опций
    private val _taxOptions = MutableStateFlow<List<String>>(emptyList())
    val taxOptions: StateFlow<List<String>> = _taxOptions.asStateFlow()

    // Новые справочники для гибкости поиска
    private val _urgencyLevels = MutableStateFlow<List<String>>(emptyList())
    val urgencyLevels: StateFlow<List<String>> = _urgencyLevels.asStateFlow()
    
    private val _possibleRequirements = MutableStateFlow<List<String>>(emptyList())
    val possibleRequirements: StateFlow<List<String>> = _possibleRequirements.asStateFlow()
    
    private val _priorityCriteria = MutableStateFlow<List<String>>(emptyList())
    val priorityCriteria: StateFlow<List<String>> = _priorityCriteria.asStateFlow()
    
    // Новые справочники для фильтров клиентов
    private val _rentalTypes = MutableStateFlow<List<String>>(emptyList())
    val rentalTypes: StateFlow<List<String>> = _rentalTypes.asStateFlow()
    
    private val _peopleCountRanges = MutableStateFlow<List<String>>(emptyList())
    val peopleCountRanges: StateFlow<List<String>> = _peopleCountRanges.asStateFlow()
    
    private val _longTermBudgetOptions = MutableStateFlow<List<String>>(emptyList())
    val longTermBudgetOptions: StateFlow<List<String>> = _longTermBudgetOptions.asStateFlow()
    
    private val _shortTermBudgetOptions = MutableStateFlow<List<String>>(emptyList())
    val shortTermBudgetOptions: StateFlow<List<String>> = _shortTermBudgetOptions.asStateFlow()
    
    init {
        loadOptions()
    }
    
    private fun loadOptions() {
        viewModelScope.launch {
            // Загружаем справочники объектов недвижимости
            _propertyTypes.value = preferencesManager.getStringList(KEY_PROPERTY_TYPES, DEFAULT_PROPERTY_TYPES)
            _districts.value = preferencesManager.getStringList(KEY_DISTRICTS, DEFAULT_DISTRICTS)
            _layouts.value = preferencesManager.getStringList(KEY_LAYOUTS, DEFAULT_LAYOUTS)
            _repairStates.value = preferencesManager.getStringList(KEY_REPAIR_STATES, DEFAULT_REPAIR_STATES)
            _bathroomTypes.value = preferencesManager.getStringList(KEY_BATHROOM_TYPES, DEFAULT_BATHROOM_TYPES)
            _heatingTypes.value = preferencesManager.getStringList(KEY_HEATING_TYPES, DEFAULT_HEATING_TYPES)
            _parkingTypes.value = preferencesManager.getStringList(KEY_PARKING_TYPES, DEFAULT_PARKING_TYPES)
            _nearbyObjects.value = preferencesManager.getStringList(KEY_NEARBY_OBJECTS, DEFAULT_NEARBY_OBJECTS)
            _views.value = preferencesManager.getStringList(KEY_VIEWS, DEFAULT_VIEWS)
            _amenities.value = preferencesManager.getStringList(KEY_AMENITIES, DEFAULT_AMENITIES)
            _poolTypes.value = preferencesManager.getStringList(KEY_POOL_TYPES, DEFAULT_POOL_TYPES)
            _management.value = preferencesManager.getStringList(KEY_MANAGEMENT, DEFAULT_MANAGEMENT)
            
            // Загружаем справочники клиентов
            _familyCompositions.value = preferencesManager.getStringList(KEY_FAMILY_COMPOSITIONS, DEFAULT_FAMILY_COMPOSITIONS)
            _petTypes.value = preferencesManager.getStringList(KEY_PET_TYPES, DEFAULT_PET_TYPES)
            _childAgeCategories.value = preferencesManager.getStringList(KEY_CHILD_AGE_CATEGORIES, DEFAULT_CHILD_AGE_CATEGORIES)
            _occupations.value = preferencesManager.getStringList(KEY_OCCUPATIONS, DEFAULT_OCCUPATIONS)
            _taxOptions.value = preferencesManager.getStringList(KEY_TAX_OPTIONS, DEFAULT_TAX_OPTIONS)
            
            // Загружаем новые справочники для гибкости поиска
            _urgencyLevels.value = preferencesManager.getStringList(KEY_URGENCY_LEVELS, DEFAULT_URGENCY_LEVELS)
            _possibleRequirements.value = preferencesManager.getStringList(KEY_POSSIBLE_REQUIREMENTS, DEFAULT_POSSIBLE_REQUIREMENTS)
            _priorityCriteria.value = preferencesManager.getStringList(KEY_PRIORITY_CRITERIA, DEFAULT_PRIORITY_CRITERIA)
            
            // Загружаем новые справочники для фильтров клиентов
            _rentalTypes.value = preferencesManager.getStringList(KEY_RENTAL_TYPES, DEFAULT_RENTAL_TYPES)
            _peopleCountRanges.value = preferencesManager.getStringList(KEY_PEOPLE_COUNT_RANGES, DEFAULT_PEOPLE_COUNT_RANGES)
            _longTermBudgetOptions.value = preferencesManager.getStringList(KEY_LONG_TERM_BUDGET_OPTIONS, DEFAULT_LONG_TERM_BUDGET_OPTIONS.map { it.toString() })
            _shortTermBudgetOptions.value = preferencesManager.getStringList(KEY_SHORT_TERM_BUDGET_OPTIONS, DEFAULT_SHORT_TERM_BUDGET_OPTIONS.map { it.toString() })
        }
    }
    
    // Методы для обновления справочников объектов недвижимости
    fun updatePropertyTypes(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_PROPERTY_TYPES, options)
            _propertyTypes.value = options
        }
    }
    
    fun updateDistricts(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_DISTRICTS, options)
            _districts.value = options
        }
    }
    
    fun updateLayouts(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_LAYOUTS, options)
            _layouts.value = options
        }
    }

    fun updateRepairStates(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_REPAIR_STATES, options)
            _repairStates.value = options
        }
    }

    fun updateBathroomTypes(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_BATHROOM_TYPES, options)
            _bathroomTypes.value = options
        }
    }

    fun updateHeatingTypes(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_HEATING_TYPES, options)
            _heatingTypes.value = options
        }
    }

    fun updateParkingTypes(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_PARKING_TYPES, options)
            _parkingTypes.value = options
        }
    }

    fun updateNearbyObjects(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_NEARBY_OBJECTS, options)
            _nearbyObjects.value = options
        }
    }
    
    fun updateViews(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_VIEWS, options)
            _views.value = options
        }
    }

    fun updateAmenities(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_AMENITIES, options)
            _amenities.value = options
        }
    }

    fun updatePoolTypes(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_POOL_TYPES, options)
            _poolTypes.value = options
        }
    }
    
    fun updateManagement(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_MANAGEMENT, options)
            _management.value = options
        }
    }
    
    // Методы для обновления справочников клиентов
    fun updateFamilyCompositions(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_FAMILY_COMPOSITIONS, options)
            _familyCompositions.value = options
        }
    }
    
    fun updatePetTypes(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_PET_TYPES, options)
            _petTypes.value = options
        }
    }
    
    fun updateChildAgeCategories(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_CHILD_AGE_CATEGORIES, options)
            _childAgeCategories.value = options
        }
    }
    
    fun updateOccupations(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_OCCUPATIONS, options)
            _occupations.value = options
        }
    }
    
    // Методы для обновления новых справочников
    fun updateTaxOptions(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_TAX_OPTIONS, options)
            _taxOptions.value = options
        }
    }
    
    // Методы для обновления новых справочников гибкости поиска
    fun updateUrgencyLevels(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_URGENCY_LEVELS, options)
            _urgencyLevels.value = options
        }
    }
    
    fun updatePossibleRequirements(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_POSSIBLE_REQUIREMENTS, options)
            _possibleRequirements.value = options
        }
    }
    
    fun updatePriorityCriteria(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_PRIORITY_CRITERIA, options)
            _priorityCriteria.value = options
        }
    }
    
    // Методы для обновления новых справочников фильтров клиентов
    fun updateRentalTypes(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_RENTAL_TYPES, options)
            _rentalTypes.value = options
        }
    }
    
    fun updatePeopleCountRanges(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_PEOPLE_COUNT_RANGES, options)
            _peopleCountRanges.value = options
        }
    }
    
    fun updateLongTermBudgetOptions(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_LONG_TERM_BUDGET_OPTIONS, options)
            _longTermBudgetOptions.value = options
        }
    }
    
    fun updateShortTermBudgetOptions(options: List<String>) {
        viewModelScope.launch {
            preferencesManager.saveStringList(KEY_SHORT_TERM_BUDGET_OPTIONS, options)
            _shortTermBudgetOptions.value = options
        }
    }
    
    // Общий метод для обновления произвольных справочников
    fun updateCustomOptions(key: String, options: List<String>) {
        viewModelScope.launch {
            when (key) {
                "taxOptions" -> updateTaxOptions(options)
                "rentalTypes" -> updateRentalTypes(options)
                "peopleCountRanges" -> updatePeopleCountRanges(options)
                "longTermBudgetOptions" -> updateLongTermBudgetOptions(options)
                "shortTermBudgetOptions" -> updateShortTermBudgetOptions(options)
                // Другие справочники можно добавить по мере необходимости
                else -> {} // Ничего не делаем для неизвестных ключей
            }
        }
    }
    
    companion object {
        // Ключи для сохранения справочников
        private const val KEY_PROPERTY_TYPES = "property_types"
        private const val KEY_DISTRICTS = "districts"
        private const val KEY_LAYOUTS = "layouts"
        private const val KEY_NEARBY_OBJECTS = "nearby_objects"
        private const val KEY_VIEWS = "views"
        private const val KEY_POOL_TYPES = "pool_types"
        private const val KEY_MANAGEMENT = "management"
        private const val KEY_REPAIR_STATES = "repair_states"
        private const val KEY_BATHROOM_TYPES = "bathroom_types"
        private const val KEY_HEATING_TYPES = "heating_types"
        private const val KEY_PARKING_TYPES = "parking_types"
        private const val KEY_AMENITIES = "amenities"
        
        private const val KEY_FAMILY_COMPOSITIONS = "family_compositions"
        private const val KEY_PET_TYPES = "pet_types"
        private const val KEY_CHILD_AGE_CATEGORIES = "child_age_categories"
        private const val KEY_OCCUPATIONS = "occupations"
        private const val KEY_TAX_OPTIONS = "tax_options"
        
        // Новые ключи для гибкости поиска
        private const val KEY_URGENCY_LEVELS = "urgency_levels"
        private const val KEY_POSSIBLE_REQUIREMENTS = "possible_requirements"
        private const val KEY_PRIORITY_CRITERIA = "priority_criteria"
        
        // Новые ключи для фильтров клиентов
        private const val KEY_RENTAL_TYPES = "rental_types"
        private const val KEY_PEOPLE_COUNT_RANGES = "people_count_ranges"
        private const val KEY_LONG_TERM_BUDGET_OPTIONS = "long_term_budget_options"
        private const val KEY_SHORT_TERM_BUDGET_OPTIONS = "short_term_budget_options"
        
        // Значения по умолчанию для справочников
        private val DEFAULT_PROPERTY_TYPES = listOf(
            "Квартира", "Дом", "Апартаменты", "Коттедж", "Таунхаус", "Студия"
        )
        
        private val DEFAULT_DISTRICTS = listOf(
            "Ленинский", "Гагаринский", "Нахимовский", "Балаклавский", "Инкерманский", "Северная сторона", "Центр"
        )
        
        private val DEFAULT_LAYOUTS = listOf(
            "Изолированная", "Смежная", "Свободная планировка", "Распашонка", "Евродвушка", "Евротрешка"
        )
        
        private val DEFAULT_NEARBY_OBJECTS = listOf(
            "Метро", "Школа", "Детский сад", "Парк", "Торговый центр", "Супермаркет", "Кафе", "Ресторан"
        )
        
        private val DEFAULT_VIEWS = listOf(
            "Море", "Город", "Парк", "Двор", "Улица", "Лес", "Горы"
        )
        
        private val DEFAULT_POOL_TYPES = listOf(
            "Открытый", "Крытый", "С подогревом", "Детский", "Бассейн-инфинити"
        )
        
        private val DEFAULT_MANAGEMENT = listOf(
            "УК", "ТСЖ", "Самоуправление"
        )
        
        private val DEFAULT_REPAIR_STATES = listOf(
            "Без ремонта", "Косметический", "Евроремонт",
            "Дизайнерский", "Требует ремонта"
        )
        
        private val DEFAULT_BATHROOM_TYPES = listOf(
            "Совмещенный", "Раздельный", "2 санузла",
            "3 санузла", "Без удобств"
        )
        
        private val DEFAULT_HEATING_TYPES = listOf(
            "Центральное", "Автономное", "Газовое",
            "Электрическое", "Печное"
        )
        
        private val DEFAULT_PARKING_TYPES = listOf(
            "Подземная", "Наземная", "Многоуровневая",
            "Во дворе", "На улице"
        )
        
        private val DEFAULT_AMENITIES = listOf(
            "Кондиционер", "Интернет", "Телевизор",
            "Холодильник", "Стиральная машина",
            "Посудомоечная машина", "Микроволновая печь",
            "Духовой шкаф", "Варочная панель",
            "Мебель", "Гардероб", "Сейф",
            "Видеонаблюдение", "Охрана", "Домофон",
            "Балкон/Лоджия", "Терраса"
        )
        
        private val DEFAULT_FAMILY_COMPOSITIONS = listOf(
            "Один человек", "Семейная пара", "Семья с детьми", "Студенты", "Коллеги", "Пожилая пара"
        )
        
        private val DEFAULT_PET_TYPES = listOf(
            "Кошка", "Собака мелкой породы", "Собака средней породы", "Собака крупной породы", "Птица", "Рыбки", "Грызуны"
        )
        
        private val DEFAULT_CHILD_AGE_CATEGORIES = listOf(
            "До 1 года", "1-3 года", "3-7 лет", "7-12 лет", "12-18 лет"
        )
        
        private val DEFAULT_OCCUPATIONS = listOf(
            "Офисный работник", "Предприниматель", "Фрилансер", "Студент", "Пенсионер", "Военнослужащий", "Медицинский работник", "IT-специалист"
        )
        
        private val DEFAULT_TAX_OPTIONS = listOf(
            "Самозанятый", "НДФЛ (физлицо)", "ООО/ИП (безналичный расчет)", "Любой вариант"
        )
        
        // Значения по умолчанию для новых справочников
        private val DEFAULT_URGENCY_LEVELS = listOf(
            "Срочно (до недели)", "В ближайший месяц", "В перспективе (более месяца)"
        )
        
        private val DEFAULT_POSSIBLE_REQUIREMENTS = listOf(
            "Район", "Этаж", "Площадь", "Количество комнат", "Мебель/техника", "Парковка", "Балкон/лоджия", "Ремонт"
        )
        
        private val DEFAULT_PRIORITY_CRITERIA = listOf(
            "Цена", "Район", "Этаж", "Площадь", "Количество комнат", "Мебель/техника", "Парковка", 
            "Транспортная доступность", "Инфраструктура", "Качество ремонта", "Тишина"
        )
        
        // Значения по умолчанию для фильтров клиентов
        private val DEFAULT_RENTAL_TYPES = listOf(
            "длительная", "посуточная"
        )
        
        private val DEFAULT_PEOPLE_COUNT_RANGES = listOf(
            "1 человек", "2 человека", "3-4 человека", "5+ человек"
        )
        
        private val DEFAULT_LONG_TERM_BUDGET_OPTIONS = listOf(
            10000.0, 15000.0, 20000.0, 25000.0, 30000.0, 40000.0, 50000.0, 75000.0, 100000.0, 150000.0
        )
        
        private val DEFAULT_SHORT_TERM_BUDGET_OPTIONS = listOf(
            1000.0, 1500.0, 2000.0, 3000.0, 4000.0, 5000.0, 7000.0, 10000.0, 15000.0, 20000.0
        )
    }
} 