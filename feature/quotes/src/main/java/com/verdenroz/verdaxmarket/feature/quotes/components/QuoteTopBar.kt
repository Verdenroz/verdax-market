package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAddIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmBackIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmCenterTopBar
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmDeleteIconButton
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuoteTopBar(
    symbol: String,
    profile: Result<Profile, DataError.Network>,
    isWatchlisted: Boolean,
    onNavigateBack: () -> Unit,
    addToWatchlist: (String, String?) -> Unit,
    deleteFromWatchlist: () -> Unit,
) {
    VxmCenterTopBar(
        title = {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(25))
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(4.dp)
            )
        },
        navigationIcon = {
            VxmBackIconButton(
                onClick = onNavigateBack,
            )
        },
        actions = {
            if (profile is Result.Success) {
                val quote = profile.data.quote
                if (isWatchlisted) {
                    VxmDeleteIconButton(
                        onClick = deleteFromWatchlist,
                    )
                } else {
                    VxmAddIconButton(
                        onClick = {
                            addToWatchlist(quote.name, quote.logo)
                        },
                    )
                }
            }

        }
    )
}

@ThemePreviews
@Composable
private fun PreviewStockTopBar() {
    VxmTheme {
        Surface {
            QuoteTopBar(
                symbol = "AAPL",
                profile = Result.Loading(),
                isWatchlisted = true,
                onNavigateBack = {},
                addToWatchlist = {_, _ -> },
                deleteFromWatchlist = {},
            )
        }
    }
}