package com.verdenroz.verdaxmarket.auth

import kotlinx.coroutines.flow.Flow

/**
 * For signing in and out of the application.
 */
interface AuthProvider {
    suspend fun signIn(): AuthResult
    suspend fun signOut()
    fun getAuthState(): Flow<AuthState>
}