package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.verdenroz.verdaxmarket.core.designsystem.R
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

@Composable
fun VxmAddIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = stringResource(id = R.string.core_designsystem_add)
        )
    }
}

@Composable
fun VxmDeleteIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            Icons.Default.Clear,
            contentDescription = stringResource(id = R.string.core_designsystem_delete)
        )
    }
}

@ThemePreviews
@Composable
fun VxmIconButtonPreview() {
    VxmTheme {
        Row {
            VxmAddIconButton(onClick = {})
            VxmDeleteIconButton(onClick = {})
        }
    }
}