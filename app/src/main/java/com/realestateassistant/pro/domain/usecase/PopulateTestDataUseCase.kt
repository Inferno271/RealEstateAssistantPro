package com.realestateassistant.pro.domain.usecase

import com.realestateassistant.pro.core.test.TestDataGenerator
import com.realestateassistant.pro.domain.repository.PropertyRepository
import com.realestateassistant.pro.domain.repository.ClientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.util.Log

/**
 * UseCase для разового заполнения базы данных тестовыми данными
 */
class PopulateTestDataUseCase @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val clientRepository: ClientRepository
) {
    /**
     * Заполняет базу данных тестовыми объектами и клиентами
     * @param propertiesCount количество генерируемых объектов недвижимости
     * @param clientsCount количество генерируемых клиентов
     * @return результат операции с информацией о количестве созданных записей
     */
    suspend operator fun invoke(propertiesCount: Int, clientsCount: Int): Result<TestDataResult> = withContext(Dispatchers.IO) {
        try {
            // Проверяем валидность входных параметров
            val safePropertiesCount = propertiesCount.coerceIn(1, 50)
            val safeClientsCount = clientsCount.coerceIn(1, 30)
            
            // Генерируем и сохраняем объекты недвижимости
            val properties = try {
                TestDataGenerator.generateProperties(safePropertiesCount)
            } catch (e: Exception) {
                Log.e("PopulateTestDataUseCase", "Ошибка при генерации объектов: ${e.message}")
                e.printStackTrace()
                emptyList() // Возвращаем пустой список в случае ошибки
            }
            
            var propertiesAdded = 0
            properties.forEach { property ->
                try {
                    propertyRepository.addProperty(property)
                    propertiesAdded++
                } catch (e: Exception) {
                    Log.e("PopulateTestDataUseCase", "Ошибка при сохранении объекта: ${e.message}")
                }
            }
            
            // Генерируем и сохраняем клиентов
            val clients = try {
                TestDataGenerator.generateClients(safeClientsCount)
            } catch (e: Exception) {
                Log.e("PopulateTestDataUseCase", "Ошибка при генерации клиентов: ${e.message}")
                e.printStackTrace()
                emptyList() // Возвращаем пустой список в случае ошибки
            }
            
            var clientsAdded = 0
            clients.forEach { client ->
                try {
                    clientRepository.addClient(client)
                    clientsAdded++
                } catch (e: Exception) {
                    Log.e("PopulateTestDataUseCase", "Ошибка при сохранении клиента: ${e.message}")
                }
            }
            
            // Возвращаем результат
            Result.success(
                TestDataResult(
                    propertiesAdded = propertiesAdded,
                    clientsAdded = clientsAdded,
                    totalDataItems = propertiesAdded + clientsAdded
                )
            )
        } catch (e: Exception) {
            Log.e("PopulateTestDataUseCase", "Ошибка при заполнении тестовыми данными: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Класс для представления результата генерации тестовых данных
     */
    data class TestDataResult(
        val propertiesAdded: Int,
        val clientsAdded: Int,
        val totalDataItems: Int
    )
} 