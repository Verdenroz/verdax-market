package com.verdenroz.verdaxmarket.core.designsystem.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.designsystem.R
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText.*

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
        DataError.Input.INVALID_EMAIL -> StringResource(R.string.core_designsystem_invalid_email)
        DataError.Input.INVALID_PASSWORD -> StringResource(R.string.core_designsystem_invalid_password)
        DataError.Input.BLANK_EMAIL -> StringResource(R.string.core_designsystem_blank_email)
        DataError.Input.BLANK_PASSWORD -> StringResource(R.string.core_designsystem_blank_password)
        DataError.Input.CONFIRM_PASSWORD_MISMATCH -> StringResource(R.string.core_designsystem_confirm_password_mismatch)
        // Auth error mappings
        DataError.Auth.INVALID_CREDENTIALS -> StringResource(R.string.core_designsystem_invalid_credentials)
        DataError.Auth.USER_NOT_FOUND -> StringResource(R.string.core_designsystem_user_not_found)
        DataError.Auth.EMAIL_ALREADY_EXISTS -> StringResource(R.string.core_designsystem_email_exists)
        DataError.Auth.NO_SAVED_CREDENTIALS -> StringResource(R.string.core_designsystem_no_saved_credentials)
        DataError.Auth.GOOGLE_SIGN_IN_FAILED -> StringResource(R.string.core_designsystem_google_sign_in_failed)
        DataError.Auth.GITHUB_SIGN_IN_FAILED -> StringResource(R.string.core_designsystem_github_sign_in_failed)
        DataError.Auth.PASSWORD_RESET_FAILED -> StringResource(R.string.core_designsystem_password_reset_failed)
        DataError.Auth.SIGN_OUT_FAILED -> StringResource(R.string.core_designsystem_sign_out_failed)
        DataError.Auth.ACCOUNT_DELETE_FAILED -> StringResource(R.string.core_designsystem_account_delete_failed)
        DataError.Auth.GOOGLE_PLAY_SERVICES_UNAVAILABLE -> StringResource(R.string.core_designsystem_google_play_unavailable)
        DataError.Auth.AUTH_UNKNOWN_ERROR -> StringResource(R.string.core_designsystem_auth_unknown_error)
        // Network errors
        DataError.Network.NO_INTERNET -> StringResource(R.string.core_designsystem_no_internet)
        DataError.Network.TIMEOUT -> StringResource(R.string.core_designsystem_timeout)
        DataError.Network.BAD_REQUEST -> StringResource(R.string.core_designsystem_bad_request)
        DataError.Network.DENIED -> StringResource(R.string.core_designsystem_denied)
        DataError.Network.NOT_FOUND -> StringResource(R.string.core_designsystem_not_found)
        DataError.Network.THROTTLED -> StringResource(R.string.core_designsystem_throttled)
        DataError.Network.SERVER_DOWN -> StringResource(R.string.core_designsystem_server_down)
        DataError.Network.UNKNOWN -> StringResource(R.string.core_designsystem_unknown_error)
        // Search errors
        DataError.Search.SEARCH_FAILED -> StringResource(R.string.core_designsystem_search_failed)
        DataError.Search.FAILED_UPDATE_VIEWS -> StringResource(R.string.core_designsystem_failed_update_views)
    }
}

fun DataError.asUiText(): UiText {
    return this.asString()
}