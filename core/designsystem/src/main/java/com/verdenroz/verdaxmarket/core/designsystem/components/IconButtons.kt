package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.verdenroz.verdaxmarket.core.designsystem.R
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket icon button for adding to watchlist
 * @param onClick add to watchlist action
 */
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
            VxmIcons.Add,
            contentDescription = stringResource(id = R.string.core_designsystem_add)
        )
    }
}

/**
 * VerdaxMarket icon button for deleting from watchlist
 * @param onClick delete from watchlist action
 */
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
            VxmIcons.Remove,
            contentDescription = stringResource(id = R.string.core_designsystem_delete)
        )
    }
}

@ThemePreviews
@Composable
private fun VxmIconButtonPreview() {
    VxmTheme {
        Row {
            VxmAddIconButton(onClick = {})
            VxmDeleteIconButton(onClick = {})
        }
    }
}