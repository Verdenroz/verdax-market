package com.verdenroz.verdaxmarket.feature.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.model.HistoricalData

@Composable
internal fun Sparkline(
    timeSeries: Map<String, HistoricalData>,
    modifier: Modifier = Modifier
) {
    val prices = timeSeries.values.toList().asReversed().map { it.close }
    val isPositive = prices.lastOrNull()?.let { it > (prices.firstOrNull() ?: it) } != false
    val color = if (isPositive) getPositiveTextColor() else getNegativeTextColor()

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                if (prices.isEmpty()) return@drawBehind

                val minPrice = prices.minOrNull() ?: return@drawBehind
                val maxPrice = prices.maxOrNull() ?: return@drawBehind
                val priceRange = maxPrice - minPrice

                val path = Path()
                prices.forEachIndexed { index, price ->
                    val x = size.width * index / (prices.size - 1)
                    val y = size.height - (size.height * (price - minPrice) / priceRange)

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = 2f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
    )
}