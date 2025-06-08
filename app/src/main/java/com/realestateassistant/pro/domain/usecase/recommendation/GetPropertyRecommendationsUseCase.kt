package com.realestateassistant.pro.domain.usecase.recommendation

import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.repository.PropertyRepository
import javax.inject.Inject

/**
 * Use case для получения рекомендаций объектов недвижимости для конкретного клиента.
 * 
 * Анализирует предпочтения клиента и сопоставляет их с доступными объектами недвижимости,
 * ранжируя результаты по степени соответствия.
 */
class GetPropertyRecommendationsUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository
) {
    /**
     * Получает список рекомендованных объектов недвижимости для клиента.
     * 
     * @param client Клиент, для которого нужно подобрать рекомендации
     * @return Результат с отсортированным по релевантности списком объектов недвижимости
     */
    suspend operator fun invoke(client: Client): Result<List<Pair<Property, Float>>> {
        return try {
            // Получаем все объекты недвижимости
            val propertiesResult = propertyRepository.getAllProperties()
            
            if (propertiesResult.isFailure) {
                return Result.failure(propertiesResult.exceptionOrNull() ?: Exception("Не удалось получить список объектов недвижимости"))
            }
            
            val properties = propertiesResult.getOrNull() ?: emptyList()
            
            // Фильтрация объектов по типу аренды
            val filteredProperties = when (client.rentalType.lowercase()) {
                "длительная" -> properties.filter { it.monthlyRent != null || it.winterMonthlyRent != null || it.summerMonthlyRent != null }
                "посуточная" -> properties.filter { it.dailyPrice != null }
                "оба_варианта" -> properties
                else -> properties
            }
            
            // Оценка соответствия каждого объекта предпочтениям клиента
            val scoredProperties = filteredProperties.map { property ->
                val matchScore = calculateMatchScore(client, property)
                property to matchScore
            }
            
            // Сортировка по убыванию оценки соответствия
            val sortedRecommendations = scoredProperties
                .filter { it.second > 0f } // Исключаем объекты с нулевым соответствием
                .sortedByDescending { it.second }
            
            Result.success(sortedRecommendations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Рассчитывает оценку соответствия объекта недвижимости предпочтениям клиента.
     * 
     * @param client Клиент с предпочтениями
     * @param property Объект недвижимости для оценки
     * @return Оценка соответствия от 0.0 до 1.0, где 1.0 - полное соответствие
     */
    private fun calculateMatchScore(client: Client, property: Property): Float {
        var totalScore = 0f
        var totalWeight = 0f
        
        // Проверка типа аренды
        if (client.rentalType.lowercase() == "длительная" && 
            (property.monthlyRent == null && property.winterMonthlyRent == null && property.summerMonthlyRent == null)) {
            return 0f
        }
        
        if (client.rentalType.lowercase() == "посуточная" && property.dailyPrice == null) {
            return 0f
        }
        
        // Основные критерии с весами
        
        // 1. Тип недвижимости (вес 10)
        if (!client.desiredPropertyType.isNullOrEmpty() && client.desiredPropertyType == property.propertyType) {
            totalScore += 10f
        }
        totalWeight += 10f
        
        // 2. Район (вес 9)
        if (!client.preferredDistrict.isNullOrEmpty() && client.preferredDistrict == property.district) {
            totalScore += 9f
        }
        totalWeight += 9f
        
        // 3. Бюджет (вес 8)
        val budgetMatch = when (client.rentalType.lowercase()) {
            "длительная" -> {
                val propertyPrice = property.monthlyRent ?: property.winterMonthlyRent ?: property.summerMonthlyRent ?: 0.0
                val minBudget = client.longTermBudgetMin ?: 0.0
                val maxBudget = client.longTermBudgetMax ?: Double.MAX_VALUE
                
                when {
                    propertyPrice == 0.0 -> 0f
                    propertyPrice in minBudget..maxBudget -> 8f
                    client.budgetFlexibility && propertyPrice <= maxBudget * (1 + (client.maxBudgetIncrease ?: 0.1)) -> 4f
                    else -> 0f
                }
            }
            "посуточная" -> {
                val propertyPrice = property.dailyPrice ?: 0.0
                val minBudget = client.shortTermBudgetMin ?: 0.0
                val maxBudget = client.shortTermBudgetMax ?: Double.MAX_VALUE
                
                when {
                    propertyPrice == 0.0 -> 0f
                    propertyPrice in minBudget..maxBudget -> 8f
                    client.budgetFlexibility && propertyPrice <= maxBudget * (1 + (client.maxBudgetIncrease ?: 0.1)) -> 4f
                    else -> 0f
                }
            }
            else -> 4f // Средняя оценка для "оба_варианта"
        }
        totalScore += budgetMatch
        totalWeight += 8f
        
        // 4. Количество комнат (вес 7)
        if (client.desiredRoomsCount != null) {
            when {
                client.desiredRoomsCount == property.roomsCount -> totalScore += 7f
                Math.abs(client.desiredRoomsCount - property.roomsCount) == 1 -> totalScore += 3.5f
            }
        }
        totalWeight += 7f
        
        // 5. Площадь (вес 6)
        if (client.desiredArea != null && client.desiredArea > 0) {
            val areaMatch = when {
                // Точное соответствие или в пределах 10%
                Math.abs(property.area - client.desiredArea) / client.desiredArea <= 0.1 -> 6f
                // В пределах 20%
                Math.abs(property.area - client.desiredArea) / client.desiredArea <= 0.2 -> 3f
                // Площадь больше желаемой на 20-50%
                property.area > client.desiredArea && 
                (property.area - client.desiredArea) / client.desiredArea <= 0.5 -> 2f
                else -> 0f
            }
            totalScore += areaMatch
        }
        totalWeight += 6f
        
        // 6. Этаж (вес 5)
        val floorMatch = if (client.preferredFloorMin != null && client.preferredFloorMax != null) {
            if (property.floor in client.preferredFloorMin..client.preferredFloorMax) 5f else 0f
        } else if (client.preferredFloorMin != null) {
            if (property.floor >= client.preferredFloorMin) 5f else 0f
        } else if (client.preferredFloorMax != null) {
            if (property.floor <= client.preferredFloorMax) 5f else 0f
        } else {
            2.5f // Нейтральная оценка, если предпочтения по этажу не указаны
        }
        totalScore += floorMatch
        totalWeight += 5f
        
        // 7. Наличие лифта (вес 4)
        if (client.needsElevator) {
            if (property.elevatorsCount != null && property.elevatorsCount > 0) {
                totalScore += 4f
            }
        } else {
            totalScore += 2f // Нейтральная оценка, если лифт не требуется
        }
        totalWeight += 4f
        
        // 8. Состояние ремонта (вес 5)
        if (!client.preferredRepairState.isNullOrEmpty() && client.preferredRepairState == property.repairState) {
            totalScore += 5f
        }
        totalWeight += 5f
        
        // 9. Наличие мебели (вес 5)
        if (client.needsFurniture && !property.noFurniture) {
            totalScore += 5f
        } else if (!client.needsFurniture && property.noFurniture) {
            totalScore += 5f
        }
        totalWeight += 5f
        
        // 10. Наличие бытовой техники (вес 4)
        if (client.needsAppliances && property.hasAppliances) {
            totalScore += 4f
        } else if (!client.needsAppliances) {
            totalScore += 2f // Нейтральная оценка, если техника не требуется
        }
        totalWeight += 4f
        
        // 11. Санузлы (вес 4)
        if (client.preferredBathroomsCount != null && property.bathroomsCount != null) {
            if (client.preferredBathroomsCount == property.bathroomsCount) {
                totalScore += 4f
            }
        }
        if (!client.preferredBathroomType.isNullOrEmpty() && client.preferredBathroomType == property.bathroomType) {
            totalScore += 2f
        }
        totalWeight += 4f
        
        // 12. Отопление (вес 3)
        if (!client.preferredHeatingType.isNullOrEmpty() && client.preferredHeatingType == property.heatingType) {
            totalScore += 3f
        }
        totalWeight += 3f
        
        // 13. Парковка (вес 4)
        if (client.needsParking && property.hasParking) {
            totalScore += 4f
            
            if (!client.preferredParkingType.isNullOrEmpty() && client.preferredParkingType == property.parkingType) {
                totalScore += 2f
            }
        } else if (!client.needsParking) {
            totalScore += 2f // Нейтральная оценка, если парковка не требуется
        }
        totalWeight += 4f
        
        // 14. Количество балконов (вес 3)
        if (client.preferredBalconiesCount != null) {
            if (client.preferredBalconiesCount == property.balconiesCount) {
                totalScore += 3f
            }
        }
        totalWeight += 3f
        
        // 15. Удобства (вес 4)
        val amenitiesMatch = if (client.preferredAmenities.isNotEmpty()) {
            val matchCount = client.preferredAmenities.count { amenity ->
                property.amenities.any { it.contains(amenity, ignoreCase = true) }
            }
            (matchCount.toFloat() / client.preferredAmenities.size) * 4f
        } else {
            2f // Нейтральная оценка, если удобства не указаны
        }
        totalScore += amenitiesMatch
        totalWeight += 4f
        
        // 16. Виды из окон (вес 3)
        val viewsMatch = if (client.preferredViews.isNotEmpty()) {
            val matchCount = client.preferredViews.count { view ->
                property.views.any { it.contains(view, ignoreCase = true) }
            }
            (matchCount.toFloat() / client.preferredViews.size) * 3f
        } else {
            1.5f // Нейтральная оценка, если виды не указаны
        }
        totalScore += viewsMatch
        totalWeight += 3f
        
        // 17. Объекты поблизости (вес 3)
        val nearbyObjectsMatch = if (client.preferredNearbyObjects.isNotEmpty()) {
            val matchCount = client.preferredNearbyObjects.count { obj ->
                property.nearbyObjects.any { it.contains(obj, ignoreCase = true) }
            }
            (matchCount.toFloat() / client.preferredNearbyObjects.size) * 3f
        } else {
            1.5f // Нейтральная оценка, если объекты поблизости не указаны
        }
        totalScore += nearbyObjectsMatch
        totalWeight += 3f
        
        // 18. Дополнительные критерии для домов
        if (property.propertyType.contains("дом", ignoreCase = true) || 
            property.propertyType.contains("коттедж", ignoreCase = true) || 
            property.propertyType.contains("таунхаус", ignoreCase = true)) {
            
            // Участок (вес 3)
            if (client.needsYard) {
                if (property.landArea > 0) {
                    totalScore += 3f
                    
                    if (client.preferredYardArea != null) {
                        val yardAreaMatch = when {
                            Math.abs(property.landArea - client.preferredYardArea) / client.preferredYardArea <= 0.2 -> 2f
                            Math.abs(property.landArea - client.preferredYardArea) / client.preferredYardArea <= 0.5 -> 1f
                            else -> 0f
                        }
                        totalScore += yardAreaMatch
                    }
                }
            }
            totalWeight += 3f
            
            // Гараж (вес 3)
            if (client.needsGarage && property.hasGarage) {
                totalScore += 3f
                
                if (client.preferredGarageSpaces != null && property.garageSpaces > 0) {
                    if (client.preferredGarageSpaces == property.garageSpaces) {
                        totalScore += 1f
                    }
                }
            } else if (!client.needsGarage) {
                totalScore += 1.5f // Нейтральная оценка, если гараж не требуется
            }
            totalWeight += 3f
            
            // Баня (вес 2)
            if (client.needsBathhouse && property.hasBathhouse) {
                totalScore += 2f
            } else if (!client.needsBathhouse) {
                totalScore += 1f // Нейтральная оценка, если баня не требуется
            }
            totalWeight += 2f
            
            // Бассейн (вес 2)
            if (client.needsPool && property.hasPool) {
                totalScore += 2f
            } else if (!client.needsPool) {
                totalScore += 1f // Нейтральная оценка, если бассейн не требуется
            }
            totalWeight += 2f
        }
        
        // 19. Разрешение на проживание с животными (вес 5)
        if (client.hasPets) {
            if (property.petsAllowed) {
                totalScore += 5f
            } else {
                return 0f // Критичное несоответствие - объект не подходит
            }
        } else {
            totalScore += 2.5f // Нейтральная оценка, если животных нет
        }
        totalWeight += 5f
        
        // 20. Разрешение на проживание с детьми (вес 5)
        if (client.childrenCount != null && client.childrenCount > 0) {
            if (property.childrenAllowed) {
                totalScore += 5f
            } else {
                return 0f // Критичное несоответствие - объект не подходит
            }
        } else {
            totalScore += 2.5f // Нейтральная оценка, если детей нет
        }
        totalWeight += 5f
        
        // 21. Разрешение на курение (вес 3)
        if (client.isSmokingClient) {
            if (property.smokingAllowed) {
                totalScore += 3f
            } else {
                return 0f // Критичное несоответствие - объект не подходит
            }
        } else {
            totalScore += 1.5f // Нейтральная оценка, если клиент не курит
        }
        totalWeight += 3f
        
        // 22. Юридические аспекты (вес 4)
        if (client.needsOfficialAgreement) {
            if (property.hasCompensationContract || 
                property.isSelfEmployed || 
                property.isPersonalIncomeTax || 
                property.isCompanyIncomeTax) {
                totalScore += 4f
            }
        } else {
            totalScore += 2f // Нейтральная оценка, если официальный договор не требуется
        }
        totalWeight += 4f
        
        // Нормализация оценки к диапазону [0, 1]
        return if (totalWeight > 0) totalScore / totalWeight else 0f
    }
} 