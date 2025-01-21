package com.verdenroz.verdaxmarket.feature.settings

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GithubAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.verdenroz.verdaxmarket.core.common.error.DataError
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
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val _authState = MutableStateFlow<UserAuthState>(UserAuthState.Loading)
    val authState: StateFlow<UserAuthState> = _authState.asStateFlow()

    private val errorChannel = Channel<DataError.Local>()
    val authErrors = errorChannel.receiveAsFlow()

    companion object {
        private const val WEB_CLIENT_ID =
            "164158213127-l8dqusc0au44n2ubcm2kfl2io67tn23c.apps.googleusercontent.com"
    }

    init {
        // Check if user is already signed in
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val authProvider = currentUser.providerData
                .firstOrNull { it.providerId != "firebase" }?.providerId
                ?: EmailAuthProvider.PROVIDER_ID
            _authState.value = UserAuthState.SignedIn(
                displayName = currentUser.displayName ?: "",
                email = currentUser.email ?: "",
                photoUrl = currentUser.photoUrl?.toString() ?: "",
                creationDate = formatCreationDate(currentUser.metadata?.creationTimestamp ?: 0),
                providerId = authProvider
            )
        } else {
            _authState.value = UserAuthState.SignedOut
        }
    }

    /**
     * Sign in with email and password
     * @param email user's email
     * @param password user's password
     * @return AuthCredential if sign-in is successful, null otherwise
     */
    suspend fun signInWithEmailPassword(email: String, password: String): AuthCredential? {
        try {
            _authState.value = UserAuthState.Loading

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user == null) throw Exception("Failed to sign in with email and password")
            handleSuccessfulSignIn(authResult.user!!)
            return authResult.credential
        } catch (e: Exception) {
            handleSignInError(e)
            return null
        }
    }

    /**
     * Create account with email and password
     * @param email user's email
     * @param password user's password
     */
    suspend fun createAccountWithEmailPassword(email: String, password: String) {
        try {
            _authState.value = UserAuthState.Loading

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if (authResult.user == null) throw Exception("Failed to create account with email and password")
            handleSuccessfulSignIn(authResult.user!!)
        } catch (e: Exception) {
            handleSignInError(e)
        }
    }

    /**
     * Sign in with Google
     * @param context application context
     * @return AuthCredential if sign-in is successful, null otherwise
     */
    suspend fun signInWithGoogle(context: Context): AuthCredential? {
        try {
            // Check Google Play Services availability
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)

            if (resultCode != ConnectionResult.SUCCESS) {
                handleSignInError(Exception("Google Play Services not available (code: $resultCode)"))
                return null
            }
            _authState.value = UserAuthState.Loading

            // Set up Google Sign-in
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .setNonce(generateNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Get Google credential
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            // Validate credential type
            if (credential !is CustomCredential ||
                credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                handleSignInError(Exception("Invalid credential type received"))
                return null
            }

            // Create Firebase credential
            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(credential.data)

            val firebaseCredential = GoogleAuthProvider.getCredential(
                googleIdTokenCredential.idToken, null
            )

            // Sign in to Firebase
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            if (authResult.user == null) {
                handleSignInError(Exception("Failed to sign in with Google"))
                return null
            }

            handleSuccessfulSignIn(authResult.user!!)
            return firebaseCredential

        } catch (e: Exception) {
            handleSignInError(e)
            return null
        }
    }

    /**
     * Sign in with GitHub
     * @param context application context
     * @return AuthCredential if sign-in is successful, null otherwise
     */
    suspend fun signInWithGitHub(context: Context): AuthCredential? {
        try {
            _authState.value = UserAuthState.Loading

            val provider = OAuthProvider.newBuilder("github.com").apply {
                scopes = listOf("user:email")
            }.build()

            val pendingAuthResult = firebaseAuth.pendingAuthResult?.await()
            if (pendingAuthResult != null) {
                if (pendingAuthResult.user == null) throw Exception("Failed to sign in with GitHub")
                handleSuccessfulSignIn(pendingAuthResult.user!!)
                return pendingAuthResult.credential
            }

            val authResult = firebaseAuth.startActivityForSignInWithProvider(
                context as android.app.Activity, provider
            ).await()

            if (authResult.user == null) throw Exception("Failed to sign in with GitHub")
            handleSuccessfulSignIn(authResult.user!!)
            return authResult.credential
        } catch (e: Exception) {
            handleSignInError(e)
            return null
        }
    }

    /**
     * Reset user's password by sending a password reset email
     * Note: This method does not require the user to be signed in and may be sent to any email address,
     * even if it is not associated with an account
     * @param email user's email
     */
    suspend fun resetPassword(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (_: Exception) {
            errorChannel.send(DataError.Local.PASSWORD_RESET_FAILED)
        }
    }

    /**
     * Sign out the current user
     */
    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _authState.value = UserAuthState.SignedOut

        } catch (_: Exception) {
            errorChannel.send(DataError.Local.SIGN_OUT_FAILED)
        }
    }

    /**
     * Delete the current user's account
     * @param context application context
     *
     * Note: This method requires the user to re-authenticate before deleting the account
     * if the last sign-in was not within 5 minutes
     */
    suspend fun deleteAccount(context: Context, password: String? = null): Boolean {
        try {
            val authProvider = (_authState.value as UserAuthState.SignedIn).providerId
            _authState.value = UserAuthState.Loading
            val user = firebaseAuth.currentUser
            if (user == null) {
                errorChannel.send(DataError.Local.ACCOUNT_DELETE_FAILED)
                return false
            }
            val lastSignIn = user.metadata?.lastSignInTimestamp!!
            val fiveMinutesInMillis = 5 * 60 * 1000
            val currentTime = System.currentTimeMillis()
            // Re-authenticate user if last sign-in was not within 5 minutes
            if (currentTime - lastSignIn > fiveMinutesInMillis) {

                when (authProvider) {
                    GoogleAuthProvider.PROVIDER_ID -> {
                        val credential = signInWithGoogle(context)
                        if (credential == null) throw Exception("Failed to obtain Google credential")
                        user.reauthenticate(credential).await()
                    }

                    GithubAuthProvider.PROVIDER_ID -> {
                        val credential = signInWithGitHub(context)
                        if (credential == null) throw Exception("Failed to obtain GitHub credential")
                        user.reauthenticate(credential).await()
                    }

                    EmailAuthProvider.PROVIDER_ID -> {
                        if (password == null) throw Exception("Password required for reauthentication")
                        val credential = EmailAuthProvider.getCredential(user.email!!, password)
                        user.reauthenticate(credential).await()
                    }
                }
            }

            user.delete().await()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _authState.value = UserAuthState.SignedOut
            return true
        } catch (_: Exception) {
            errorChannel.send(DataError.Local.ACCOUNT_DELETE_FAILED)
            _authState.value = UserAuthState.SignedOut
            return false
        }
    }

    private fun handleSuccessfulSignIn(user: FirebaseUser) {
        _authState.value = UserAuthState.SignedIn(
            displayName = user.displayName ?: "",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString() ?: "",
            creationDate = formatCreationDate(user.metadata?.creationTimestamp ?: 0),
            providerId = user.providerData
                .firstOrNull { it.providerId != "firebase" }?.providerId
                ?: EmailAuthProvider.PROVIDER_ID
        )
    }

    private suspend fun handleSignInError(e: Exception) {
        if (e is GetCredentialCancellationException) {
            _authState.value = UserAuthState.SignedOut
            return
        }

        val error = when (e) {
            is FirebaseAuthInvalidCredentialsException -> DataError.Local.INVALID_CREDENTIALS
            is FirebaseAuthInvalidUserException -> DataError.Local.USER_NOT_FOUND
            is FirebaseAuthUserCollisionException -> DataError.Local.EMAIL_ALREADY_EXISTS
            is NoCredentialException -> DataError.Local.NO_SAVED_CREDENTIALS
            is GoogleIdTokenParsingException -> DataError.Local.GOOGLE_SIGN_IN_FAILED
            else -> DataError.Local.AUTH_UNKNOWN_ERROR
        }

        errorChannel.send(error)
        _authState.value = UserAuthState.SignedOut
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
        val creationDate: String,
        val providerId: String
    ) : UserAuthState
}