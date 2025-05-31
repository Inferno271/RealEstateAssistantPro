package com.realestateassistant.pro.data.repository

import com.realestateassistant.pro.data.local.dao.ClientDao
import com.realestateassistant.pro.data.local.entity.ClientEntity
import com.realestateassistant.pro.data.local.mapper.ClientMapper
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.repository.ClientRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepositoryImpl @Inject constructor(
    private val clientDao: ClientDao
) : ClientRepository {

    override suspend fun addClient(client: Client): Result<Client> {
        return try {
            val newId = client.id.ifEmpty { UUID.randomUUID().toString() }
            val currentTime = System.currentTimeMillis()
            val updatedClient = client.copy(
                id = newId,
                createdAt = currentTime,
                updatedAt = currentTime
            )
            val clientEntity = ClientMapper.mapToEntity(updatedClient)
            clientDao.insertClient(clientEntity)
            Result.success(updatedClient)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateClient(client: Client): Result<Unit> {
        return try {
            if (client.id.isEmpty()) {
                return Result.failure(Exception("ID клиента пустой."))
            }
            
            // Получаем текущего клиента для сохранения createdAt
            val existingClient = clientDao.getClient(client.id)
            if (existingClient != null) {
                // Создаем новую сущность с обновленными данными, сохраняя createdAt
                val updatedClient = client.copy(
                    createdAt = existingClient.createdAt,
                    updatedAt = System.currentTimeMillis()
                )
                val clientEntity = ClientMapper.mapToEntity(updatedClient)
                clientDao.updateClient(clientEntity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Клиент не найден."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteClient(clientId: String): Result<Unit> {
        return try {
            clientDao.deleteClient(clientId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClient(clientId: String): Result<Client> {
        return try {
            val clientEntity = clientDao.getClient(clientId)
            if (clientEntity != null) {
                Result.success(ClientMapper.mapToDomain(clientEntity))
            } else {
                Result.failure(Exception("Клиент не найден."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllClients(): Result<List<Client>> {
        return try {
            println("DEBUG: ClientRepositoryImpl.getAllClients - начало запроса")
            val clientEntities = clientDao.getAllClients().first()
            println("DEBUG: ClientRepositoryImpl.getAllClients - получено сущностей: ${clientEntities.size}")
            println("DEBUG: ClientRepositoryImpl.getAllClients - типы аренды: ${clientEntities.map { it.rentalType }}")
            
            val clients = clientEntities.map { 
                val client = ClientMapper.mapToDomain(it)
                println("DEBUG: ClientRepositoryImpl.getAllClients - маппинг: ${it.id}, тип: ${it.rentalType} -> ${client.rentalType}")
                client 
            }
            
            println("DEBUG: ClientRepositoryImpl.getAllClients - итоговое количество: ${clients.size}")
            Result.success(clients)
        } catch (e: Exception) {
            println("DEBUG: ClientRepositoryImpl.getAllClients - ошибка: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getClientsByRentalType(rentalType: String): Result<List<Client>> {
        return try {
            val clientEntities = clientDao.getClientsByRentalType(rentalType).first()
            val clients = clientEntities.map { ClientMapper.mapToDomain(it) }
            Result.success(clients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Метод для отладки - получает список всех клиентов напрямую (без Flow)
     */
    suspend fun checkClientsInDatabase() {
        try {
            println("DEBUG: ClientRepositoryImpl.checkClientsInDatabase - начало проверки")
            val clientEntities = clientDao.getAllClientsDirectly()
            println("DEBUG: ClientRepositoryImpl.checkClientsInDatabase - напрямую получено: ${clientEntities.size}")
            println("DEBUG: ClientRepositoryImpl.checkClientsInDatabase - типы аренды: ${clientEntities.map { it.rentalType }}")
            
            for (entity in clientEntities) {
                println("DEBUG: ClientRepository - клиент: id=${entity.id}, имя=${entity.fullName}, тип=${entity.rentalType}")
            }
        } catch (e: Exception) {
            println("DEBUG: ClientRepositoryImpl.checkClientsInDatabase - ошибка: ${e.message}")
            e.printStackTrace()
        }
    }
} 