package com.verdenroz.verdaxmarket.auth

/**
 * Represents the data of an authenticated user.
 * @param id The unique identifier of the user.
 * @param email The email address of the user.
 * @param displayName The display name of the user.
 * @param photoUrl The URL of the user's profile photo.
 * @param provider The provider of the authentication.
 */
data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val provider: AuthProviderType
)