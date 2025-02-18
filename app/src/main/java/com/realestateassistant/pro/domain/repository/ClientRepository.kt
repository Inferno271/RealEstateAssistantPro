package com.realestateassistant.pro.domain.repository

import com.realestateassistant.pro.domain.model.Client

interface ClientRepository {
    /**
     * Добавляет нового клиента.
     * @param client Данные клиента для добавления
     * @return Result с добавленным клиентом (включая сгенерированный id) при успехе
     */
    suspend fun addClient(client: Client): Result<Client>

    /**
     * Обновляет данные существующего клиента.
     * @param client Обновленные данные клиента
     * @return Result с Unit при успехе
     */
    suspend fun updateClient(client: Client): Result<Unit>

    /**
     * Удаляет клиента по его id.
     * @param clientId Идентификатор клиента для удаления
     * @return Result с Unit при успехе
     */
    suspend fun deleteClient(clientId: String): Result<Unit>

    /**
     * Получает данные клиента по его id.
     * @param clientId Идентификатор клиента
     * @return Result с данными клиента при успехе
     */
    suspend fun getClient(clientId: String): Result<Client>

    /**
     * Получает список всех клиентов.
     * @return Result со списком всех клиентов при успехе
     */
    suspend fun getAllClients(): Result<List<Client>>

    /**
     * Получает список клиентов по типу аренды.
     * @param rentalType Тип аренды ("длительная" или "посуточная")
     * @return Result со списком клиентов, заинтересованных в указанном типе аренды
     */
    suspend fun getClientsByRentalType(rentalType: String): Result<List<Client>>
} 