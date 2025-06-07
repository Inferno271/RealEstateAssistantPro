package com.realestateassistant.pro.data.local.dao

import androidx.room.*
import com.realestateassistant.pro.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO для работы с пользователями
 */
@Dao
interface UserDao {
    /**
     * Вставляет нового пользователя
     * Если пользователь с таким id уже существует, он будет заменен
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Вставляет список пользователей
     * Если пользователь с таким id уже существует, он будет заменен
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    /**
     * Обновляет существующего пользователя
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Удаляет пользователя по его id
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    /**
     * Получает пользователя по его id
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUser(userId: String): UserEntity?

    /**
     * Получает список всех пользователей
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    /**
     * Получает пользователя по email
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    /**
     * Получает список пользователей, которые не синхронизированы
     */
    @Query("SELECT * FROM users WHERE isSynced = 0")
    suspend fun getUnsyncedUsers(): List<UserEntity>

    /**
     * Обновляет статус синхронизации пользователя
     */
    @Query("UPDATE users SET isSynced = :isSynced WHERE id = :userId")
    suspend fun updateSyncStatus(userId: String, isSynced: Boolean)
    
    /**
     * Получает количество пользователей в базе данных
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
} 