package com.verdenroz.verdaxmarket.feature.watchlist.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.R
import kotlin.math.roundToInt

@Composable
internal fun WatchlistedQuote(
    quote: WatchlistQuote,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(quote.symbol) {
                detectTapGestures(
                    onTap = {
                        onClick()
                    },
                    onDoubleTap = {
                        onNavigateToQuote(quote.symbol)
                    }
                )
            }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                shape = RoundedCornerShape(25)
            )
    ) {
        IconButton(
            onClick = onMoreClick,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
        ) {
            Icon(
                imageVector = VxmIcons.More,
                contentDescription = stringResource(id = R.string.feature_watchlist_more),
            )
        }

        if (!quote.logo.isNullOrBlank()) {
            VxmAsyncImage(
                model = quote.logo!!,
                description = stringResource(id = R.string.feature_watchlist_logo_description),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.inverseSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quote.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.25.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        }
        VxmListItem(
            headlineContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = quote.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(.5f)
                            .padding(end = 8.dp)
                    )
                    if (quote.price != null && quote.change != null && quote.percentChange != null) {
                        Column(
                            modifier = Modifier.weight(.3f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = quote.price!!,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = quote.change!!,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (quote.change?.startsWith("-") == true) getNegativeTextColor() else getPositiveTextColor(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.End
                            )
                        }
                        Text(
                            text = quote.percentChange!!,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (quote.percentChange?.startsWith("-") == true) getNegativeTextColor() else getPositiveTextColor(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (quote.percentChange?.startsWith("-") == true) getNegativeBackgroundColor() else getPositiveBackgroundColor())
                                .padding(4.dp)
                                .weight(.25f)
                        )
                    }
                }
            },
        )
    }
}

@Composable
internal fun EditableWatchlistQuote(
    quote: WatchlistQuote,
    dragModifier: Modifier,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    onDelete: (WatchlistQuote) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val deleteButtonWidth = 72.dp
    val deleteButtonWidthPx = with(LocalDensity.current) { deleteButtonWidth.toPx() }

    val offsetXAnimated by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetXAnimated"
    )

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
    ) {

        // Delete button that gets revealed
        IconButton(
            onClick = { onDelete(quote) },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(deleteButtonWidth)
                .padding(12.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = VxmIcons.Delete,
                contentDescription = stringResource(id = R.string.feature_watchlist_delete)
            )
        }

        Row(
            modifier = Modifier
                .offset { IntOffset(offsetXAnimated.roundToInt(), 0) }
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = (offsetX + dragAmount).coerceIn(0f, deleteButtonWidthPx)
                            offsetX = newOffset
                        },
                        onDragEnd = {
                            // Snap to either fully closed or fully open
                            offsetX = if (offsetX > deleteButtonWidthPx / 2) {
                                deleteButtonWidthPx
                            } else {
                                0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(25)),
                contentAlignment = Alignment.Center
            ) {
                WatchlistedQuote(
                    quote = quote,
                    onClick = onClick,
                    onMoreClick = onMoreClick,
                    onNavigateToQuote = onNavigateToQuote
                )
            }

            IconButton(
                modifier = dragModifier,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { },
            ) {
                Icon(
                    imageVector = VxmIcons.DragHandle,
                    contentDescription = stringResource(id = R.string.feature_watchlist_reorder)
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewWatchlistedQuote() {
    VxmTheme {
        WatchlistedQuote(
            quote = WatchlistQuote(
                symbol = "AAPL",
                name = "Apple Inc.",
                price = "$145.86",
                change = "+0.86",
                percentChange = "+0.59%",
                logo = null,
                order = 0
            ),
            onClick = { },
            onMoreClick = { },
            onNavigateToQuote = { }
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewEditableWatchlistedQuote() {
    VxmTheme {
        EditableWatchlistQuote(
            quote = WatchlistQuote(
                symbol = "AAPL",
                name = "Apple Inc.",
                price = null,
                change = null,
                percentChange = null,
                logo = null,
                order = 0
            ),
            dragModifier = Modifier,
            onClick = { },
            onMoreClick = { },
            onDelete = { },
            onNavigateToQuote = { }
        )
    }
}