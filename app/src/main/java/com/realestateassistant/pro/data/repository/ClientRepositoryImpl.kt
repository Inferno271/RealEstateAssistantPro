package com.realestateassistant.pro.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.realestateassistant.pro.data.remote.FirebaseDatabaseManager
import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.repository.ClientRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepositoryImpl @Inject constructor(
    private val firebaseDatabaseManager: FirebaseDatabaseManager
) : ClientRepository {

    private val clientsRef: DatabaseReference
        get() = firebaseDatabaseManager.getClientsReference()

    override suspend fun addClient(client: Client): Result<Client> {
        return try {
            val newRef = clientsRef.push()
            val newId = newRef.key
            if (newId == null) {
                return Result.failure(Exception("Не удалось сгенерировать новый ключ для клиента."))
            }
            val updatedClient = client.copy(id = newId)
            newRef.setValue(updatedClient).await()
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
            clientsRef.child(client.id).setValue(client).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteClient(clientId: String): Result<Unit> {
        return try {
            clientsRef.child(clientId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClient(clientId: String): Result<Client> {
        return try {
            val snapshot = clientsRef.child(clientId).get().await()
            val client = snapshot.getValue(Client::class.java)
            if (client != null) {
                Result.success(client)
            } else {
                Result.failure(Exception("Клиент не найден."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllClients(): Result<List<Client>> {
        return try {
            val snapshot = clientsRef.get().await()
            val clientList = mutableListOf<Client>()
            snapshot.children.forEach { child ->
                val client = child.getValue(Client::class.java)
                if (client != null) {
                    clientList.add(client)
                }
            }
            Result.success(clientList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getClientsByRentalType(rentalType: String): Result<List<Client>> {
        return try {
            val query: Query = clientsRef.orderByChild("rentalType").equalTo(rentalType)
            val snapshot = query.get().await()
            val clientList = mutableListOf<Client>()
            snapshot.children.forEach { child ->
                val client = child.getValue(Client::class.java)
                if (client != null) {
                    clientList.add(client)
                }
            }
            Result.success(clientList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 