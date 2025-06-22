package com.realestateassistant.pro.domain.usecase.property

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.model.PropertyStatus
import com.realestateassistant.pro.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Модульный тест для проверки фильтрации объектов недвижимости по статусу и типу сделки
 */
@RunWith(AndroidJUnit4::class)
class PropertyFilteringTest {

    // Тестовые данные
    private lateinit var testProperties: List<Property>
    
    // Мокированный репозиторий
    private lateinit var propertyRepository: PropertyRepository
    
    // Тестируемый UseCase
    private lateinit var getPropertiesByStatusUseCase: GetPropertiesByStatusUseCase
    
    @Before
    fun setup() {
        // Создаём тестовые объекты недвижимости
        testProperties = createTestProperties()
        
        // Мокируем репозиторий
        propertyRepository = mock()
        
        // Создаём тестируемый UseCase
        getPropertiesByStatusUseCase = GetPropertiesByStatusUseCase(propertyRepository)
    }
    
    /**
     * Тест проверяет корректность фильтрации объектов по статусу
     */
    @Test
    fun testPropertyFilteringByStatus() = runBlocking {
        // Настраиваем мок репозитория, чтобы возвращал отфильтрованные по статусу AVAILABLE объекты
        whenever(propertyRepository.observePropertiesByStatus(PropertyStatus.AVAILABLE)).thenReturn(
            flow { emit(testProperties.filter { it.status == PropertyStatus.AVAILABLE }) }
        )
        
        // Запрашиваем объекты со статусом AVAILABLE
        val result = getPropertiesByStatusUseCase(PropertyStatus.AVAILABLE)
        
        // Собираем результат
        val availableProperties = mutableListOf<Property>()
        result.collect { properties ->
            availableProperties.addAll(properties)
        }
        
        // Проверяем, что получили только объекты со статусом AVAILABLE
        assertEquals(3, availableProperties.size)
        availableProperties.forEach { property ->
            assertEquals(PropertyStatus.AVAILABLE, property.status)
        }
    }
    
    /**
     * Тест проверяет корректность фильтрации объектов по типу сделки (долгосрочная аренда)
     */
    @Test
    fun testPropertyFilteringByLongTermRental() = runBlocking {
        // Создаём тестовый UseCase для фильтрации по типу сделки
        val filterProperties = { properties: List<Property> ->
            // Фильтруем объекты для долгосрочной аренды (у которых есть месячная цена)
            properties.filter { it.monthlyRent != null }
        }
        
        // Получаем объекты для долгосрочной аренды
        val longTermRentalProperties = filterProperties(testProperties)
        
        // Проверяем, что получили только объекты с установленной ежемесячной арендной платой
        assertEquals(4, longTermRentalProperties.size)
        longTermRentalProperties.forEach { property ->
            assert(property.monthlyRent != null)
        }
    }
    
    /**
     * Тест проверяет корректность фильтрации объектов по типу сделки (краткосрочная аренда)
     */
    @Test
    fun testPropertyFilteringByShortTermRental() = runBlocking {
        // Создаём тестовый UseCase для фильтрации по типу сделки
        val filterProperties = { properties: List<Property> ->
            // Фильтруем объекты для краткосрочной аренды (у которых есть суточная цена)
            properties.filter { it.dailyPrice != null }
        }
        
        // Получаем объекты для краткосрочной аренды
        val shortTermRentalProperties = filterProperties(testProperties)
        
        // Проверяем, что получили только объекты с установленной посуточной арендной платой
        assertEquals(3, shortTermRentalProperties.size)
        shortTermRentalProperties.forEach { property ->
            assert(property.dailyPrice != null)
        }
    }
    
    /**
     * Тест проверяет корректность комбинированной фильтрации по статусу и типу сделки
     */
    @Test
    fun testPropertyFilteringByStatusAndDealType() = runBlocking {
        // Настраиваем мок репозитория для возврата объектов со статусом AVAILABLE
        whenever(propertyRepository.observePropertiesByStatus(PropertyStatus.AVAILABLE)).thenReturn(
            flow { emit(testProperties.filter { it.status == PropertyStatus.AVAILABLE }) }
        )
        
        // Запрашиваем объекты со статусом AVAILABLE
        val availablePropertiesFlow = getPropertiesByStatusUseCase(PropertyStatus.AVAILABLE)
        
        // Собираем результат
        val availableProperties = mutableListOf<Property>()
        availablePropertiesFlow.collect { properties ->
            availableProperties.addAll(properties)
        }
        
        // Дополнительно фильтруем по типу сделки (долгосрочная аренда)
        val availableLongTermProperties = availableProperties.filter { it.monthlyRent != null }
        
        // Проверяем, что получили только доступные объекты для долгосрочной аренды
        assertEquals(2, availableLongTermProperties.size)
        availableLongTermProperties.forEach { property ->
            assertEquals(PropertyStatus.AVAILABLE, property.status)
            assert(property.monthlyRent != null)
        }
    }
    
    /**
     * Вспомогательный метод для создания тестовых объектов недвижимости
     */
    private fun createTestProperties(): List<Property> {
        return listOf(
            // Доступные объекты для долгосрочной аренды
            Property(
                id = "1",
                propertyType = "Квартира",
                roomsCount = 2,
                area = 65.0,
                monthlyRent = 35000.0,
                status = PropertyStatus.AVAILABLE
            ),
            Property(
                id = "2",
                propertyType = "Дом",
                roomsCount = 4,
                area = 120.0,
                monthlyRent = 75000.0,
                status = PropertyStatus.AVAILABLE
            ),
            
            // Доступный объект для краткосрочной аренды
            Property(
                id = "3",
                propertyType = "Квартира",
                roomsCount = 1,
                area = 45.0,
                dailyPrice = 3500.0,
                status = PropertyStatus.AVAILABLE
            ),
            
            // Зарезервированные объекты для долгосрочной аренды
            Property(
                id = "4",
                propertyType = "Апартаменты",
                roomsCount = 2,
                area = 70.0,
                monthlyRent = 45000.0,
                status = PropertyStatus.RESERVED
            ),
            
            // Зарезервированные объекты для краткосрочной аренды
            Property(
                id = "5",
                propertyType = "Квартира",
                roomsCount = 3,
                area = 85.0,
                dailyPrice = 5000.0,
                status = PropertyStatus.RESERVED
            ),
            
            // Занятые объекты для долгосрочной аренды
            Property(
                id = "6",
                propertyType = "Квартира",
                roomsCount = 2,
                area = 60.0,
                monthlyRent = 40000.0,
                status = PropertyStatus.OCCUPIED
            ),
            
            // Занятые объекты для краткосрочной аренды
            Property(
                id = "7",
                propertyType = "Апартаменты",
                roomsCount = 1,
                area = 50.0,
                dailyPrice = 4000.0,
                status = PropertyStatus.OCCUPIED
            )
        )
    }
} 