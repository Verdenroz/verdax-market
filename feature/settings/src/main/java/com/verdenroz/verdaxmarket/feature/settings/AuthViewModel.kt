package com.verdenroz.verdaxmarket.feature.settings

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID.randomUUID
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val credentialManager: CredentialManager,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<UserAuthState>(UserAuthState.Loading)
    val authState: StateFlow<UserAuthState> = _authState.asStateFlow()

    private val errorChannel = Channel<String>()
    val authErrors = errorChannel.receiveAsFlow()

    companion object {
        private const val WEB_CLIENT_ID =
            "164158213127-l8dqusc0au44n2ubcm2kfl2io67tn23c.apps.googleusercontent.com"
        private const val TAG = "AuthViewModel"
    }

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _authState.value = UserAuthState.SignedIn(
                displayName = currentUser.displayName ?: "",
                email = currentUser.email ?: "",
                photoUrl = currentUser.photoUrl?.toString() ?: "",
                creationDate = formatCreationDate(currentUser.metadata?.creationTimestamp ?: 0)
            )
        } else {
            _authState.value = UserAuthState.SignedOut
        }
    }

    // Email/Password Authentication
    suspend fun signInWithEmailPassword(email: String, password: String) {
        try {
            _authState.value = UserAuthState.Loading

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            handleSuccessfulSignIn(authResult.user)

        } catch (e: Exception) {
            handleSignInError(e)
        }
    }

    suspend fun createAccountWithEmailPassword(email: String, password: String) {
        try {
            _authState.value = UserAuthState.Loading

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            handleSuccessfulSignIn(authResult.user)

        } catch (e: Exception) {
            handleSignInError(e)
        }
    }

    // Google Sign In
    suspend fun signInWithGoogle(context: Context) {
        try {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)

            if (resultCode != ConnectionResult.SUCCESS) {
                throw Exception("Google Play Services not available (code: $resultCode)")
            }

            performGoogleSignIn(context)
        } catch (e: Exception) {
            handleSignInError(e)
        }
    }

    // GitHub Authentication
    suspend fun signInWithGitHub(context: Context) {
        try {
            _authState.value = UserAuthState.Loading

            val provider = OAuthProvider.newBuilder("github.com").apply {
                scopes = listOf("user:email")
            }.build()

            val pendingAuthResult = firebaseAuth.pendingAuthResult
            if (pendingAuthResult != null) {
                handleSuccessfulSignIn(pendingAuthResult.await().user)
                return
            }

            val authResult = firebaseAuth.startActivityForSignInWithProvider(
                context as android.app.Activity, provider
            ).await()

            handleSuccessfulSignIn(authResult.user)

        } catch (e: Exception) {
            handleSignInError(e)
        }
    }

    private suspend fun performGoogleSignIn(context: Context) {
        _authState.value = UserAuthState.Loading
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
            .setNonce(generateNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val result = credentialManager.getCredential(
            request = request,
            context = context
        )

        handleGoogleSignInResult(result)
    }

    private suspend fun handleGoogleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential

        if (credential !is CustomCredential || credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            throw Exception("Invalid credential type received")
        }

        val googleIdTokenCredential = GoogleIdTokenCredential
            .createFrom(credential.data)

        val firebaseCredential = GoogleAuthProvider.getCredential(
            googleIdTokenCredential.idToken, null
        )

        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        handleSuccessfulSignIn(authResult.user)
    }

    private fun handleSuccessfulSignIn(user: FirebaseUser?) {
        user ?: throw Exception("Firebase user is null after sign in")

        _authState.value = UserAuthState.SignedIn(
            displayName = user.displayName ?: "",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString() ?: "",
            creationDate = formatCreationDate(user.metadata?.creationTimestamp ?: 0)
        )

        Log.d(TAG, "Successfully signed in user: ${user.email}")
    }

    private suspend fun handleSignInError(e: Exception) {
        if (e is GetCredentialCancellationException) {
            // do nothing if user cancels sign-in
            _authState.value = UserAuthState.SignedOut
            return
        }

        val errorMessage = when (e) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid credentials. Please check your email and password."
            is FirebaseAuthInvalidUserException -> "No user found with this email."
            is FirebaseAuthUserCollisionException -> "An account already exists with this email."
            is NoCredentialException -> "No saved credentials found."
            is GoogleIdTokenParsingException -> "Failed to parse Google Sign-In token."
            else -> "Sign-in failed: ${e.message}"
        }

        Log.e(TAG, "Sign-in error: $errorMessage", e)
        errorChannel.send(errorMessage)
        _authState.value = UserAuthState.SignedOut
    }

    suspend fun resetPassword(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent to $email")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending password reset email: ${e.message}", e)
            errorChannel.send("Error sending password reset email: ${e.message}")
        }
    }

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _authState.value = UserAuthState.SignedOut
            Log.d(TAG, "Successfully signed out")

        } catch (e: Exception) {
            Log.e(TAG, "Error signing out: ${e.message}", e)
            errorChannel.send("Error signing out: ${e.message}")
        }
    }

    private fun generateNonce(): String {
        val ranNonce = randomUUID().toString()
        return MessageDigest.getInstance("SHA-256")
            .digest(ranNonce.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun formatCreationDate(creationDate: Long): String {
        val date = Date(creationDate)
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return format.format(date)
    }

}

sealed interface UserAuthState {
    data object Loading : UserAuthState
    data object SignedOut : UserAuthState
    data class SignedIn(
        val displayName: String,
        val email: String,
        val photoUrl: String,
        val creationDate: String
    ) : UserAuthState
}