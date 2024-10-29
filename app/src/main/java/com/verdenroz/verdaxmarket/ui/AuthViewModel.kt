package com.verdenroz.verdaxmarket.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.auth.AuthProvider
import com.verdenroz.verdaxmarket.auth.AuthProviderType
import com.verdenroz.verdaxmarket.auth.AuthResult
import com.verdenroz.verdaxmarket.auth.AuthState
import com.verdenroz.verdaxmarket.auth.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    @Named("google") private val googleAuthProvider: AuthProvider,
    @Named("github") private val githubAuthProvider: AuthProvider
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.SignedOut)
    val authState = _authState.asStateFlow()

    val user: StateFlow<AuthUser?> = authState.map {
        when (it) {
            is AuthState.SignedIn -> it.user
            else -> null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            // Combine auth states from both providers
            combine(
                googleAuthProvider.getAuthState(),
                githubAuthProvider.getAuthState()
            ) { googleState, githubState ->
                when {
                    googleState is AuthState.SignedIn -> googleState
                    githubState is AuthState.SignedIn -> githubState
                    else -> AuthState.SignedOut
                }
            }.collect {
                _authState.value = it
            }
        }
    }

    private fun handleAuthResult(result: AuthResult) {
        when (result) {
            is AuthResult.Success -> {
                _authState.value = AuthState.SignedIn(result.user)
            }

            is AuthResult.Error -> {
                _authState.value =
                    AuthState.Error(result.exception.localizedMessage ?: "An error occurred")
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            val result = googleAuthProvider.signIn()
            handleAuthResult(result)
        }
    }

    fun signInWithGithub() {
        viewModelScope.launch {
            val result = githubAuthProvider.signIn()
            handleAuthResult(result)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            when (authState.value) {
                is AuthState.SignedIn -> {
                    val provider = when ((authState.value as AuthState.SignedIn).user.provider) {
                        AuthProviderType.GOOGLE -> googleAuthProvider
                        AuthProviderType.GITHUB -> githubAuthProvider
                        else -> null
                    }
                    provider?.signOut()
                }

                else -> {}
            }
        }
    }
}