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
        DataError.Local.DATABASE -> UiText.StringResource(R.string.core_designsystem_database)
        DataError.Local.UNKNOWN -> UiText.StringResource(R.string.core_designsystem_unknown_error)
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
