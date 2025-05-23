package com.realestateassistant.pro.data.local.dao

import androidx.room.*
import com.realestateassistant.pro.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с клиентами
 */
@Dao
interface ClientDao {
    /**
     * Вставляет нового клиента
     * Если клиент с таким id уже существует, он будет заменен
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    /**
     * Вставляет список клиентов
     * Если клиент с таким id уже существует, он будет заменен
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    /**
     * Обновляет существующего клиента
     */
    @Update
    suspend fun updateClient(client: ClientEntity)

    /**
     * Удаляет клиента по его id
     */
    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClient(clientId: String)

    /**
     * Получает клиента по его id
     */
    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClient(clientId: String): ClientEntity?

    /**
     * Получает список всех клиентов
     */
    @Query("SELECT * FROM clients")
    fun getAllClients(): Flow<List<ClientEntity>>

    /**
     * Получает количество клиентов в базе данных
     */
    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getClientCount(): Int
    
    /**
     * Получает список клиентов по типу аренды
     */
    @Query("SELECT * FROM clients WHERE rentalType = :rentalType")
    fun getClientsByRentalType(rentalType: String): Flow<List<ClientEntity>>
    
    /**
     * Поиск клиентов по имени (частичное совпадение)
     */
    @Query("SELECT * FROM clients WHERE fullName LIKE '%' || :name || '%'")
    fun searchClientsByName(name: String): Flow<List<ClientEntity>>
    
    /**
     * Получает список клиентов, которые не синхронизированы
     */
    @Query("SELECT * FROM clients WHERE isSynced = 0")
    suspend fun getUnsyncedClients(): List<ClientEntity>

    /**
     * Обновляет статус синхронизации клиента
     */
    @Query("UPDATE clients SET isSynced = :isSynced WHERE id = :clientId")
    suspend fun updateSyncStatus(clientId: String, isSynced: Boolean)
} 