package com.verdenroz.verdaxmarket.feature.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.RegionFilter
import com.verdenroz.verdaxmarket.core.model.ThemePreference
import com.verdenroz.verdaxmarket.feature.settings.R
import java.util.Locale

@Composable
fun ThemePreferenceItem(
    currentTheme: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit
) {
    PreferenceItem(
        currentPreference = currentTheme,
        onPreferenceChange = onThemeChange,
        title = stringResource(id = R.string.feature_settings_theme),
        description = stringResource(id = R.string.feature_settings_theme_description),
        icon = VxmIcons.Palette,
        titleCase = true
    )
}

@Composable
fun RegionPreferenceItem(
    currentRegion: RegionFilter,
    onRegionChange: (RegionFilter) -> Unit,
) {
    PreferenceItem(
        currentPreference = currentRegion,
        onPreferenceChange = onRegionChange,
        title = stringResource(id = R.string.feature_settings_region),
        description = stringResource(id = R.string.feature_settings_region_description),
        icon = VxmIcons.Map,
        titleCase = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal inline fun <reified T : Enum<T>> PreferenceItem(
    currentPreference: T,
    crossinline onPreferenceChange: (T) -> Unit,
    title: String,
    description: String,
    icon: ImageVector,
    titleCase: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val items = enumValues<T>()

    VxmListItem(
        headlineContent = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        trailingContent = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                TextField(
                    value = if (titleCase) currentPreference.name.lowercase().replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    } else currentPreference.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(.5f)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach { preference ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (titleCase) preference.name.lowercase()
                                        .replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                        }
                                    else preference.name
                                )
                            },
                            onClick = {
                                onPreferenceChange(preference)
                                expanded = false
                            },
                        )
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@ThemePreviews
@Composable
private fun PreviewThemeSetting() {
    VxmTheme {
        Column {
            ThemePreferenceItem(
                currentTheme = ThemePreference.SYSTEM,
                onThemeChange = {}
            )
            RegionPreferenceItem(
                currentRegion = RegionFilter.US,
                onRegionChange = {}
            )
        }
    }
}