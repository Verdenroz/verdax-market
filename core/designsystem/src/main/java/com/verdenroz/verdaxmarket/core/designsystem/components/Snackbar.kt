package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket wrapper for [SnackbarHost]
 * @param hostState [SnackbarHostState] to show snackbar
 */
@Composable
fun VxmSnackbarHost(
    modifier: Modifier = Modifier,
    hostState: SnackbarHostState,
) {
    SnackbarHost(
        hostState = hostState,
        snackbar = { data -> VxmSnackbar(data = data) },
        modifier = modifier
    )
}

/**
 * VerdaxMarket wrapper for [Snackbar]
 * @param data [SnackbarData] to show
 */
@Composable
fun VxmSnackbar(
    data: SnackbarData
) {
    Snackbar(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier.padding(vertical = 32.dp)
    ) {
        Row(
            horizontalArrangement = if (data.visuals.actionLabel != null) Arrangement.SpaceEvenly else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.visuals.message,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(.6f)
            )

            data.visuals.actionLabel?.let { actionLabel ->
                TextButton(
                    onClick = { data.performAction() },
                    modifier = Modifier.weight(.4f),
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SnackbarPreview() {
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        hostState.showSnackbar("This is a snackbar with an action", "Action")
    }
    VxmTheme {
        Surface {
            VxmSnackbarHost(
                hostState = hostState,
            )
        }
    }
}