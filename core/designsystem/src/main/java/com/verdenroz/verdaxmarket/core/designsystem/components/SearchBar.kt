package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
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
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews

/**
 * VerdaxMarket wrapper around [SearchBar] to provide a search bar with custom styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VxmSearchBar(
    query: String,
    active: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
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
        active = active,
        onActiveChange = onActiveChange,
        placeholder = {
            Text(
                stringResource(id = R.string.core_designsystem_search),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        },
        leadingIcon = {
            if (!active) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.core_designsystem_search)
                )
            } else {
                IconButton(onClick = { onActiveChange(false) }) {
                    Icon(
                        Icons.Default.ArrowDropDown,
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
        active = false,
        onQueryChange = {},
        onSearch = {},
        onActiveChange = {},
        trailingIcon = {},
        content = {}
    )
}