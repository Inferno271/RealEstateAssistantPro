package com.realestateassistant.pro.core.test

import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.SeasonalPrice
import java.util.UUID
import kotlin.random.Random
import java.time.LocalDate
import java.time.ZoneId
import java.time.Month
import java.time.Year

/**
 * Генератор тестовых данных для заполнения приложения
 * Используется только для демонстрационных и тестовых целей
 */
object TestDataGenerator {

    /**
     * Генерирует список тестовых объектов недвижимости
     * @param count количество объектов для генерации
     * @return список сгенерированных объектов недвижимости
     */
    fun generateProperties(count: Int = 20): List<Property> {
        val properties = mutableListOf<Property>()
        
        repeat(count) {
            val propertyType = propertyTypes.random()
            val district = districts.random()
            val rooms = Random.nextInt(1, 5)
            val isStudio = rooms == 1 && Random.nextBoolean()
            val area = when (propertyType) {
                "Квартира" -> Random.nextDouble(30.0, 120.0)
                "Дом" -> Random.nextDouble(80.0, 300.0)
                "Коммерческая недвижимость" -> Random.nextDouble(50.0, 500.0)
                else -> Random.nextDouble(30.0, 120.0)
            }
            
            val useShortTerm = Random.nextBoolean()
            val useLongTerm = !useShortTerm || Random.nextBoolean()
            
            // Вспомогательные списки для выбора случайных элементов
            val viewsList = if (views.isNotEmpty()) { 
                if (Random.nextBoolean()) listOf(views.random()) else emptyList() 
            } else { 
                emptyList() 
            }
            
            val property = Property(
                id = UUID.randomUUID().toString(),
                propertyType = propertyType,
                address = generateAddress(district),
                district = district,
                area = roundToOneDecimal(area),
                roomsCount = if (isStudio) 0 else rooms,
                isStudio = isStudio,
                floor = Random.nextInt(1, 20),
                totalFloors = Random.nextInt(5, 25),
                contactName = if (contactNames.isNotEmpty()) contactNames.random() else "Контакт",
                contactPhone = listOf(generatePhoneNumber()),
                nearbyObjects = generateNearbyObjects(),
                views = viewsList,
                repairState = if (repairStates.isNotEmpty()) repairStates.random() else "Стандартный",
                bathroomsCount = Random.nextInt(1, 3),
                hasParking = Random.nextBoolean(),
                balconiesCount = Random.nextInt(0, 2),
                description = generateDescription(propertyType, district, area, rooms),
                photos = emptyList(), // Фото нужно будет добавлять отдельно
                
                // Дополнительные поля в зависимости от типа недвижимости
                levelsCount = if (propertyType == "Дом") Random.nextInt(1, 3) else 0,
                landArea = if (propertyType == "Дом") Random.nextDouble(2.0, 20.0) else 0.0,
                hasGarage = if (propertyType == "Дом") Random.nextBoolean() else false,
                
                // Условия проживания
                childrenAllowed = Random.nextBoolean(),
                petsAllowed = Random.nextBoolean(),
                smokingAllowed = Random.nextBoolean(),
                
                // Координаты
                latitude = getDistrictCoordinates(district)?.first,
                longitude = getDistrictCoordinates(district)?.second,
                
                // Условия аренды
                monthlyRent = if (useLongTerm) 
                    (Random.nextDouble(20000.0, 150000.0).toInt()).toDouble() else null,
                dailyPrice = if (useShortTerm) 
                    (Random.nextDouble(2000.0, 15000.0).toInt()).toDouble() else null,
                depositCustomAmount = if (useLongTerm) 
                    (Random.nextDouble(20000.0, 100000.0).toInt()).toDouble() else null,
                minStayDays = if (useShortTerm) 
                    Random.nextInt(1, 7) else null,
                checkInTime = if (useShortTerm) 
                    listOf("14:00", "15:00", "16:00").random() else null,
                checkOutTime = if (useShortTerm) 
                    listOf("11:00", "12:00", "13:00").random() else null,
                
                // Вычисляем сезонные цены для посуточной аренды
                seasonalPrices = if (useShortTerm) {
                    val price = (Random.nextDouble(2000.0, 15000.0).toInt()).toDouble()
                    generateSeasonalPrices(price)
                } else emptyList(),
                
                // Ограничиваем генерацию дат для предотвращения переполнения
                createdAt = System.currentTimeMillis() - Random.nextLong(0, 30L * 24L * 60L * 60L * 1000L),
                updatedAt = System.currentTimeMillis()
            )
            
            properties.add(property)
        }
        
        return properties
    }

    /**
     * Генерирует список тестовых клиентов
     * @param count количество клиентов для генерации
     * @return список сгенерированных клиентов
     */
    fun generateClients(count: Int = 15): List<Client> {
        val clients = mutableListOf<Client>()
        
        // Убедимся, что у нас есть типы аренды
        val rentalTypes = listOf("длительная", "посуточная", "обе")
        
        repeat(count) {
            val rentalType = if (rentalTypes.isNotEmpty()) rentalTypes.random() else "длительная"
            
            // Безопасные расчеты бюджета
            val longTermBudgetMin = if (rentalType != "посуточная") 
                (Random.nextDouble(15000.0, 60000.0).toInt()).toDouble() else null
            val longTermBudgetMax = if (longTermBudgetMin != null) 
                longTermBudgetMin + (Random.nextDouble(10000.0, 80000.0).toInt()).toDouble() else null
            
            val shortTermBudgetMin = if (rentalType != "длительная") 
                (Random.nextDouble(1500.0, 5000.0).toInt()).toDouble() else null
            val shortTermBudgetMax = if (shortTermBudgetMin != null) 
                shortTermBudgetMin + (Random.nextDouble(1000.0, 8000.0).toInt()).toDouble() else null
            
            // Проверяем размеры списков перед случайным выбором
            val preferredPropertyTypes = if (propertyTypes.size >= 3) {
                propertyTypes.shuffled().take(Random.nextInt(1, 3))
            } else if (propertyTypes.isNotEmpty()) {
                propertyTypes.take(1)
            } else {
                listOf("Квартира")
            }
            
            val preferredDistricts = if (districts.size >= 4) {
                districts.shuffled().take(Random.nextInt(1, 4)).joinToString(", ")
            } else if (districts.isNotEmpty()) {
                districts.take(1).joinToString(", ")
            } else {
                "Центральный"
            }
            
            val hasPets = Random.nextBoolean()
            val peopleCount = Random.nextInt(1, 5)
            val childrenCount = if (peopleCount > 1) Random.nextInt(0, peopleCount) else 0
            
            val client = Client(
                id = UUID.randomUUID().toString(),
                fullName = generateFullName(),
                phone = listOf(generatePhoneNumber()),
                rentalType = rentalType,
                desiredPropertyType = preferredPropertyTypes.joinToString(", "),
                preferredDistrict = preferredDistricts,
                desiredArea = Random.nextDouble(30.0, 80.0),
                desiredRoomsCount = Random.nextInt(1, 4),
                additionalRequirements = if (Random.nextBoolean()) generateAdditionalRequirements() else null,
                comment = if (Random.nextBoolean()) "Обратился по рекомендации" else "",
                
                // Финансовые параметры
                longTermBudgetMin = longTermBudgetMin,
                longTermBudgetMax = longTermBudgetMax,
                shortTermBudgetMin = shortTermBudgetMin,
                shortTermBudgetMax = shortTermBudgetMax,
                
                // Семейное положение
                familyComposition = generateFamilyComposition(),
                peopleCount = peopleCount,
                childrenCount = childrenCount,
                childrenAges = if (childrenCount > 0) 
                    List(childrenCount) { Random.nextInt(1, 18) } else emptyList(),
                hasPets = hasPets,
                petTypes = if (hasPets) {
                    val petTypesList = listOf("кошка", "собака", "грызуны")
                    if (petTypesList.size >= 3) {
                        petTypesList.shuffled().take(Random.nextInt(1, 3))
                    } else if (petTypesList.isNotEmpty()) {
                        petTypesList.take(1)
                    } else {
                        emptyList()
                    }
                } else emptyList(),
                petCount = if (hasPets) Random.nextInt(1, 3) else null,
                
                // Дополнительные предпочтения
                preferredAmenities = if (amenities.size >= 5) {
                    amenities.shuffled().take(Random.nextInt(1, 5))
                } else if (amenities.isNotEmpty()) {
                    amenities.take(1)
                } else {
                    emptyList()
                },
                preferredViews = if (views.isNotEmpty() && Random.nextBoolean()) 
                    listOf(views.random()) else emptyList(),
                preferredRepairState = if (repairStates.isNotEmpty()) 
                    repairStates.random() else "Стандартный",
                preferredFloorMin = if (Random.nextBoolean()) Random.nextInt(2, 5) else null,
                preferredFloorMax = if (Random.nextBoolean()) Random.nextInt(10, 20) else null,
                needsParking = Random.nextBoolean(),
                needsFurniture = !Random.nextBoolean(),
                
                // Метаданные
                createdAt = System.currentTimeMillis() - Random.nextLong(0, 90L * 24L * 60L * 60L * 1000L),
                updatedAt = System.currentTimeMillis()
            )
            
            clients.add(client)
        }
        
        return clients
    }

    // Вспомогательные списки для генерации данных
    private val propertyTypes = listOf(
        "Квартира", "Студия", "Дом", "Таунхаус", "Коммерческая недвижимость", "Апартаменты"
    )
    
    private val districts = listOf(
        "Центральный", "Адмиралтейский", "Василеостровский", "Выборгский", 
        "Калининский", "Кировский", "Красногвардейский", "Красносельский", 
        "Московский", "Невский", "Петроградский", "Приморский", "Фрунзенский"
    )
    
    private val streets = listOf(
        "Невский проспект", "Лиговский проспект", "Московский проспект", "Каменноостровский проспект",
        "Большой проспект П.С.", "Большой проспект В.О.", "Вознесенский проспект", "Загородный проспект",
        "улица Восстания", "улица Рубинштейна", "улица Жуковского", "улица Марата",
        "Набережная реки Фонтанки", "Набережная канала Грибоедова", "Набережная Обводного канала",
        "Кронверкская набережная", "Дворцовая набережная", "Университетская набережная"
    )
    
    private val contactNames = listOf(
        "Александр", "Анна", "Дмитрий", "Екатерина", "Иван", "Мария", "Михаил", "Ольга", 
        "Сергей", "Татьяна", "Андрей", "Елена", "Артем", "Виктория", "Николай"
    )
    
    private val firstNames = listOf(
        "Александр", "Анна", "Дмитрий", "Екатерина", "Иван", "Мария", "Михаил", "Ольга", 
        "Сергей", "Татьяна", "Андрей", "Елена", "Артем", "Виктория", "Николай",
        "Алексей", "Наталья", "Владимир", "Юлия", "Игорь", "Светлана", "Максим", "Евгения"
    )
    
    private val lastNames = listOf(
        "Иванов", "Смирнов", "Кузнецов", "Попов", "Васильев", "Петров", "Соколов", 
        "Михайлов", "Новиков", "Федоров", "Морозов", "Волков", "Алексеев", "Лебедев",
        "Семенов", "Егоров", "Павлов", "Козлов", "Степанов", "Николаев", "Орлов"
    )
    
    private val amenities = listOf(
        "Кондиционер", "Посудомоечная машина", "Стиральная машина", "Телевизор", "Интернет",
        "Микроволновая печь", "Духовка", "Холодильник", "Чайник", "Утюг", "Фен", "Сушилка для белья"
    )
    
    private val views = listOf(
        "Вид на море", "Вид на парк", "Вид на город", "Вид на реку", "Вид на сад"
    )
    
    private val repairStates = listOf(
        "Стандартный", "Евроремонт", "Люкс", "Требует ремонта", "Под чистовую отделку"
    )
    
    // Функции генерации конкретных элементов
    private fun generateAddress(district: String): String {
        val street = streets.random()
        val house = Random.nextInt(1, 100)
        val building = if (Random.nextBoolean()) ", корп. ${Random.nextInt(1, 5)}" else ""
        val apartment = Random.nextInt(1, 200)
        return "$street, д. $house$building, кв. $apartment"
    }
    
    private fun generatePhoneNumber(): String {
        val prefix = listOf("+7 (921) ", "+7 (911) ", "+7 (905) ", "+7 (981) ").random()
        val part1 = Random.nextInt(100, 999)
        val part2 = Random.nextInt(10, 99)
        val part3 = Random.nextInt(10, 99)
        return "$prefix$part1-$part2-$part3"
    }
    
    private fun generateFullName(): String {
        // Проверяем, есть ли имена и фамилии
        if (firstNames.isEmpty() || lastNames.isEmpty()) {
            return "Иван Иванов"
        }
        
        val firstName = firstNames.random()
        val lastName = lastNames.random()
        val gender = if (firstName.endsWith('а') || firstName.endsWith('я')) "female" else "male"
        
        val formattedLastName = if (gender == "female") {
            // Для женских имен меняем окончание фамилии
            if (lastName.endsWith("ов") || lastName.endsWith("ев") || lastName.endsWith("ин")) {
                "${lastName}а"
            } else {
                lastName
            }
        } else {
            lastName
        }
        
        return "$formattedLastName $firstName"
    }
    
    private fun generateDescription(propertyType: String, district: String, area: Double, rooms: Int): String {
        val areaStr = area.toInt().toString()
        val roomsStr = if (rooms == 1) "однокомнатная" else if (rooms == 2) "двухкомнатная" else if (rooms == 3) "трехкомнатная" else "многокомнатная"
        
        val descriptionTemplates = listOf(
            "Светлая $roomsStr квартира площадью $areaStr м² в $district районе. Удобная транспортная доступность.",
            "Просторная $roomsStr квартира с качественным ремонтом в $district районе. Общая площадь $areaStr м².",
            "Уютная $roomsStr квартира в $district районе. Площадь $areaStr м². Развитая инфраструктура.",
            "$roomsStr квартира площадью $areaStr м² с современным ремонтом в $district районе.",
            "Комфортная $roomsStr квартира в $district районе площадью $areaStr м². Отличное состояние."
        )
        
        return descriptionTemplates.random()
    }
    
    private fun generateNearbyObjects(): List<String> {
        val objects = listOf(
            "Метро", "Школа", "Детский сад", "Поликлиника", "Супермаркет", "Парк", 
            "Фитнес-центр", "ТРЦ", "Кафе", "Ресторан", "Аптека", "Банк"
        )
        
        return if (objects.size >= 5) {
            objects.shuffled().take(Random.nextInt(2, 5))
        } else {
            objects.shuffled().take(Math.min(2, objects.size))
        }
    }
    
    private fun generateFamilyComposition(): String {
        val compositions = listOf(
            "Семейная пара", "Один человек", "Семья с детьми", "Семья с ребенком",
            "Молодая пара", "Пожилая пара", "Студент", "Работающий специалист"
        )
        
        return if (compositions.isNotEmpty()) compositions.random() else "Один человек"
    }
    
    private fun generateAdditionalRequirements(): String {
        val requirements = listOf(
            "Тихий район", "Близость к метро", "Наличие парковки", "Охраняемая территория",
            "Новый дом", "Хорошая звукоизоляция", "Зеленый двор", "Наличие лифта",
            "Качественная мебель", "Вся бытовая техника", "Без шумных соседей"
        )
        
        val selectedRequirements = if (requirements.size >= 4) {
            requirements.shuffled().take(Random.nextInt(1, 4))
        } else {
            requirements.shuffled().take(Math.min(1, requirements.size))
        }
        return selectedRequirements.joinToString(", ")
    }
    
    private fun getDistrictCoordinates(district: String): Pair<Double, Double>? {
        // Примерные координаты центров районов Санкт-Петербурга
        return when (district) {
            "Центральный" -> Pair(59.9339, 30.3339)
            "Адмиралтейский" -> Pair(59.9167, 30.3000)
            "Василеостровский" -> Pair(59.9442, 30.2811)
            "Выборгский" -> Pair(60.0358, 30.3205)
            "Калининский" -> Pair(59.9992, 30.3910)
            "Кировский" -> Pair(59.8650, 30.2606)
            "Красногвардейский" -> Pair(59.9706, 30.4748)
            "Красносельский" -> Pair(59.7640, 30.0903)
            "Московский" -> Pair(59.8529, 30.3208)
            "Невский" -> Pair(59.8736, 30.4375)
            "Петроградский" -> Pair(59.9631, 30.3111)
            "Приморский" -> Pair(60.0139, 30.2556)
            "Фрунзенский" -> Pair(59.8646, 30.3839)
            else -> Pair(59.9339, 30.3339) // Центр Петербурга по умолчанию
        }
    }
    
    private fun roundToOneDecimal(number: Double): Double {
        return Math.round(number * 10.0) / 10.0
    }

    private fun generateSeasonalPrices(basePrice: Double): List<SeasonalPrice> {
        val currentYear = LocalDate.now().year
        val seasonalPrices = mutableListOf<SeasonalPrice>()
        
        // Генерируем 2-3 сезонные цены
        val seasonsCount = Random.nextInt(2, 4)
        
        val seasons = listOf(
            Pair("Лето", Pair(6, 8)), // Июнь-Август
            Pair("Зима", Pair(12, 2)), // Декабрь-Февраль
            Pair("Весна", Pair(3, 5)), // Март-Май
            Pair("Осень", Pair(9, 11)) // Сентябрь-Ноябрь
        ).shuffled().take(seasonsCount)
        
        seasons.forEach { (_, monthRange) ->
            val startMonth = monthRange.first
            val endMonthValue = monthRange.second
            
            // Создаем даты в миллисекундах (с безопасными расчетами)
            val startDate = LocalDate.of(currentYear, startMonth, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            val adjustedEndMonth = if (endMonthValue < startMonth) endMonthValue + 12 else endMonthValue
            val endYear = if (adjustedEndMonth > 12) currentYear + 1 else currentYear
            val finalEndMonth = if (adjustedEndMonth > 12) adjustedEndMonth - 12 else adjustedEndMonth
            
            val endDate = LocalDate.of(endYear, finalEndMonth, 
                Month.of(finalEndMonth).length(Year.isLeap(endYear.toLong())))
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            // Безопасно вычисляем цену
            val priceMultiplier = 1.0 + Random.nextDouble(0.2, 0.8)
            val seasonalPrice = (basePrice * priceMultiplier).toInt().toDouble()
            
            seasonalPrices.add(
                SeasonalPrice(
                    startDate = startDate,
                    endDate = endDate,
                    price = seasonalPrice
                )
            )
        }
        
        return seasonalPrices
    }
} 