package com.verdenroz.verdaxmarket.auth.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.verdenroz.verdaxmarket.auth.AuthProvider
import com.verdenroz.verdaxmarket.auth.R
import com.verdenroz.verdaxmarket.auth.github.FirebaseGithubAuthProvider
import com.verdenroz.verdaxmarket.auth.google.FirebaseGoogleAuthProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context
    ): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    @Named("google")
    fun provideGoogleAuthProvider(
        firebaseAuth: FirebaseAuth,
        @ApplicationContext context: Context,
        googleSignInClient: GoogleSignInClient
    ): AuthProvider = FirebaseGoogleAuthProvider(firebaseAuth, context, googleSignInClient)

    @Provides
    @Singleton
    @Named("github")
    fun provideGithubAuthProvider(
        firebaseAuth: FirebaseAuth,
        @ApplicationContext context: Context
    ): AuthProvider = FirebaseGithubAuthProvider(firebaseAuth, context)
}