package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAddIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmDeleteIconButton
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.feature.quotes.R

@Composable
internal fun QuoteTopBar(
    navController: NavController,
    symbol: String,
    isWatchlisted: Boolean,
    addToWatchlist: () -> Unit,
    deleteFromWatchlist: () -> Unit,
) {
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(id = R.string.feature_quotes_back),
                )
            }
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

            if (isWatchlisted) {
                VxmDeleteIconButton(onClick = deleteFromWatchlist)
            } else {
                VxmAddIconButton(onClick = addToWatchlist)
            }
        }

        HorizontalDivider(
            thickness = Dp.Hairline,
            color = MaterialTheme.colorScheme.surfaceTint,
        )
    }
}

@Preview
@Composable
private fun PreviewStockTopBar() {
    VxmTheme {
        Surface {
            QuoteTopBar(
                navController = rememberNavController(),
                symbol = "AAPL",
                isWatchlisted = true,
                addToWatchlist = { Result.Success(Unit) },
                deleteFromWatchlist = { Result.Success(Unit) },
            )
        }
    }
}