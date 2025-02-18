package com.realestateassistant.pro.data.repository

import com.google.firebase.database.DatabaseReference
import com.realestateassistant.pro.data.remote.FirebaseDatabaseManager
import com.realestateassistant.pro.domain.model.Property
import com.realestateassistant.pro.domain.repository.PropertyRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepositoryImpl @Inject constructor(
    private val firebaseDatabaseManager: FirebaseDatabaseManager
) : PropertyRepository {

    private val propertiesRef: DatabaseReference
        get() = firebaseDatabaseManager.getPropertiesReference()

    override suspend fun addProperty(property: Property): Result<Property> {
        return try {
            // Получаем новую ссылку с авто-генерацией ключа
            val newRef = propertiesRef.push()
            val newId = newRef.key
            if (newId == null) {
                return Result.failure(Exception("Не удалось сгенерировать новый ключ для объекта недвижимости."))
            }
            // Обновляем объект, устанавливая новый id
            val updatedProperty = property.copy(id = newId)
            newRef.setValue(updatedProperty).await()
            Result.success(updatedProperty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProperty(property: Property): Result<Unit> {
        return try {
            if (property.id.isEmpty()) {
                return Result.failure(Exception("Property id пустой."))
            }
            propertiesRef.child(property.id).setValue(property).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProperty(propertyId: String): Result<Unit> {
        return try {
            propertiesRef.child(propertyId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProperty(propertyId: String): Result<Property> {
        return try {
            val snapshot = propertiesRef.child(propertyId).get().await()
            val property = snapshot.getValue(Property::class.java)
            if (property != null) {
                Result.success(property)
            } else {
                Result.failure(Exception("Объект недвижимости не найден."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllProperties(): Result<List<Property>> {
        return try {
            val snapshot = propertiesRef.get().await()
            val propertyList = mutableListOf<Property>()
            snapshot.children.forEach { child ->
                val property = child.getValue(Property::class.java)
                if (property != null) {
                    propertyList.add(property)
                }
            }
            Result.success(propertyList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 