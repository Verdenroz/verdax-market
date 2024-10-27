package com.verdenroz.core.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.verdenroz.core.datastore.UserSettingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    private val context: Context,
    private val userSettingsStore: UserSettingsStore
) {
    // Combine system permission state with user preference
    val areNotificationsEnabled: Flow<Boolean> = userSettingsStore.userSettings.map { userData ->
        userData.notificationsEnabled && checkNotificationPermission()
    }

    fun checkNotificationPermission(): Boolean {
        // Check system-level notification permission
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    suspend fun updateNotificationSettings(enabled: Boolean) {
        // Update user preference in DataStore
        userSettingsStore.setNotificationsEnabled(enabled)

        if (enabled && !checkNotificationPermission()) {
            // If user wants to enable notifications but doesn't have permission,
            // they need to be directed to system settings
            openNotificationSettings()
        }
    }

    private fun openNotificationSettings() {
        // Create intent to open system notification settings
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.content.Intent().apply {
                action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            android.content.Intent().apply {
                action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = android.net.Uri.fromParts("package", context.packageName, null)
            }
        }
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}