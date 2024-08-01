package com.verdenroz.verdaxmarket.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.feature.home.R
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MarketIndices(
    indices: Result<List<MarketIndex>, DataError>,
    snackbarHost: SnackbarHostState,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.feature_home_market_performance),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        when (indices) {
            is Result.Loading -> {
                MarketIndexSkeleton()
            }

            is Result.Error -> {
                MarketIndexSkeleton()

                LaunchedEffect(indices.error) {
                    snackbarHost.showSnackbar(
                        message = indices.error.asUiText().asString(context),
                        actionLabel = UiText.StringResource(R.string.feature_home_dismiss)
                            .asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
            }

            is Result.Success -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = indices.data,
                        key = { index -> index.name }
                    ) { index ->
                        MarketIndexCard(index = index)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketIndexCard(index: MarketIndex) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(index.name)
            }
        },
        state = tooltipState
    ) {
        Card(
            modifier = Modifier
                .size(125.dp, 75.dp)
                .clickable { scope.launch { tooltipState.show() } },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = index.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = String.format(Locale.US, "%.2f", index.value.toDouble()),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = index.change,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (index.change.contains('+')) positiveTextColor else negativeTextColor
                    )
                    Text(
                        text = index.percentChange,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (index.change.contains('+')) positiveTextColor else negativeTextColor
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewMarketIndexCard() {
    VxmTheme {
        MarketIndexCard(
            MarketIndex(
                name = "Dow Jones",
                value = "100.0",
                change = "+100.0",
                percentChange = "+100%"
            ),
        )
    }
}

@Composable
fun MarketIndexSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(5) {
            item(key = it) {
                Card(
                    modifier = Modifier.size(125.dp, 75.dp),
                    colors = CardDefaults.cardColors(containerColor = color)
                ) {
                    // Content of the card, leave it empty for skeleton
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewMarketIndexSkeleton() {
    VxmTheme {
        MarketIndexSkeleton()
    }
}
