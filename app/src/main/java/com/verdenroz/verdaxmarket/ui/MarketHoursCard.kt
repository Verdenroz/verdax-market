package com.verdenroz.verdaxmarket.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.verdenroz.verdaxmarket.R
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.MarketHours
import com.verdenroz.verdaxmarket.core.model.enums.MarketStatus
import com.verdenroz.verdaxmarket.core.model.enums.MarketStatusReason
import kotlinx.coroutines.android.awaitFrame
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MarketHoursCard(
    marketHours: MarketHours,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    var formattedTime by remember { mutableStateOf(formatNYTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            awaitFrame()
            formattedTime = formatNYTime()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(25),
            modifier = Modifier.zIndex(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                MarketStatusDot(
                    status = marketHours.status,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp)
                )

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = marketHours.status.toStringRes(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = marketHours.status.toColor()
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Card(
                modifier = Modifier.padding(horizontal = 4.dp),
                shape = RoundedCornerShape(25)
            ) {
                Text(
                    text = marketHours.reason.toStringRes(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun MarketStatusDot(
    status: MarketStatus,
    modifier: Modifier = Modifier
) {
    val dotColor = status.toColor()
    Canvas(modifier = modifier) {
        drawCircle(
            color = dotColor,
            radius = size.minDimension / 2
        )
    }
}

private fun formatNYTime(): String {
    val nyZoneId = ZoneId.of("America/New_York")
    val nyTime = ZonedDateTime.now(nyZoneId)
    return nyTime.format(DateTimeFormatter.ofPattern("HH:mm 'ET'"))
}

@Composable
private fun MarketStatus.toStringRes(): String = when (this) {
    MarketStatus.OPEN -> stringResource(R.string.market_status_open)
    MarketStatus.CLOSED -> stringResource(R.string.market_status_closed)
    MarketStatus.PREMARKET -> stringResource(R.string.market_status_premarket)
    MarketStatus.AFTER_HOURS -> stringResource(R.string.market_status_after_hours)
    MarketStatus.EARLY_CLOSE -> stringResource(R.string.market_status_early_close)
}

@Composable
private fun MarketStatusReason.toStringRes(): String = when (this) {
    MarketStatusReason.WEEKEND -> stringResource(R.string.market_status_reason_weekend)
    MarketStatusReason.HOLIDAY -> stringResource(R.string.market_status_reason_holiday)
    MarketStatusReason.REGULAR_HOURS -> stringResource(R.string.market_status_reason_regular_hours)
    MarketStatusReason.PRE_MARKET -> stringResource(R.string.market_status_reason_premarket)
    MarketStatusReason.AFTER_HOURS -> stringResource(R.string.market_status_reason_after_hours)
    MarketStatusReason.EARLY_CLOSE -> stringResource(R.string.market_status_reason_early_close)
    MarketStatusReason.OUTSIDE_HOURS -> stringResource(R.string.market_status_reason_outside_hours)
}

@Composable
private fun MarketStatus.toColor() = when (this) {
    MarketStatus.OPEN -> Color(0xFF00C853) // Dark green
    MarketStatus.PREMARKET -> Color(0xFF00ACC1) // Light blue
    MarketStatus.AFTER_HOURS -> Color(0xFFFB8C00) // Orange
    MarketStatus.CLOSED -> Color(MaterialTheme.colorScheme.error.toArgb())
    MarketStatus.EARLY_CLOSE -> Color(MaterialTheme.colorScheme.error.toArgb())
}

@ThemePreviews
@Composable
private fun PreviewMarketHoursCard() {
    VxmTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                MarketHoursCard(
                    marketHours = MarketHours(
                        status = MarketStatus.PREMARKET,
                        reason = MarketStatusReason.PRE_MARKET
                    ),
                    expanded = false
                )
                MarketHoursCard(
                    marketHours = MarketHours(
                        status = MarketStatus.OPEN,
                        reason = MarketStatusReason.REGULAR_HOURS
                    ),
                    expanded = false
                )
                MarketHoursCard(
                    marketHours = MarketHours(
                        status = MarketStatus.AFTER_HOURS,
                        reason = MarketStatusReason.AFTER_HOURS
                    ),
                    expanded = false
                )
                MarketHoursCard(
                    marketHours = MarketHours(
                        status = MarketStatus.CLOSED,
                        reason = MarketStatusReason.WEEKEND
                    ),
                    expanded = false
                )
                MarketHoursCard(
                    marketHours = MarketHours(
                        status = MarketStatus.EARLY_CLOSE,
                        reason = MarketStatusReason.EARLY_CLOSE
                    ),
                    expanded = false
                )
            }

        }
    }
}