package com.verdenroz.verdaxmarket.auth.github

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.verdenroz.verdaxmarket.auth.AuthProvider
import com.verdenroz.verdaxmarket.auth.AuthProviderType
import com.verdenroz.verdaxmarket.auth.AuthResult
import com.verdenroz.verdaxmarket.auth.AuthState
import com.verdenroz.verdaxmarket.auth.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseGithubAuthProvider @Inject constructor(
    private val auth: FirebaseAuth,
    private val context: Context
) : AuthProvider {
    override suspend fun signIn(): AuthResult = withContext(Dispatchers.IO) {
        try {
            val provider = OAuthProvider.newBuilder("github.com")
            val pendingResult = auth.pendingAuthResult

            val authResult = if (pendingResult != null) {
                pendingResult.await()
            } else {
                auth.startActivityForSignInWithProvider(
                    context as Activity,
                    provider.build()
                ).await()
            }

            AuthResult.Success(
                AuthUser(
                    id = authResult.user?.uid ?: "",
                    email = authResult.user?.email,
                    displayName = authResult.user?.displayName,
                    photoUrl = authResult.user?.photoUrl?.toString(),
                    provider = AuthProviderType.GITHUB
                )
            )
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getAuthState(): Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            auth.currentUser?.let { user ->
                trySend(
                    AuthState.SignedIn(
                        AuthUser(
                            id = user.uid,
                            email = user.email,
                            displayName = user.displayName,
                            photoUrl = user.photoUrl?.toString(),
                            provider = AuthProviderType.GITHUB
                        )
                    )
                )
            } ?: trySend(AuthState.SignedOut)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
}