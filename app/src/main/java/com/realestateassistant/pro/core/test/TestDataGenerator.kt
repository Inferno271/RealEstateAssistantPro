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
            
            // Определяем логичные параметры в зависимости от типа недвижимости
            val roomsCount: Int
            val isStudio: Boolean
            val area: Double
            val bathroomsCount: Int
            val hasBalcony: Boolean
            val totalFloors: Int
            
            when (propertyType) {
                "Студия" -> {
                    roomsCount = 0
                    isStudio = true
                    area = roundToNearest(Random.nextDouble(22.0, 45.0), 0.5)
                    bathroomsCount = 1
                    hasBalcony = Random.nextBoolean()
                    totalFloors = Random.nextInt(5, 25)
                }
                "Квартира" -> {
                    roomsCount = Random.nextInt(1, 5)
                    isStudio = false
                    area = roundToNearest(
                        when (roomsCount) {
                            1 -> Random.nextDouble(30.0, 50.0)
                            2 -> Random.nextDouble(45.0, 75.0)
                            3 -> Random.nextDouble(65.0, 100.0)
                            else -> Random.nextDouble(85.0, 130.0)
                        }, 0.5
                    )
                    bathroomsCount = when {
                        roomsCount <= 1 -> 1
                        roomsCount <= 3 -> Random.nextInt(1, 3)
                        else -> Random.nextInt(2, 4)
                    }
                    hasBalcony = Random.nextBoolean()
                    totalFloors = Random.nextInt(5, 25)
                }
                "Дом" -> {
                    roomsCount = Random.nextInt(2, 7)
                    isStudio = false
                    area = roundToNearest(
                        when {
                            roomsCount <= 3 -> Random.nextDouble(80.0, 150.0)
                            roomsCount <= 5 -> Random.nextDouble(130.0, 250.0)
                            else -> Random.nextDouble(200.0, 350.0)
                        }, 1.0
                    )
                    bathroomsCount = when {
                        roomsCount <= 3 -> Random.nextInt(1, 3)
                        else -> Random.nextInt(2, 4)
                    }
                    hasBalcony = false
                    totalFloors = Random.nextInt(1, 4) // Дома имеют 1-3 этажа
                }
                "Таунхаус" -> {
                    roomsCount = Random.nextInt(3, 6)
                    isStudio = false
                    area = roundToNearest(Random.nextDouble(100.0, 180.0), 1.0)
                    bathroomsCount = Random.nextInt(2, 4)
                    hasBalcony = false
                    totalFloors = 2 // Таунхаусы обычно 2-этажные
                }
                "Апартаменты" -> {
                    roomsCount = Random.nextInt(1, 4)
                    isStudio = false
                    area = roundToNearest(
                        when (roomsCount) {
                            1 -> Random.nextDouble(40.0, 60.0)
                            2 -> Random.nextDouble(55.0, 85.0)
                            else -> Random.nextDouble(75.0, 150.0)
                        }, 0.5
                    )
                    bathroomsCount = Random.nextInt(1, 3)
                    hasBalcony = Random.nextBoolean()
                    totalFloors = Random.nextInt(5, 30)
                }
                else -> {
                    roomsCount = 1
                    isStudio = false
                    area = 30.0
                    bathroomsCount = 1
                    hasBalcony = false
                    totalFloors = 5
                }
            }
            
            // Корректно определяем этаж в зависимости от типа недвижимости и этажности
            val floor = when (propertyType) {
                "Дом", "Таунхаус" -> 0 // Для домов и таунхаусов этаж не указывается (0)
                else -> {
                    // Для квартир, студий и прочего - этаж должен быть меньше этажности
                    if (totalFloors > 0) Random.nextInt(1, totalFloors + 1) else 1
                }
            }
            
            // Балконы/лоджии только для квартир, студий и апартаментов
            val balconiesCount = if (hasBalcony && 
                (propertyType == "Квартира" || propertyType == "Студия" || propertyType == "Апартаменты")) {
                Random.nextInt(1, 3) // 1 или 2 балкона
            } else {
                0
            }
            
            // Типы аренды в зависимости от района и типа недвижимости
            val useShortTerm = district == "Балаклавский" || 
                             district == "Ленинский" || 
                             propertyType == "Апартаменты" ||
                             Random.nextInt(10) < 4
            
            val useLongTerm = !useShortTerm || // Если не сдаётся посуточно, то долгосрочно
                              district != "Балаклавский" || // Не на курорте
                              Random.nextInt(10) < 3 // Небольшой шанс, что и так и так
            
            // Вспомогательные списки для выбора случайных элементов
            val viewsList = when (district) {
                "Балаклавский" -> listOf("Вид на море", "Вид на бухту").random().let { listOf(it) }
                "Ленинский" -> if (Random.nextBoolean()) listOf("Вид на море") else emptyList()
                "Гагаринский" -> if (Random.nextBoolean()) listOf("Вид на парк") else emptyList()
                "Нахимовский" -> if (Random.nextBoolean()) listOf("Вид на город") else emptyList()
                else -> emptyList()
            }
            
            // Генерируем цену в зависимости от типа недвижимости и района
            val baseRentMultiplier = when (district) {
                "Ленинский" -> 1.2
                "Гагаринский" -> 1.0
                "Нахимовский" -> 0.9
                "Балаклавский" -> 1.3
                else -> 1.0
            }
            
            // Базовые цены для долгосрочной аренды по типу недвижимости (в тысячах рублей)
            val monthlyRentBase = when (propertyType) {
                "Квартира" -> when (roomsCount) {
                    1 -> 25000.0
                    2 -> 35000.0
                    3 -> 45000.0
                    else -> 55000.0
                } * baseRentMultiplier
                "Студия" -> 20000.0 * baseRentMultiplier
                "Дом" -> 50000.0 * baseRentMultiplier
                "Таунхаус" -> 40000.0 * baseRentMultiplier
                "Апартаменты" -> 30000.0 * baseRentMultiplier
                else -> 25000.0 * baseRentMultiplier
            }
            
            // Базовые цены для краткосрочной аренды по типу недвижимости
            val dailyPriceBase = when (propertyType) {
                "Квартира" -> when (roomsCount) {
                    1 -> 2500.0
                    2 -> 3500.0
                    3 -> 4500.0
                    else -> 5500.0
                } * baseRentMultiplier
                "Студия" -> 2000.0 * baseRentMultiplier
                "Дом" -> 6000.0 * baseRentMultiplier
                "Таунхаус" -> 5000.0 * baseRentMultiplier
                "Апартаменты" -> 3500.0 * baseRentMultiplier
                else -> 2500.0 * baseRentMultiplier
            }
            
            // Определяем количество уровней и площадь земли для домов и таунхаусов
            val levelsCount = when (propertyType) {
                "Дом" -> Random.nextInt(1, 4) // 1-3 уровня
                "Таунхаус" -> 2 // Обычно 2 уровня
                else -> 0
            }
            
            val landArea = when (propertyType) {
                "Дом" -> roundToNearest(Random.nextDouble(4.0, 20.0), 0.5)
                "Таунхаус" -> roundToNearest(Random.nextDouble(1.0, 4.0), 0.5)
                else -> 0.0
            }
            
            // Гараж только для домов и таунхаусов
            val hasGarage = when (propertyType) {
                "Дом" -> Random.nextBoolean()
                "Таунхаус" -> Random.nextInt(10) < 7 // 70% таунхаусов имеют гараж
                else -> false
            }
            
            // Парковка чаще в определенных районах и для определенных типов
            val hasParking = when {
                propertyType == "Дом" || propertyType == "Таунхаус" -> true
                district == "Балаклавский" -> Random.nextInt(10) < 7 // 70% в Балаклаве
                district == "Ленинский" -> Random.nextInt(10) < 3 // 30% в центре
                else -> Random.nextInt(10) < 5 // 50% в других районах
            }
            
            // Округляем цены до красивых значений
            val finalMonthlyRent = if (useLongTerm) roundPrice(monthlyRentBase) else null
            val finalDailyPrice = if (useShortTerm) roundPrice(dailyPriceBase, 100.0) else null
            val finalDeposit = if (useLongTerm) roundPrice(monthlyRentBase) else null
            
            // Генерируем правильное состояние ремонта - не может быть пустым
            val repairState = repairStates.random()
            
            // Контактная информация - всегда должна быть заполнена
            val contactName = contactNames.random()
            val contactPhone = listOf(generatePhoneNumber())
            
            // Выбираем ближайшие объекты с учетом района
            val nearbyObjects = generateNearbyObjects(district)
            
            // Вычисляем даты создания и обновления
            val createdAt = System.currentTimeMillis() - Random.nextLong(0L, 30L * 24L * 60L * 60L * 1000L)
            val updatedAt = createdAt + Random.nextLong(0L, 5L * 24L * 60L * 60L * 1000L)
            
            // Генерируем удобства в зависимости от типа недвижимости
            val propertyAmenities = generateAmenities(propertyType, roomsCount)
            
            // Генерируем тип отопления
            val heatingType = heatingTypes.random()
            
            // Тип санузла
            val bathroomType = when {
                bathroomsCount > 1 -> "Раздельный"
                Random.nextBoolean() -> "Совмещенный"
                else -> "Раздельный"
            }
            
            // Тип парковки
            val parkingType = if (hasParking) {
                when {
                    propertyType == "Дом" || propertyType == "Таунхаус" -> "Личная"
                    Random.nextBoolean() -> "Общая"
                    else -> "Уличная"
                }
            } else null
            
            val property = Property(
                id = UUID.randomUUID().toString(),
                propertyType = propertyType,
                address = generateAddress(district),
                district = district,
                area = area,
                roomsCount = roomsCount,
                isStudio = isStudio,
                floor = floor,
                totalFloors = totalFloors,
                contactName = contactName,
                contactPhone = contactPhone,
                nearbyObjects = nearbyObjects,
                views = viewsList,
                repairState = repairState,
                bathroomsCount = bathroomsCount,
                bathroomType = bathroomType,
                hasParking = hasParking,
                parkingType = parkingType,
                balconiesCount = balconiesCount,
                description = generateDescription(propertyType, district, area, roomsCount),
                photos = emptyList(), // Фото нужно будет добавлять отдельно
                
                // Дополнительные поля в зависимости от типа недвижимости
                levelsCount = levelsCount,
                landArea = landArea,
                hasGarage = hasGarage,
                
                // Удобства
                amenities = propertyAmenities,
                heatingType = heatingType,
                hasAppliances = Random.nextInt(10) < 8, // 80% имеют технику
                noFurniture = Random.nextInt(10) < 2, // 20% без мебели
                
                // Условия проживания
                childrenAllowed = Random.nextInt(10) < 8, // 80% разрешают с детьми
                petsAllowed = Random.nextInt(10) < 4,     // 40% разрешают с животными
                smokingAllowed = Random.nextInt(10) < 3,  // 30% разрешают курить
                
                // Координаты
                latitude = getDistrictCoordinates(district)?.first,
                longitude = getDistrictCoordinates(district)?.second,
                
                // Условия аренды
                monthlyRent = finalMonthlyRent,
                depositCustomAmount = finalDeposit,
                dailyPrice = finalDailyPrice,
                minStayDays = if (useShortTerm) {
                    if (district == "Балаклавский") 3 else Random.nextInt(1, 5)
                } else null,
                checkInTime = if (useShortTerm) {
                    listOf("14:00", "15:00").random()
                } else null,
                checkOutTime = if (useShortTerm) {
                    listOf("12:00").random()
                } else null,
                
                // Сезонные цены только для краткосрочной аренды
                seasonalPrices = if (useShortTerm && finalDailyPrice != null) {
                    generateSeasonalPrices(finalDailyPrice)
                } else emptyList(),
                
                // Метаданные
                createdAt = createdAt,
                updatedAt = updatedAt
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
        
        repeat(count) {
            val rentalType = rentalTypes.random()
            
            // Безопасные расчеты бюджета с округлением
            val longTermBudgetMin = if (rentalType != "посуточная") 
                roundPrice(Random.nextInt(15, 50) * 1000.0) else null
            val longTermBudgetMax = if (longTermBudgetMin != null) 
                roundPrice(longTermBudgetMin + Random.nextInt(10, 50) * 1000.0) else null
            
            val shortTermBudgetMin = if (rentalType != "длительная") 
                roundPrice(Random.nextInt(1500, 4000).toDouble(), 500.0) else null
            val shortTermBudgetMax = if (shortTermBudgetMin != null) 
                roundPrice(shortTermBudgetMin + Random.nextInt(1000, 3000).toDouble(), 500.0) else null
            
            // Предпочтения по типу недвижимости
            val prefPropertyTypes = when {
                rentalType == "посуточная" -> 
                    listOf("Апартаменты", "Квартира").shuffled().take(Random.nextInt(1, 3))
                rentalType == "длительная" && Random.nextBoolean() -> 
                    listOf("Квартира", "Дом", "Таунхаус").shuffled().take(Random.nextInt(1, 3))
                else ->
                    propertyTypes.shuffled().take(Random.nextInt(1, minOf(3, propertyTypes.size)))
            }
            
            // Предпочтения по районам - максимально реалистичные
            val preferredDistricts = when (rentalType) {
                "посуточная" -> 
                    listOf("Балаклавский", "Ленинский").shuffled().take(Random.nextInt(1, 3)).joinToString(", ")
                "длительная" -> if (Random.nextBoolean()) {
                    // Для длительной аренды чаще всего несколько районов
                    districts.shuffled().take(Random.nextInt(1, 4)).joinToString(", ")
                } else {
                    // Иногда конкретный район
                    districts.random()
                }
                else -> districts.shuffled().take(Random.nextInt(1, 4)).joinToString(", ")
            }
            
            // Семейное положение
            val peopleCount = Random.nextInt(1, 6)
            val childrenCount = if (peopleCount > 1) {
                when {
                    peopleCount == 2 -> if (Random.nextInt(10) < 3) 1 else 0 // 30% пар с ребенком
                    peopleCount == 3 -> if (Random.nextInt(10) < 7) 1 else 0 // 70% троих - с одним ребенком
                    peopleCount == 4 -> if (Random.nextInt(10) < 8) 2 else 1 // 80% четверых - с двумя детьми
                    else -> Random.nextInt(1, peopleCount - 1) // Для больших групп
                }
            } else 0 // Если один человек - без детей
            
            // Предпочтения по комнатам в зависимости от количества людей и типов недвижимости
            val desiredRooms = when {
                prefPropertyTypes.contains("Студия") -> 0
                else -> when {
                    peopleCount <= 1 -> if (Random.nextInt(10) < 7) 1 else 2 // Одинокие обычно 1-комнатные
                    peopleCount == 2 -> if (Random.nextInt(10) < 6) 1 else 2 // Пары часто 1-2 комнатные
                    peopleCount == 3 -> if (Random.nextInt(10) < 7) 2 else 3 // Семьи из 3 человек - 2-3 комнаты
                    peopleCount == 4 -> if (Random.nextInt(10) < 8) 3 else 2 // Семьи из 4 человек - обычно 3 комнаты
                    else -> Random.nextInt(3, 5) // Большие семьи - 3+ комнат
                }
            }
            
            // Определяем желаемую площадь в зависимости от количества людей и комнат
            val desiredArea = when {
                desiredRooms == 0 -> roundToNearest(Random.nextDouble(25.0, 40.0), 5.0) // Студия
                desiredRooms == 1 -> roundToNearest(Random.nextDouble(30.0, 50.0), 5.0) // 1-комнатная
                desiredRooms == 2 -> roundToNearest(Random.nextDouble(45.0, 70.0), 5.0) // 2-комнатная
                desiredRooms == 3 -> roundToNearest(Random.nextDouble(65.0, 100.0), 5.0) // 3-комнатная
                else -> roundToNearest(Random.nextDouble(90.0, 150.0), 5.0) // 4+ комнат
            }
            
            // Наличие питомцев - логичное распределение
            val hasPets = when {
                childrenCount > 0 -> Random.nextInt(10) < 4 // 40% семей с детьми имеют питомцев
                peopleCount == 1 -> Random.nextInt(10) < 3 // 30% одиноких имеют питомцев
                else -> Random.nextInt(10) < 5 // 50% остальных
            }
            
            // Предпочтения по этажу - более логичные
            // Семьи с детьми часто хотят этажи повыше первого, но ниже верхнего
            val preferredFloorMin = when {
                childrenCount > 0 -> Random.nextInt(2, 5)
                else -> if (Random.nextBoolean()) Random.nextInt(1, 5) else null
            }
            
            val preferredFloorMax = when {
                preferredFloorMin != null -> Random.nextInt(
                    preferredFloorMin + 2, 
                    preferredFloorMin + 15
                )
                else -> null
            }
            
            // Определяем потребность в парковке
            val needsParking = when {
                rentalType == "посуточная" -> Random.nextInt(10) < 7 // 70% туристов нуждаются в парковке
                peopleCount >= 3 -> Random.nextInt(10) < 8 // 80% семей нуждаются в парковке
                else -> Random.nextInt(10) < 5 // 50% остальных
            }
            
            val client = Client(
                id = UUID.randomUUID().toString(),
                fullName = generateFullName(),
                phone = listOf(generatePhoneNumber()),
                rentalType = rentalType,
                desiredPropertyType = prefPropertyTypes.joinToString(", "),
                preferredDistrict = preferredDistricts,
                desiredArea = desiredArea,
                desiredRoomsCount = desiredRooms,
                additionalRequirements = if (Random.nextBoolean()) 
                    generateAdditionalRequirements(rentalType, peopleCount, childrenCount, hasPets) else null,
                comment = if (Random.nextBoolean()) "Обратился по рекомендации" else "",
                
                // Финансовые параметры
                longTermBudgetMin = longTermBudgetMin,
                longTermBudgetMax = longTermBudgetMax,
                shortTermBudgetMin = shortTermBudgetMin,
                shortTermBudgetMax = shortTermBudgetMax,
                
                // Семейное положение
                familyComposition = generateFamilyComposition(peopleCount, childrenCount),
                peopleCount = peopleCount,
                childrenCount = childrenCount,
                childrenAges = if (childrenCount > 0) 
                    List(childrenCount) { Random.nextInt(1, 18) }.sorted() else emptyList(),
                hasPets = hasPets,
                petTypes = if (hasPets) {
                    petTypes.shuffled().take(Random.nextInt(1, minOf(3, petTypes.size)))
                } else emptyList(),
                petCount = if (hasPets) Random.nextInt(1, 3) else null,
                
                // Дополнительные предпочтения - зависят от типа аренды и состава семьи
                preferredAmenities = if (amenities.isNotEmpty()) {
                    when (rentalType) {
                        "посуточная" -> {
                            val baseAmenities = listOf("Кондиционер", "Интернет", "Телевизор")
                            if (Random.nextBoolean()) baseAmenities + "Стиральная машина" else baseAmenities
                        }
                        else -> {
                            val baseAmenities = listOf("Кондиционер", "Интернет", "Холодильник", "Стиральная машина")
                            
                            // Дополнительные удобства для семей
                            if (childrenCount > 0) {
                                baseAmenities + listOf("Посудомоечная машина", "Микроволновая печь")
                            } else if (peopleCount > 2) {
                                baseAmenities + "Посудомоечная машина"
                            } else {
                                baseAmenities
                            }
                        }
                    }
                } else {
                    emptyList()
                },
                preferredViews = when {
                    rentalType == "посуточная" && preferredDistricts.contains("Балаклавский") -> 
                        listOf("Вид на море")
                    rentalType == "посуточная" && preferredDistricts.contains("Ленинский") -> 
                        if (Random.nextBoolean()) listOf("Вид на море") else emptyList()
                    else -> emptyList()
                },
                preferredRepairState = if (repairStates.isNotEmpty()) {
                    when {
                        longTermBudgetMax != null && longTermBudgetMax > 60000 -> 
                            listOf("Евроремонт", "Люкс").random()
                        else -> repairStates.filter { it != "Требует ремонта" }.random()
                    }
                } else "Стандартный",
                preferredFloorMin = preferredFloorMin,
                preferredFloorMax = preferredFloorMax,
                needsParking = needsParking,
                needsFurniture = rentalType == "посуточная" || Random.nextInt(10) < 8, // 80% долгосрочных хотят мебель
                
                // Метаданные
                createdAt = System.currentTimeMillis() - Random.nextLong(0L, 90L * 24L * 60L * 60L * 1000L),
                updatedAt = System.currentTimeMillis()
            )
            
            clients.add(client)
        }
        
        return clients
    }

    // Вспомогательные списки для генерации данных
    private val propertyTypes = listOf(
        "Квартира", "Студия", "Дом", "Таунхаус", "Апартаменты"
    )
    
    // Районы Севастополя
    private val districts = listOf(
        "Ленинский", "Гагаринский", "Нахимовский", "Балаклавский"
    )
    
    // Улицы Севастополя, сгруппированные по районам
    private val streetsByDistrict = mapOf(
        "Ленинский" to listOf(
            "проспект Нахимова", "улица Ленина", "улица Большая Морская", 
            "улица Генерала Петрова", "улица Очаковцев", "улица Одесская", 
            "улица Советская", "улица Луначарского", "улица Воронина"
        ),
        "Гагаринский" to listOf(
            "проспект Октябрьской Революции", "проспект Героев Сталинграда", 
            "улица Вакуленчука", "улица Репина", "улица Кесаева", 
            "улица Астана Кесаева", "улица Корчагина", "улица Шевченко"
        ),
        "Нахимовский" to listOf(
            "улица Богданова", "улица Нахимова", "улица Рабочая", 
            "улица Гоголя", "улица Мира", "улица Матросская", 
            "улица Леваневского", "улица Катерная"
        ),
        "Балаклавский" to listOf(
            "улица Новикова", "улица Рубцова", "улица Строительная", 
            "улица Невская", "улица Адмирала Владимирского", "улица Истомина",
            "улица Назукина", "Таврическая набережная"
        )
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
    
    // Типы удобств для объектов недвижимости
    private val amenities = listOf(
        "Кондиционер", "Посудомоечная машина", "Стиральная машина", "Телевизор", "Интернет",
        "Микроволновая печь", "Духовка", "Холодильник", "Чайник", "Утюг", "Фен", "Сушилка для белья"
    )
    
    // Типы видов из окна
    private val views = listOf(
        "Вид на море", "Вид на бухту", "Вид на город", "Вид на горы", "Вид на парк"
    )
    
    // Состояния ремонта
    private val repairStates = listOf(
        "Стандартный", "Евроремонт", "Люкс", "Требует ремонта", "Под чистовую отделку"
    )
    
    // Типы аренды
    private val rentalTypes = listOf(
        "длительная", "посуточная", "обе"
    )
    
    // Типы домашних животных
    private val petTypes = listOf(
        "кошка", "собака", "грызуны"
    )
    
    // Достопримечательности и объекты инфраструктуры Севастополя по районам
    private val nearbyObjectsByDistrict = mapOf(
        "Ленинский" to listOf(
            "Приморский бульвар", "Графская пристань", "Памятник затопленным кораблям", 
            "Площадь Нахимова", "Морской вокзал", "Матросский клуб", "Супермаркет", "Аптека"
        ),
        "Гагаринский" to listOf(
            "Парк Победы", "Омега-бухта", "ТРЦ Муссон", "Стрелецкая бухта", 
            "Парк имени Анны Ахматовой", "Супермаркет", "Детский сад", "Школа", "Поликлиника"
        ),
        "Нахимовский" to listOf(
            "Малахов курган", "Северная бухта", "Исторический бульвар", 
            "Корабельная сторона", "Супермаркет", "Автовокзал", "Школа", "Поликлиника"
        ),
        "Балаклавский" to listOf(
            "Балаклавская бухта", "Генуэзская крепость Чембало", "Набережная Назукина", 
            "Музей подводных лодок", "Супермаркет", "Яхт-клуб", "Пляж", "Кафе"
        )
    )
    
    // Функции генерации конкретных элементов
    private fun generateAddress(district: String): String {
        val streets = streetsByDistrict[district] ?: streetsByDistrict["Ленинский"]
        if (streets.isNullOrEmpty()) return "ул. Ленина, д. 1, кв. 1"
        
        val street = streets.random()
        val house = Random.nextInt(1, 100)
        val building = if (Random.nextBoolean()) ", корп. ${Random.nextInt(1, 5)}" else ""
        val apartment = Random.nextInt(1, 200)
        return "$street, д. $house$building, кв. $apartment"
    }
    
    private fun generatePhoneNumber(): String {
        val prefix = listOf("+7 (978) ", "+7 (989) ", "+7 (988) ", "+7 (918) ").random()
        val part1 = Random.nextInt(100, 999)
        val part2 = Random.nextInt(10, 99)
        val part3 = Random.nextInt(10, 99)
        return "$prefix$part1-$part2-$part3"
    }
    
    private fun generateFullName(): String {
        val firstName = firstNames.random()
        val lastName = lastNames.random()
        
        // Правильная генерация отчества на основе мужского имени
        val patronymicBase = firstNames.filter { 
            !it.endsWith("а") && !it.endsWith("я") 
        }.random()
        
        val patronymic = when {
            firstName.endsWith("а") || firstName.endsWith("я") -> {
                // Для женских имен
                when (patronymicBase) {
                    "Александр" -> "Александровна"
                    "Дмитрий" -> "Дмитриевна"
                    "Иван" -> "Ивановна"
                    "Михаил" -> "Михайловна"
                    "Сергей" -> "Сергеевна"
                    "Андрей" -> "Андреевна"
                    "Артем" -> "Артемовна"
                    "Николай" -> "Николаевна"
                    "Алексей" -> "Алексеевна"
                    "Владимир" -> "Владимировна"
                    "Игорь" -> "Игоревна"
                    "Максим" -> "Максимовна"
                    else -> patronymicBase + "овна"
                }
            }
            else -> {
                // Для мужских имен
                when (patronymicBase) {
                    "Александр" -> "Александрович"
                    "Дмитрий" -> "Дмитриевич"
                    "Иван" -> "Иванович"
                    "Михаил" -> "Михайлович"
                    "Сергей" -> "Сергеевич"
                    "Андрей" -> "Андреевич"
                    "Артем" -> "Артемович"
                    "Николай" -> "Николаевич"
                    "Алексей" -> "Алексеевич"
                    "Владимир" -> "Владимирович"
                    "Игорь" -> "Игоревич"
                    "Максим" -> "Максимович"
                    else -> patronymicBase + "ович"
                }
            }
        }
        
        // Для женских фамилий добавляем окончание "а"
        val finalLastName = if (firstName.endsWith("а") || firstName.endsWith("я")) {
            if (lastName.endsWith("ий") || lastName.endsWith("ый")) {
                lastName.substring(0, lastName.length - 2) + "ая"
            } else if (!lastName.endsWith("а") && !lastName.endsWith("я")) {
                lastName + "а"
            } else {
                lastName
            }
        } else {
            lastName
        }
        
        return "$finalLastName $firstName $patronymic"
    }
    
    private fun generateDescription(propertyType: String, district: String, area: Double, rooms: Int): String {
        val intro = when (propertyType) {
            "Студия" -> "Уютная светлая студия"
            "Квартира" -> {
                val roomsText = when (rooms) {
                    1 -> "Однокомнатная квартира"
                    2 -> "Двухкомнатная квартира"
                    3 -> "Трёхкомнатная квартира"
                    4 -> "Четырёхкомнатная квартира"
                    else -> "$rooms-комнатная квартира"
                }
                "$roomsText"
            }
            "Дом" -> "Просторный дом"
            "Таунхаус" -> "Современный таунхаус"
            "Апартаменты" -> "Комфортабельные апартаменты"
            else -> "Объект недвижимости"
        }
        
        val locationFeature = when (district) {
            "Ленинский" -> listOf(
                "в самом центре города", 
                "в историческом центре Севастополя", 
                "рядом с Приморским бульваром"
            ).random()
            "Гагаринский" -> listOf(
                "в современном районе", 
                "недалеко от парка Победы", 
                "рядом с Омега-бухтой"
            ).random()
            "Нахимовский" -> listOf(
                "в тихом районе", 
                "с удобной транспортной доступностью", 
                "рядом с Малаховым курганом"
            ).random()
            "Балаклавский" -> listOf(
                "с видом на Балаклавскую бухту", 
                "в живописном районе", 
                "недалеко от моря"
            ).random()
            else -> "в хорошем районе"
        }
        
        val areaFeature = "площадью $area кв.м"
        
        val additionalFeatures = listOf(
            "Качественный ремонт", 
            "Хорошая инфраструктура", 
            "Развитая транспортная сеть",
            "Благоустроенная территория",
            "Красивый вид из окна",
            "Тихие соседи",
            "Чистый подъезд",
            "Закрытая территория"
        ).shuffled().take(Random.nextInt(2, 4)).joinToString(". ")
        
        return "$intro $locationFeature, $areaFeature. $additionalFeatures."
    }
    
    private fun generateNearbyObjects(district: String): List<String> {
        val districtObjects = nearbyObjectsByDistrict[district] ?: emptyList()
        
        // Всегда добавляем базовые объекты инфраструктуры
        val baseObjects = listOf("Продуктовый магазин", "Остановка общественного транспорта")
        
        // Комбинируем и выбираем случайное подмножество
        val allObjects = (baseObjects + districtObjects).distinct()
        return if (allObjects.isNotEmpty()) {
            allObjects.shuffled().take(Random.nextInt(3, minOf(6, allObjects.size)))
        } else {
            emptyList()
        }
    }
    
    private fun generateFamilyComposition(peopleCount: Int, childrenCount: Int): String {
        val adultCount = peopleCount - childrenCount
        
        return when {
            adultCount == 1 && childrenCount == 0 -> "Один взрослый"
            adultCount == 1 && childrenCount == 1 -> "Один взрослый с ребенком"
            adultCount == 1 && childrenCount > 1 -> "Один взрослый с $childrenCount детьми"
            adultCount == 2 && childrenCount == 0 -> "Семейная пара без детей"
            adultCount == 2 && childrenCount == 1 -> "Семейная пара с ребенком"
            adultCount == 2 && childrenCount > 1 -> "Семейная пара с $childrenCount детьми"
            adultCount > 2 && childrenCount == 0 -> "$adultCount взрослых"
            adultCount > 2 && childrenCount > 0 -> "$adultCount взрослых и $childrenCount детей"
            else -> "$peopleCount человек"
        }
    }
    
    /**
     * Генерирует дополнительные требования клиента с учетом типа аренды и состава семьи
     * @param rentalType тип аренды
     * @param peopleCount количество людей
     * @param childrenCount количество детей
     * @param hasPets наличие домашних животных
     * @return строка с дополнительными требованиями
     */
    private fun generateAdditionalRequirements(
        rentalType: String, 
        peopleCount: Int = 1, 
        childrenCount: Int = 0,
        hasPets: Boolean = false
    ): String {
        val requirements = mutableListOf<String>()
        
        // Базовые требования по типу аренды
        val baseRequirements = when (rentalType) {
            "посуточная" -> listOf(
                "Близость к морю",
                "Хороший вид из окна",
                "Тихий район",
                "Наличие кондиционера",
                "Wi-Fi",
                "Близость к достопримечательностям"
            )
            else -> listOf(
                "Тихие соседи",
                "Чистый подъезд",
                "Развитая инфраструктура",
                "Удобная транспортная доступность",
                "Новый дом",
                "Хорошая звукоизоляция",
                "Утеплённый балкон"
            )
        }
        
        // Добавляем 1-2 базовых требования
        requirements.addAll(baseRequirements.shuffled().take(Random.nextInt(1, 3)))
        
        // Дополнительные требования в зависимости от количества людей
        if (peopleCount > 2) {
            val familyRequirements = listOf(
                "Просторная кухня",
                "Вместительный холодильник",
                "Достаточное количество шкафов"
            )
            if (Random.nextBoolean()) {
                requirements.add(familyRequirements.random())
            }
        }
        
        // Дополнительные требования для семей с детьми
        if (childrenCount > 0) {
            val childrenRequirements = listOf(
                "Рядом с детским садом/школой",
                "Безопасный двор",
                "Детская площадка во дворе",
                "Рядом с парком"
            )
            requirements.add(childrenRequirements.random())
        }
        
        // Дополнительные требования для людей с питомцами
        if (hasPets) {
            requirements.add("Возможность проживания с домашними животными")
        }
        
        return requirements.shuffled().take(minOf(requirements.size, 3)).joinToString(", ")
    }
    
    private fun getDistrictCoordinates(district: String): Pair<Double, Double>? {
        return when (district) {
            "Ленинский" -> Pair(44.6166, 33.5254) // Центр Севастополя
            "Гагаринский" -> Pair(44.5841, 33.4913) // Район Гагаринский
            "Нахимовский" -> Pair(44.6236, 33.5405) // Район Нахимовский
            "Балаклавский" -> Pair(44.5025, 33.6020) // Балаклава
            else -> null
        }
    }
    
    private fun roundToNearest(value: Double, step: Double): Double {
        if (value <= 0.0) return 0.0
        return (value / step).toInt() * step
    }
    
    /**
     * Округляет цену до "красивого" значения
     * @param price исходная цена
     * @param step шаг округления (по умолчанию 1000 рублей)
     * @return округленная цена
     */
    private fun roundPrice(price: Double, step: Double = 1000.0): Double {
        if (price <= 0.0) return 0.0
        return (price / step).toInt() * step
    }

    private fun generateSeasonalPrices(basePrice: Double): List<SeasonalPrice> {
        val seasonalPrices = mutableListOf<SeasonalPrice>()
        
        // Цены высокого сезона (июнь-август)
        val highSeasonMultiplier = Random.nextDouble(1.3, 1.8)
        val highSeasonPrice = roundPrice(basePrice * highSeasonMultiplier, 100.0)
        
        val highSeasonStart = LocalDate.of(LocalDate.now().year, Month.JUNE, 1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val highSeasonEnd = LocalDate.of(LocalDate.now().year, Month.AUGUST, 31)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        seasonalPrices.add(
            SeasonalPrice(
                startDate = highSeasonStart,
                endDate = highSeasonEnd,
                price = highSeasonPrice
            )
        )
        
        // Цены бархатного сезона (сентябрь-октябрь)
        val mildSeasonMultiplier = Random.nextDouble(1.1, 1.4)
        val mildSeasonPrice = roundPrice(basePrice * mildSeasonMultiplier, 100.0)
        
        val mildSeasonStart = LocalDate.of(LocalDate.now().year, Month.SEPTEMBER, 1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val mildSeasonEnd = LocalDate.of(LocalDate.now().year, Month.OCTOBER, 31)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        seasonalPrices.add(
            SeasonalPrice(
                startDate = mildSeasonStart,
                endDate = mildSeasonEnd,
                price = mildSeasonPrice
            )
        )
        
        // Цены низкого сезона (ноябрь-март) - скидка от базовой цены
        val lowSeasonMultiplier = Random.nextDouble(0.7, 0.9)
        val lowSeasonPrice = roundPrice(basePrice * lowSeasonMultiplier, 100.0)
        
        val lowSeasonStart = LocalDate.of(LocalDate.now().year, Month.NOVEMBER, 1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val lowSeasonEnd = LocalDate.of(LocalDate.now().year, Month.MARCH, 31)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        seasonalPrices.add(
            SeasonalPrice(
                startDate = lowSeasonStart,
                endDate = lowSeasonEnd,
                price = lowSeasonPrice
            )
        )
        
        return seasonalPrices
    }

    // Генерирует список удобств в зависимости от типа недвижимости
    private fun generateAmenities(propertyType: String, roomsCount: Int): List<String> {
        val baseAmenities = mutableListOf(
            "Холодильник", 
            "Стиральная машина", 
            "Телевизор", 
            "Интернет"
        )
        
        // Добавляем удобства в зависимости от типа недвижимости
        when (propertyType) {
            "Квартира", "Апартаменты" -> {
                if (roomsCount >= 2 && Random.nextBoolean()) baseAmenities.add("Посудомоечная машина")
                if (Random.nextBoolean()) baseAmenities.add("Кондиционер")
                if (Random.nextBoolean()) baseAmenities.add("Микроволновая печь")
            }
            "Дом", "Таунхаус" -> {
                baseAmenities.add("Кондиционер")
                baseAmenities.add("Посудомоечная машина")
                baseAmenities.add("Микроволновая печь")
                if (Random.nextBoolean()) baseAmenities.add("Духовка")
            }
            "Студия" -> {
                if (Random.nextBoolean()) baseAmenities.add("Кондиционер")
                if (Random.nextBoolean()) baseAmenities.add("Микроволновая печь")
            }
        }
        
        // Добавляем случайные удобства из общего списка
        val additionalAmenities = amenities.filter { !baseAmenities.contains(it) }
            .shuffled().take(Random.nextInt(0, 3))
        
        return (baseAmenities + additionalAmenities).shuffled()
    }

    // Типы отопления
    private val heatingTypes = listOf(
        "Центральное", "Автономное", "Электрическое", "Газовое", "Комбинированное"
    )
} 