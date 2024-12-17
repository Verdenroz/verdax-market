package com.verdenroz.verdaxmarket.feature.watchlist.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.R
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.pow

@Composable
internal fun WatchlistedQuote(
    quote: WatchlistQuote,
    onClick: () -> Unit,
    onNavigateToQuote: (String) -> Unit
) {
    // Keep track of tap gesture
    var isTapped by remember { mutableStateOf(false) }
    LaunchedEffect(isTapped) {
        delay(300)
        isTapped = false
    }
    VxmListItem(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Workaround for double tap gesture without the delay
                        if (isTapped) {
                            onNavigateToQuote(quote.symbol)
                        } else {
                            isTapped = true
                        }
                    }
                )
            }
            .clickable { onClick() },
        leadingContent = {
            if (quote.logo != null) {
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
        },
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

@Composable
internal fun EditableWatchlistQuote(
    quote: WatchlistQuote,
    dragModifier: Modifier,
    onDelete: (WatchlistQuote) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val maxDragDistance = 500f
    val dragProportion = abs(offsetX) / maxDragDistance
    val weight by animateFloatAsState(
        targetValue = (dragProportion.pow(2)).coerceIn(0.00001f, 1f),
        label = "weight"
    )
    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "elevation")
    val backgroundColor by animateColorAsState(
        targetValue = if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        label = "backgroundColor"
    )
    Row(
        modifier = modifier
            .offset(y = offsetY.dp)
            .shadow(elevation, RoundedCornerShape(25))
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        if (dragAmount > 0 || offsetX != 0f) {
                            offsetX += dragAmount
                            offsetX = offsetX.coerceIn(-maxDragDistance, maxDragDistance)
                        }
                    },
                    onDragEnd = {
                        if (abs(offsetX) > maxDragDistance * 0.75) {
                            onDelete(quote)
                        }
                        offsetX = 0f
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(weight)
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(25))
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = VxmIcons.DeleteSweep,
                contentDescription = stringResource(id = R.string.feature_watchlist_delete),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.alpha(1f - weight)
            )
        }
        Box(
            modifier = Modifier
                .weight((1f - weight).coerceAtLeast(0.01f))
                .padding(8.dp)
                .clip(RoundedCornerShape(25))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            WatchlistedQuote(
                quote = quote,
                onClick = { },
                onNavigateToQuote = { }
            )
        }
        IconButton(
            modifier = dragModifier,
            onClick = {},
        ) {
            Icon(VxmIcons.DragHandle, contentDescription = "Reorder")
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewWatchlistedQuote() {
    EditableWatchlistQuote(
        quote = WatchlistQuote(
            symbol = "AAPL",
            name = "Apple Inc.",
            price = "$145.86",
            change = "+0.86",
            percentChange = "+0.59%",
            logo = null,
            order = 0
        ),
        dragModifier = Modifier,
        onDelete = { },
    )
}