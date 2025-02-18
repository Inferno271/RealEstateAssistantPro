package com.realestateassistant.pro.data.repository

import com.google.firebase.auth.FirebaseUser
import com.realestateassistant.pro.data.remote.FirebaseAuthManager
import com.realestateassistant.pro.domain.model.User
import com.realestateassistant.pro.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория аутентификации, использующая FirebaseAuthManager.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthManager: FirebaseAuthManager
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return firebaseAuthManager.signInWithEmail(email, password)
            .mapCatching { firebaseUser -> mapFirebaseUser(firebaseUser) }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<User> {
        return firebaseAuthManager.signUpWithEmail(email, password)
            .mapCatching { firebaseUser -> mapFirebaseUser(firebaseUser) }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return firebaseAuthManager.signInWithGoogle(idToken)
            .mapCatching { firebaseUser -> mapFirebaseUser(firebaseUser) }
    }

    override fun signOut() {
        firebaseAuthManager.signOut()
    }

    override fun getCurrentUser(): User? {
        return firebaseAuthManager.getCurrentUser()?.let { firebaseUser -> mapFirebaseUser(firebaseUser) }
    }

    /**
     * Преобразует объект FirebaseUser в доменную модель User
     */
    private fun mapFirebaseUser(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: "",
            phone = firebaseUser.phoneNumber ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: ""
        )
    }
} 