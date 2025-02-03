package com.realestateassistant.pro.domain.model

/**
 * Модель данных пользователя.
 * Представляет информацию о пользователе в системе.
 *
 * @property id Уникальный идентификатор пользователя
 * @property email Email адрес пользователя
 * @property name Имя пользователя
 * @property phone Номер телефона пользователя
 * @property photoUrl URL фотографии профиля пользователя
 */
data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val photoUrl: String = ""
) 