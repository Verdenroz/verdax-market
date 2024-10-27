package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.verdenroz.verdaxmarket.core.designsystem.R
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews

/**
 * VerdaxMarket wrapper around [SearchBar] to provide a search bar with custom styling. This search bar is always active.
 * @param query The current query in the search bar.
 * @param onQueryChange Callback that is called when the query changes.
 * @param onSearch Callback that is called when the search button is clicked or IME action Search.
 * @param trailingIcon The trailing icon to be displayed in the search bar.
 * @param content The content to be displayed below the search bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VxmSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    expand: Boolean? = null,
    onExpandChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    trailingIcon: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceDim,
            dividerColor = Color.Transparent,
            inputFieldColors = SearchBarDefaults.inputFieldColors(
                cursorColor = MaterialTheme.colorScheme.secondary,
                selectionColors = TextSelectionColors(
                    handleColor = MaterialTheme.colorScheme.secondary,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        ),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = true,
        onActiveChange = {},
        placeholder = {
            Text(
                stringResource(id = R.string.core_designsystem_search),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        },
        leadingIcon = {
            when (expand) {
                null -> Icon(
                    VxmIcons.Search,
                    contentDescription = stringResource(id = R.string.core_designsystem_search)
                )
                true -> IconButton(onClick = { onExpandChange(false)  }) {
                    Icon(
                        VxmIcons.KeyboardUp,
                        contentDescription = stringResource(id = R.string.core_designsystem_cancel_search)
                    )
                }
                false -> IconButton(onClick = { onExpandChange(true)  }) {
                    Icon(
                        VxmIcons.KeyboardDown,
                        contentDescription = stringResource(id = R.string.core_designsystem_cancel_search)
                    )
                }

            }
        },
        trailingIcon = trailingIcon,
        content = content
    )
}

@ThemePreviews
@Composable
private fun PreviewSearchBar() {
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