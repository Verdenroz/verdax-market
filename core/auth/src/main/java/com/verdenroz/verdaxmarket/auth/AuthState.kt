package com.verdenroz.verdaxmarket.auth

/**
 * Represents the current state of the authentication.
 */
sealed class AuthState {
    data object SignedOut : AuthState()
    data class SignedIn(val user: AuthUser) : AuthState()
    data class Error(val message: String) : AuthState()
}