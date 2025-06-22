package com.realestateassistant.pro.domain.usecase.recommendation

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyStatus
import com.realestateassistant.pro.domain.repository.PropertyRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Модульный тест для проверки алгоритма соответствия объектов предпочтениям клиента
 */
@RunWith(AndroidJUnit4::class)
class ClientMatchingAlgorithmTest {
    
    // Тестовые данные
    private lateinit var testProperties: List<Property>
    private lateinit var testClients: List<Client>
    
    // Мокированный репозиторий
    private lateinit var propertyRepository: PropertyRepository
    
    // Тестируемый UseCase
    private lateinit var getPropertyRecommendationsUseCase: GetPropertyRecommendationsUseCase
    
    @Before
    fun setup() {
        // Создаём тестовые объекты недвижимости и клиентов
        testProperties = createTestProperties()
        testClients = createTestClients()
        
        // Мокируем репозиторий
        propertyRepository = mock()
        
        // Создаём тестируемый UseCase
        getPropertyRecommendationsUseCase = GetPropertyRecommendationsUseCase(propertyRepository)
    }
    
    /**
     * Тест проверяет корректность работы алгоритма подбора объектов для клиента,
     * ищущего долгосрочную аренду
     */
    @Test
    fun testClientMatchingForLongTermRental() = runBlocking {
        // Клиент, который ищет долгосрочную аренду
        val longTermClient = testClients[0]
        
        // Настраиваем мок репозитория
        whenever(propertyRepository.getAllProperties()).thenReturn(
            Result.success(testProperties)
        )
        
        // Вызываем тестируемый UseCase
        val recommendationsResult = getPropertyRecommendationsUseCase(longTermClient)
        
        // Проверяем успешность результата
        assertTrue(recommendationsResult.isSuccess)
        
        // Получаем рекомендации
        val recommendations = recommendationsResult.getOrNull()!!
        
        // Проверяем, что все рекомендованные объекты имеют месячную арендную плату
        recommendations.forEach { (property, _) ->
            assertTrue("Рекомендация должна иметь месячную арендную плату", property.monthlyRent != null)
        }
        
        // Проверяем, что рекомендации отсортированы по релевантности (от высшей к низшей)
        for (i in 0 until recommendations.size - 1) {
            assertTrue(
                "Рекомендации должны быть отсортированы по убыванию релевантности",
                recommendations[i].second >= recommendations[i + 1].second
            )
        }
        
        // Проверяем, что наиболее релевантная рекомендация соответствует предпочтениям клиента
        val topRecommendation = recommendations.first().first
        assertEquals("Квартира", topRecommendation.propertyType)
        assertEquals(2, topRecommendation.roomsCount)
        assertTrue(topRecommendation.area >= 55.0) // Не менее желаемой площади
    }
    
    /**
     * Тест проверяет корректность работы алгоритма подбора объектов для клиента,
     * ищущего краткосрочную аренду
     */
    @Test
    fun testClientMatchingForShortTermRental() = runBlocking {
        // Клиент, который ищет краткосрочную аренду
        val shortTermClient = testClients[1]
        
        // Настраиваем мок репозитория
        whenever(propertyRepository.getAllProperties()).thenReturn(
            Result.success(testProperties)
        )
        
        // Вызываем тестируемый UseCase
        val recommendationsResult = getPropertyRecommendationsUseCase(shortTermClient)
        
        // Проверяем успешность результата
        assertTrue(recommendationsResult.isSuccess)
        
        // Получаем рекомендации
        val recommendations = recommendationsResult.getOrNull()!!
        
        // Проверяем, что все рекомендованные объекты имеют посуточную арендную плату
        recommendations.forEach { (property, _) ->
            assertTrue("Рекомендация должна иметь посуточную арендную плату", property.dailyPrice != null)
        }
        
        // Проверяем, что рекомендации отсортированы по релевантности (от высшей к низшей)
        for (i in 0 until recommendations.size - 1) {
            assertTrue(
                "Рекомендации должны быть отсортированы по убыванию релевантности",
                recommendations[i].second >= recommendations[i + 1].second
            )
        }
        
        // Проверяем, что наиболее релевантная рекомендация соответствует предпочтениям клиента
        val topRecommendation = recommendations.first().first
        assertEquals("Квартира", topRecommendation.propertyType)
        assertTrue(topRecommendation.area >= 40.0) // Не менее желаемой площади
    }
    
    /**
     * Тест проверяет корректность обработки критичных критериев (например, разрешение на проживание с детьми)
     */
    @Test
    fun testClientMatchingWithCriticalCriteria() = runBlocking {
        // Клиент с детьми
        val clientWithChildren = testClients[2]
        
        // Настраиваем мок репозитория
        whenever(propertyRepository.getAllProperties()).thenReturn(
            Result.success(testProperties)
        )
        
        // Вызываем тестируемый UseCase
        val recommendationsResult = getPropertyRecommendationsUseCase(clientWithChildren)
        
        // Проверяем успешность результата
        assertTrue(recommendationsResult.isSuccess)
        
        // Получаем рекомендации
        val recommendations = recommendationsResult.getOrNull()!!
        
        // Проверяем, что все рекомендованные объекты разрешают проживание с детьми
        recommendations.forEach { (property, _) ->
            assertTrue("Рекомендация должна разрешать проживание с детьми", property.childrenAllowed)
        }
    }
    
    /**
     * Тест проверяет корректность расчёта оценки соответствия для разных объектов
     */
    @Test
    fun testMatchScoreCalculation() = runBlocking {
        // Клиент для тестирования
        val testClient = testClients[0]
        
        // Настраиваем мок репозитория
        whenever(propertyRepository.getAllProperties()).thenReturn(
            Result.success(testProperties)
        )
        
        // Вызываем тестируемый UseCase
        val recommendationsResult = getPropertyRecommendationsUseCase(testClient)
        
        // Проверяем успешность результата
        assertTrue(recommendationsResult.isSuccess)
        
        // Получаем рекомендации
        val recommendations = recommendationsResult.getOrNull()!!
        
        // Проверяем, что все оценки соответствия находятся в диапазоне [0, 1]
        recommendations.forEach { (_, score) ->
            assertTrue("Оценка соответствия должна быть в диапазоне [0, 1]", score in 0f..1f)
        }
    }
    
    /**
     * Вспомогательный метод для создания тестовых объектов недвижимости
     */
    private fun createTestProperties(): List<Property> {
        return listOf(
            // Квартиры для долгосрочной аренды
            Property(
                id = "1",
                propertyType = "Квартира",
                roomsCount = 2,
                area = 65.0,
                monthlyRent = 35000.0,
                district = "Центральный",
                repairState = "Хороший",
                bathroomsCount = 1,
                bathroomType = "Совмещенный",
                floor = 4,
                totalFloors = 9,
                hasParking = true,
                parkingType = "Наземная",
                hasAppliances = true,
                noFurniture = false,
                childrenAllowed = true,
                petsAllowed = true,
                status = PropertyStatus.AVAILABLE
            ),
            Property(
                id = "2",
                propertyType = "Квартира",
                roomsCount = 1,
                area = 45.0,
                monthlyRent = 28000.0,
                district = "Адмиралтейский",
                repairState = "Стандартный",
                bathroomsCount = 1,
                bathroomType = "Совмещенный",
                floor = 2,
                totalFloors = 5,
                hasParking = false,
                hasAppliances = true,
                noFurniture = false,
                childrenAllowed = true,
                petsAllowed = false,
                status = PropertyStatus.AVAILABLE
            ),
            
            // Дома для долгосрочной аренды
            Property(
                id = "3",
                propertyType = "Дом",
                roomsCount = 4,
                area = 120.0,
                monthlyRent = 85000.0,
                district = "Пушкинский",
                repairState = "Отличный",
                bathroomsCount = 2,
                bathroomType = "Раздельный",
                hasParking = true,
                parkingType = "Гараж",
                hasGarage = true,
                garageSpaces = 2,
                hasAppliances = true,
                noFurniture = false,
                childrenAllowed = true,
                petsAllowed = true,
                status = PropertyStatus.AVAILABLE
            ),
            
            // Квартиры для краткосрочной аренды
            Property(
                id = "4",
                propertyType = "Квартира",
                roomsCount = 1,
                area = 40.0,
                dailyPrice = 3000.0,
                district = "Центральный",
                repairState = "Хороший",
                bathroomsCount = 1,
                bathroomType = "Совмещенный",
                floor = 3,
                totalFloors = 5,
                hasParking = false,
                hasAppliances = true,
                noFurniture = false,
                childrenAllowed = false,
                petsAllowed = false,
                status = PropertyStatus.AVAILABLE
            ),
            Property(
                id = "5",
                propertyType = "Квартира",
                roomsCount = 2,
                area = 55.0,
                dailyPrice = 4500.0,
                district = "Адмиралтейский",
                repairState = "Отличный",
                bathroomsCount = 1,
                bathroomType = "Совмещенный",
                floor = 4,
                totalFloors = 7,
                hasParking = true,
                parkingType = "Подземная",
                hasAppliances = true,
                noFurniture = false,
                childrenAllowed = true,
                petsAllowed = true,
                status = PropertyStatus.AVAILABLE
            ),
            
            // Апартаменты для краткосрочной аренды
            Property(
                id = "6",
                propertyType = "Апартаменты",
                roomsCount = 2,
                area = 60.0,
                dailyPrice = 5000.0,
                district = "Петроградский",
                repairState = "Отличный",
                bathroomsCount = 1,
                bathroomType = "Совмещенный",
                floor = 10,
                totalFloors = 15,
                hasParking = true,
                parkingType = "Подземная",
                hasAppliances = true,
                noFurniture = false,
                childrenAllowed = true,
                petsAllowed = false,
                status = PropertyStatus.AVAILABLE
            )
        )
    }
    
    /**
     * Вспомогательный метод для создания тестовых клиентов
     */
    private fun createTestClients(): List<Client> {
        return listOf(
            // Клиент для долгосрочной аренды
            Client(
                id = "1",
                fullName = "Иванов Иван Иванович",
                phone = listOf("+7 (999) 123-45-67"),
                rentalType = "длительная",
                familyComposition = "Одиночка",
                peopleCount = 1,
                desiredPropertyType = "Квартира",
                desiredRoomsCount = 2,
                desiredArea = 55.0,
                longTermBudgetMin = 30000.0,
                longTermBudgetMax = 40000.0,
                preferredDistrict = "Центральный",
                preferredFloorMin = 3,
                preferredBathroomType = "Совмещенный",
                needsFurniture = true,
                needsAppliances = true,
                needsParking = false,
                hasPets = false,
                childrenCount = 0
            ),
            
            // Клиент для краткосрочной аренды
            Client(
                id = "2",
                fullName = "Петров Петр Петрович",
                phone = listOf("+7 (999) 987-65-43"),
                rentalType = "посуточная",
                familyComposition = "Пара без детей",
                peopleCount = 2,
                desiredPropertyType = "Квартира",
                desiredArea = 40.0,
                shortTermBudgetMin = 3000.0,
                shortTermBudgetMax = 5000.0,
                preferredShortTermDistrict = "Центральный",
                shortTermCheckInDate = System.currentTimeMillis(),
                shortTermCheckOutDate = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L, // +3 дня
                needsFurniture = true,
                needsAppliances = true,
                hasPets = false,
                childrenCount = 0
            ),
            
            // Клиент с детьми
            Client(
                id = "3",
                fullName = "Сидорова Анна Сергеевна",
                phone = listOf("+7 (999) 111-22-33"),
                rentalType = "длительная",
                familyComposition = "Семья с детьми",
                peopleCount = 4,
                childrenCount = 2,
                childrenAges = listOf(5, 7),
                desiredPropertyType = "Квартира",
                desiredRoomsCount = 3,
                desiredArea = 70.0,
                longTermBudgetMin = 40000.0,
                longTermBudgetMax = 60000.0,
                preferredDistrict = "Адмиралтейский",
                needsFurniture = true,
                needsAppliances = true,
                hasPets = false
            )
        )
    }
} 