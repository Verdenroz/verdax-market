package com.verdenroz.verdaxmarket.auth

/**
 * Represents the result of an authentication operation.
 */
sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val exception: Exception) : AuthResult()
}