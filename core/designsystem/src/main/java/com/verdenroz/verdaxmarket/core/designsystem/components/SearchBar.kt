package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.R
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket search bar that always displays its content as it arrives.
 * @param query The current query in the search bar.
 * @param onQueryChange Callback that is called when the query changes.
 * @param onSearch Callback that is called when the search button is clicked or IME action Search.
 * @param trailingIcon The trailing icon to be displayed in the search bar.
 * @param content The content to be displayed below the search bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VxmSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    expand: Boolean? = null,
    onExpandChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    trailingIcon: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    var textFieldFocusRequester = remember { FocusRequester() }
    var textFieldFocus = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading icon (search/keyboard icons)
                when (expand) {
                    null -> IconButton(onClick = { }) {
                        Icon(
                            VxmIcons.Search,
                            contentDescription = stringResource(id = R.string.core_designsystem_search),
                        )
                    }
                    true -> IconButton(onClick = {
                        onExpandChange(false)
                        textFieldFocus.clearFocus()
                    }) {
                        Icon(
                            VxmIcons.KeyboardDown,
                            contentDescription = stringResource(id = R.string.core_designsystem_cancel_search)
                        )
                    }
                    false -> IconButton(onClick = {
                        onExpandChange(true)
                        textFieldFocusRequester.requestFocus()
                    }) {
                        Icon(
                            VxmIcons.KeyboardUp,
                            contentDescription = stringResource(id = R.string.core_designsystem_cancel_search)
                        )
                    }
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(textFieldFocusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && expand == null) {
                                onExpandChange(true)
                            }
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch(query)
                            textFieldFocus.clearFocus()
                        }
                    ),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.core_designsystem_search),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Trailing icon (filter)
                trailingIcon()
            }
        }

        // Search content
        content()
    }
}

@ThemePreviews
@Composable
private fun PreviewSearchBar() {
    VxmTheme {
        VxmSearchBar(
            query = "",
            onQueryChange = {},
            expand = false,
            onExpandChange = {},
            onSearch = {},
            trailingIcon = {},
            content = {}
        )
    }
}