package com.verdenroz.verdaxmarket.auth.google

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.verdenroz.verdaxmarket.auth.AuthProvider
import com.verdenroz.verdaxmarket.auth.AuthProviderType
import com.verdenroz.verdaxmarket.auth.AuthResult
import com.verdenroz.verdaxmarket.auth.AuthState
import com.verdenroz.verdaxmarket.auth.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseGoogleAuthProvider @Inject constructor(
    private val auth: FirebaseAuth,
    private val context: Context,
    private val googleSignInClient: GoogleSignInClient,
) : AuthProvider {
    override suspend fun signIn(): AuthResult = withContext(Dispatchers.IO) {
        try {
            val signInIntent = googleSignInClient.signInIntent
            val result: Task<GoogleSignInAccount> = suspendCancellableCoroutine { continuation ->
                val launcher = (context as ComponentActivity).activityResultRegistry
                    .register(
                        "google_sign_in",
                        ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            continuation.resume(task)
                        } else {
                            continuation.resume(
                                Tasks.forException(Exception("Sign in cancelled"))
                            )
                        }
                    }
                launcher.launch(signInIntent)
            }

            val googleAccount = result.await()
            val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()

            AuthResult.Success(
                AuthUser(
                    id = authResult.user?.uid ?: "",
                    email = authResult.user?.email,
                    displayName = authResult.user?.displayName,
                    photoUrl = authResult.user?.photoUrl?.toString(),
                    provider = AuthProviderType.GOOGLE
                )
            )
        } catch (e: Exception) {
            Log.d("FirebaseGoogleAuthProvider", "signIn: $e")
            AuthResult.Error(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().await()
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
                            provider = AuthProviderType.GOOGLE
                        )
                    )
                )
            } ?: trySend(AuthState.SignedOut)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
}