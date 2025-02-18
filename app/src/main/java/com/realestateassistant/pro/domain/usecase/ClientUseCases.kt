package com.realestateassistant.pro.domain.usecase

import com.realestateassistant.pro.domain.model.Client
import com.realestateassistant.pro.domain.repository.ClientRepository

class AddClient(private val repository: ClientRepository) {
    suspend operator fun invoke(client: Client): Result<Client> {
        return repository.addClient(client)
    }
}

class UpdateClient(private val repository: ClientRepository) {
    suspend operator fun invoke(client: Client): Result<Unit> {
        return repository.updateClient(client)
    }
}

class DeleteClient(private val repository: ClientRepository) {
    suspend operator fun invoke(clientId: String): Result<Unit> {
        return repository.deleteClient(clientId)
    }
}

class GetClient(private val repository: ClientRepository) {
    suspend operator fun invoke(clientId: String): Result<Client> {
        return repository.getClient(clientId)
    }
}

class GetAllClients(private val repository: ClientRepository) {
    suspend operator fun invoke(): Result<List<Client>> {
        return repository.getAllClients()
    }
}

class GetClientsByRentalType(private val repository: ClientRepository) {
    suspend operator fun invoke(rentalType: String): Result<List<Client>> {
        return repository.getClientsByRentalType(rentalType)
    }
}

// Собираем все use case в один контейнер для удобства
data class ClientUseCases(
    val addClient: AddClient,
    val updateClient: UpdateClient,
    val deleteClient: DeleteClient,
    val getClient: GetClient,
    val getAllClients: GetAllClients,
    val getClientsByRentalType: GetClientsByRentalType
) 