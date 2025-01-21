package com.verdenroz.verdaxmarket.core.designsystem.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.designsystem.R

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, *args)
        }
    }
}

fun DataError.asString(): UiText {
    return when (this) {
        // Input error mappings
        DataError.Local.INVALID_EMAIL -> UiText.StringResource(R.string.core_designsystem_invalid_email)
        DataError.Local.INVALID_PASSWORD -> UiText.StringResource(R.string.core_designsystem_invalid_password)
        DataError.Local.BLANK_EMAIL -> UiText.StringResource(R.string.core_designsystem_blank_email)
        DataError.Local.BLANK_PASSWORD -> UiText.StringResource(R.string.core_designsystem_blank_password)
        DataError.Local.CONFIRM_PASSWORD_MISMATCH -> UiText.StringResource(R.string.core_designsystem_confirm_password_mismatch)
        // Auth error mappings
        DataError.Local.INVALID_CREDENTIALS -> UiText.StringResource(R.string.core_designsystem_invalid_credentials)
        DataError.Local.USER_NOT_FOUND -> UiText.StringResource(R.string.core_designsystem_user_not_found)
        DataError.Local.EMAIL_ALREADY_EXISTS -> UiText.StringResource(R.string.core_designsystem_email_exists)
        DataError.Local.NO_SAVED_CREDENTIALS -> UiText.StringResource(R.string.core_designsystem_no_saved_credentials)
        DataError.Local.GOOGLE_SIGN_IN_FAILED -> UiText.StringResource(R.string.core_designsystem_google_sign_in_failed)
        DataError.Local.GITHUB_SIGN_IN_FAILED -> UiText.StringResource(R.string.core_designsystem_github_sign_in_failed)
        DataError.Local.PASSWORD_RESET_FAILED -> UiText.StringResource(R.string.core_designsystem_password_reset_failed)
        DataError.Local.SIGN_OUT_FAILED -> UiText.StringResource(R.string.core_designsystem_sign_out_failed)
        DataError.Local.ACCOUNT_DELETE_FAILED -> UiText.StringResource(R.string.core_designsystem_account_delete_failed)
        DataError.Local.GOOGLE_PLAY_SERVICES_UNAVAILABLE -> UiText.StringResource(R.string.core_designsystem_google_play_unavailable)
        DataError.Local.REAUTH_REQUIRED -> UiText.StringResource(R.string.core_designsystem_reauth_required)
        DataError.Local.AUTH_UNKNOWN_ERROR -> UiText.StringResource(R.string.core_designsystem_auth_unknown_error)
        // Network errors
        DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.core_designsystem_no_internet)
        DataError.Network.TIMEOUT -> UiText.StringResource(R.string.core_designsystem_timeout)
        DataError.Network.BAD_REQUEST -> UiText.StringResource(R.string.core_designsystem_bad_request)
        DataError.Network.DENIED -> UiText.StringResource(R.string.core_designsystem_denied)
        DataError.Network.NOT_FOUND -> UiText.StringResource(R.string.core_designsystem_not_found)
        DataError.Network.THROTTLED -> UiText.StringResource(R.string.core_designsystem_throttled)
        DataError.Network.SERVER_DOWN -> UiText.StringResource(R.string.core_designsystem_server_down)
        DataError.Network.UNKNOWN -> UiText.StringResource(R.string.core_designsystem_unknown_error)
    }
}

fun DataError.asUiText(): UiText {
    return this.asString()
}