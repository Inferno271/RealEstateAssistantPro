package com.realestateassistant.pro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor() : ViewModel() {
    private val _propertyTypes = MutableStateFlow(listOf(
        "Квартира", "Дом", "Таунхаус", "Комната",
        "Студия", "Апартаменты", "Пентхаус", "Дуплекс"
    ))
    val propertyTypes: StateFlow<List<String>> = _propertyTypes

    private val _districts = MutableStateFlow(listOf(
        "Центральный", "Адмиралтейский", "Василеостровский",
        "Выборгский", "Калининский", "Кировский"
    ))
    val districts: StateFlow<List<String>> = _districts

    private val _layouts = MutableStateFlow(listOf(
        "Студия", "Свободная планировка", "Смежные комнаты",
        "Изолированные комнаты", "Евро-планировка",
        "Распашонка", "Линейная", "Коридорная"
    ))
    val layouts: StateFlow<List<String>> = _layouts

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

    private val _nearbyObjects = MutableStateFlow(listOf(
        "Метро", "Школа", "Детский сад", "Супермаркет",
        "Парк", "Торговый центр", "Фитнес-центр",
        "Аптека", "Поликлиника", "Остановка транспорта",
        "Ресторан", "Кафе", "Банк", "Почта"
    ))
    val nearbyObjects: StateFlow<List<String>> = _nearbyObjects

    private val _views = MutableStateFlow(listOf(
        "На улицу", "Во двор", "На парк", "На море",
        "На горы", "На реку", "На город", "На лес",
        "На озеро", "На сад", "Панорамный вид"
    ))
    val views: StateFlow<List<String>> = _views

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

    private val _petTypes = MutableStateFlow(listOf(
        "Кошки", "Собаки мелких пород",
        "Собаки средних пород", "Собаки крупных пород",
        "Птицы", "Грызуны", "Рептилии", "Рыбки",
        "Без ограничений"
    ))
    val petTypes: StateFlow<List<String>> = _petTypes

    fun updatePropertyTypes(newList: List<String>) {
        _propertyTypes.value = newList
    }

    fun updateDistricts(newList: List<String>) {
        _districts.value = newList
    }

    fun updateLayouts(newList: List<String>) {
        _layouts.value = newList
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

    fun updateNearbyObjects(newList: List<String>) {
        _nearbyObjects.value = newList
    }

    fun updateViews(newList: List<String>) {
        _views.value = newList
    }

    fun updateAmenities(newList: List<String>) {
        _amenities.value = newList
    }

    fun updatePetTypes(newList: List<String>) {
        _petTypes.value = newList
    }
} 