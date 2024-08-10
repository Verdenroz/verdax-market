package com.verdenroz.verdaxmarket.feature.watchlist.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.feature.watchlist.R

@Composable
internal fun ClearWatchlistFab(
    fabPadding: Dp,
    clearWatchlist: () -> Unit
) {
    var isFabExpanded by remember { mutableStateOf(false) }

    val fabOffset by animateDpAsState(
        targetValue = if (isFabExpanded) 100.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabOffset",
    )

    Box(
        modifier = Modifier
            .padding(bottom = fabPadding)
    ) {
        ExtendedFloatingActionButton(
            text = { Text(text = stringResource(id = R.string.feature_watchlist_clear)) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.feature_watchlist_clear)
                )
            },
            onClick = { isFabExpanded = !isFabExpanded },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.Center)
                .width(
                    animateDpAsState(
                        targetValue = if (!isFabExpanded) 175.dp else 0.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "fabSize"
                    ).value
                )
                .height(
                    animateDpAsState(
                        targetValue = if (!isFabExpanded) 64.dp else 0.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessVeryLow
                        ),
                        label = "fabSize"
                    ).value
                )
        )
        Box(
            modifier = Modifier
                .offset(y = -fabOffset)
                .padding(16.dp)
        ) {
            SmallFloatingActionButton(
                onClick = {
                    clearWatchlist()
                    isFabExpanded = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(
                    animateDpAsState(
                        targetValue = if (isFabExpanded) 48.dp else 0.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "fabSize"
                    ).value
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.feature_watchlist_clear),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Box(
            modifier = Modifier
                .offset(x = -fabOffset)
                .padding(16.dp)
        ) {
            SmallFloatingActionButton(
                onClick = {
                    isFabExpanded = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(
                    animateDpAsState(
                        targetValue = if (isFabExpanded) 48.dp else 0.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "fabSize"
                    ).value
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.feature_watchlist_dismiss),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}