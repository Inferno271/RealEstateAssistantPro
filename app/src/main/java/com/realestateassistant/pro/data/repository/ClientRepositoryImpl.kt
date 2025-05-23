package com.realestateassistant.pro.data.repository

import com.realestateassistant.pro.data.local.dao.ClientDao
import com.realestateassistant.pro.data.local.entity.ClientEntity
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
            val updatedClient = client.copy(id = newId)
            val clientEntity = mapToEntity(updatedClient)
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
            val clientEntity = mapToEntity(client)
            clientDao.updateClient(clientEntity)
            Result.success(Unit)
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
                Result.success(mapFromEntity(clientEntity))
            } else {
                Result.failure(Exception("Клиент не найден."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllClients(): Result<List<Client>> {
        return try {
            val clientEntities = clientDao.getAllClients().first()
            val clients = clientEntities.map { mapFromEntity(it) }
            Result.success(clients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClientsByRentalType(rentalType: String): Result<List<Client>> {
        return try {
            val clientEntities = clientDao.getClientsByRentalType(rentalType).first()
            val clients = clientEntities.map { mapFromEntity(it) }
            Result.success(clients)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToEntity(client: Client): ClientEntity {
        return ClientEntity(
            id = client.id,
            fullName = client.fullName,
            phone = client.phone,
            email = client.email,
            rentalType = client.rentalType,
            comment = client.comment,
            preferredAddress = client.preferredAddress,
            longTermBudgetMin = client.longTermBudgetMin,
            longTermBudgetMax = client.longTermBudgetMax,
            desiredPropertyType = client.desiredPropertyType,
            desiredRoomsCount = client.desiredRoomsCount,
            peopleCount = client.peopleCount,
            childrenCount = client.childrenCount,
            petsInfo = client.petsInfo,
            desiredArea = client.desiredArea,
            additionalRequirements = client.additionalRequirements,
            legalPreferences = client.legalPreferences,
            shortTermCheckInDate = client.shortTermCheckInDate,
            shortTermCheckOutDate = client.shortTermCheckOutDate,
            shortTermGuests = client.shortTermGuests,
            dailyBudget = client.dailyBudget,
            preferredShortTermDistrict = client.preferredShortTermDistrict,
            checkInOutConditions = client.checkInOutConditions,
            additionalServices = client.additionalServices,
            additionalShortTermRequirements = client.additionalShortTermRequirements,
            createdAt = System.currentTimeMillis(),
            isSynced = false
        )
    }

    private fun mapFromEntity(entity: ClientEntity): Client {
        return Client(
            id = entity.id,
            fullName = entity.fullName,
            phone = entity.phone,
            email = entity.email,
            rentalType = entity.rentalType,
            comment = entity.comment,
            preferredAddress = entity.preferredAddress,
            longTermBudgetMin = entity.longTermBudgetMin,
            longTermBudgetMax = entity.longTermBudgetMax,
            desiredPropertyType = entity.desiredPropertyType,
            desiredRoomsCount = entity.desiredRoomsCount,
            peopleCount = entity.peopleCount,
            childrenCount = entity.childrenCount,
            petsInfo = entity.petsInfo,
            desiredArea = entity.desiredArea,
            additionalRequirements = entity.additionalRequirements,
            legalPreferences = entity.legalPreferences,
            shortTermCheckInDate = entity.shortTermCheckInDate,
            shortTermCheckOutDate = entity.shortTermCheckOutDate,
            shortTermGuests = entity.shortTermGuests,
            dailyBudget = entity.dailyBudget,
            preferredShortTermDistrict = entity.preferredShortTermDistrict,
            checkInOutConditions = entity.checkInOutConditions,
            additionalServices = entity.additionalServices,
            additionalShortTermRequirements = entity.additionalShortTermRequirements
        )
    }
} 