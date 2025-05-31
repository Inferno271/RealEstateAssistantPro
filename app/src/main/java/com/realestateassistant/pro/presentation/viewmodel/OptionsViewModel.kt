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
 * Сохраняет списки опций в SharedPreferences и предоставляет API для их получения и обновления.
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

    private val _repairStates = MutableStateFlow(listOf(
        "Без ремонта", "Косметический", "Евроремонт",
        "Дизайнерский", "Требует ремонта"
    ))
    val repairStates: StateFlow<List<String>> = _repairStates

    private val _bathroomTypes = MutableStateFlow(listOf(
        "Совмещенный", "Раздельный", "2 санузла",
        "3 санузла", "Без удобств"
    ))
    val bathroomTypes: StateFlow<List<String>> = _bathroomTypes

    private val _heatingTypes = MutableStateFlow(listOf(
        "Центральное", "Автономное", "Газовое",
        "Электрическое", "Печное"
    ))
    val heatingTypes: StateFlow<List<String>> = _heatingTypes

    private val _parkingTypes = MutableStateFlow(listOf(
        "Подземная", "Наземная", "Многоуровневая",
        "Во дворе", "На улице"
    ))
    val parkingTypes: StateFlow<List<String>> = _parkingTypes

    private val _nearbyObjects = MutableStateFlow<List<String>>(emptyList())
    val nearbyObjects: StateFlow<List<String>> = _nearbyObjects.asStateFlow()
    
    private val _views = MutableStateFlow<List<String>>(emptyList())
    val views: StateFlow<List<String>> = _views.asStateFlow()

    private val _amenities = MutableStateFlow(listOf(
        "Кондиционер", "Интернет", "Телевизор",
        "Холодильник", "Стиральная машина",
        "Посудомоечная машина", "Микроволновая печь",
        "Духовой шкаф", "Варочная панель",
        "Мебель", "Гардероб", "Сейф",
        "Видеонаблюдение", "Охрана", "Домофон",
        "Балкон/Лоджия", "Терраса"
    ))
    val amenities: StateFlow<List<String>> = _amenities

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
    
    init {
        loadOptions()
    }
    
    private fun loadOptions() {
        viewModelScope.launch {
            // Загружаем справочники объектов недвижимости
            _propertyTypes.value = preferencesManager.getStringList(KEY_PROPERTY_TYPES, DEFAULT_PROPERTY_TYPES)
            _districts.value = preferencesManager.getStringList(KEY_DISTRICTS, DEFAULT_DISTRICTS)
            _layouts.value = preferencesManager.getStringList(KEY_LAYOUTS, DEFAULT_LAYOUTS)
            _nearbyObjects.value = preferencesManager.getStringList(KEY_NEARBY_OBJECTS, DEFAULT_NEARBY_OBJECTS)
            _views.value = preferencesManager.getStringList(KEY_VIEWS, DEFAULT_VIEWS)
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

    fun updateRepairStates(newList: List<String>) {
        _repairStates.value = newList
    }

    fun updateBathroomTypes(newList: List<String>) {
        _bathroomTypes.value = newList
    }

    fun updateHeatingTypes(newList: List<String>) {
        _heatingTypes.value = newList
    }

    fun updateParkingTypes(newList: List<String>) {
        _parkingTypes.value = newList
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

    fun updateAmenities(newList: List<String>) {
        _amenities.value = newList
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
    
    // Общий метод для обновления произвольных справочников
    fun updateCustomOptions(key: String, options: List<String>) {
        viewModelScope.launch {
            when (key) {
                "taxOptions" -> updateTaxOptions(options)
                // Другие справочники можно добавить по мере необходимости
                else -> {} // Ничего не делаем для неизвестных ключей
            }
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
    
    companion object {
        // Ключи для сохранения справочников
        private const val KEY_PROPERTY_TYPES = "property_types"
        private const val KEY_DISTRICTS = "districts"
        private const val KEY_LAYOUTS = "layouts"
        private const val KEY_NEARBY_OBJECTS = "nearby_objects"
        private const val KEY_VIEWS = "views"
        private const val KEY_POOL_TYPES = "pool_types"
        private const val KEY_MANAGEMENT = "management"
        
        private const val KEY_FAMILY_COMPOSITIONS = "family_compositions"
        private const val KEY_PET_TYPES = "pet_types"
        private const val KEY_CHILD_AGE_CATEGORIES = "child_age_categories"
        private const val KEY_OCCUPATIONS = "occupations"
        private const val KEY_TAX_OPTIONS = "tax_options"
        
        // Новые ключи для гибкости поиска
        private const val KEY_URGENCY_LEVELS = "urgency_levels"
        private const val KEY_POSSIBLE_REQUIREMENTS = "possible_requirements"
        private const val KEY_PRIORITY_CRITERIA = "priority_criteria"
        
        // Значения по умолчанию для справочников
        private val DEFAULT_PROPERTY_TYPES = listOf(
            "Квартира", "Дом", "Апартаменты", "Коттедж", "Таунхаус", "Студия"
        )
        
        private val DEFAULT_DISTRICTS = listOf(
            "Центральный", "Ленинский", "Московский", "Приморский", "Петроградский", "Василеостровский"
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
    }
} 