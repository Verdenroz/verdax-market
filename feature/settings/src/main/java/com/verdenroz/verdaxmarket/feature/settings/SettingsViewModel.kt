package com.verdenroz.verdaxmarket.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.data.repository.UserDataRepository
import com.verdenroz.verdaxmarket.core.model.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userSetting
            .map { userData ->
                SettingsUiState.Success(
                    settings = UserEditableSettings(
                        themePreference = userData.themePreference,
                        notificationsEnabled = userData.notificationsEnabled,
                        hintsEnabled = userData.hintsEnabled,
                        showMarketHours = userData.showMarketHours,
                        isSynced = userData.isSynced,
                        enableAnonymousAnalytics = userData.enableAnonymousAnalytics,
                    ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5.seconds.inWholeMilliseconds),
                initialValue = SettingsUiState.Loading,
            )

    fun updateThemePreference(theme: ThemePreference) {
        viewModelScope.launch {
            userDataRepository.setThemePreference(theme)
        }
    }


    fun updateNotificationSetting(isNotificationEnabled: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNotificationsEnabled(isNotificationEnabled)
        }
    }

    fun updateHintsSetting(isHintsEnabled: Boolean) {
        viewModelScope.launch {
            userDataRepository.setHintsEnabled(isHintsEnabled)
        }
    }

    fun updateShowMarketHoursSetting(isShowMarketHours: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShowMarketHours(isShowMarketHours)
        }
    }

    fun updateSyncSetting(isSynced: Boolean) {
        viewModelScope.launch {
            userDataRepository.setSync(isSynced)
        }
    }

    fun updateEnableAnonymousAnalyticsSetting(isEnableAnonymousAnalytics: Boolean) {
        viewModelScope.launch {
            userDataRepository.setEnableAnonymousAnalytics(isEnableAnonymousAnalytics)
        }
    }

}

data class UserEditableSettings(
    val themePreference: ThemePreference,
    val notificationsEnabled: Boolean,
    val hintsEnabled: Boolean,
    val showMarketHours: Boolean,
    val isSynced: Boolean,
    val enableAnonymousAnalytics: Boolean,
)


sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}