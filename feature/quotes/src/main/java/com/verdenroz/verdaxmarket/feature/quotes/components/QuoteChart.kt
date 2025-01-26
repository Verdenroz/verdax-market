package com.verdenroz.verdaxmarket.feature.quotes.components

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmRadioButton
import com.verdenroz.verdaxmarket.core.designsystem.theme.LocalTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.feature.quotes.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

private const val SPACING = 75f

@Composable
internal fun QuoteChart(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    timeSeries: Map<TimePeriod, Result<Map<String, HistoricalData>, DataError.Network>>,
) {
    val isDarkTheme = LocalTheme.current

    val positiveTextColor = getPositiveTextColor()
    val negativeTextColor = getNegativeTextColor()
    val positiveBackgroundColor = getPositiveBackgroundColor()
    val negativeBackgroundColor = getNegativeBackgroundColor()
    var timePeriod by rememberSaveable { mutableStateOf(TimePeriod.SIX_MONTH) }

    when (val currentTimeSeries = timeSeries[timePeriod]) {
        is Result.Loading, null -> {
            StockChartSkeleton()
        }

        is Result.Error -> {
            StockChartError()

            TimePeriodBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                currentTimePeriod = timePeriod,
                updateTimeSeries = { timePeriod = it },
            )
        }

        is Result.Success -> {
            val density = LocalDensity.current
            val scope = rememberCoroutineScope()

            val data = currentTimeSeries.data.entries.toList()
                .asReversed()
                .associate { it.key to it.value }

            val guidelineColor = MaterialTheme.colorScheme.outline

            val positiveChart = remember(data) {
                currentTimeSeries.data.values.first().close > currentTimeSeries.data.values.last().close
            }

            val upperValue = remember(data) {
                data.values.maxOf { it.close }.plus(1)
            }
            val lowerValue = remember(data) {
                data.values.minOf { it.close }.minus(1)
            }

            val selectedData = rememberSaveable {
                mutableStateOf<Pair<String, HistoricalData>?>(null)
            }

            Text(
                text = selectedData.value.let {
                    it?.let { (_, historicalData) ->
                        "${historicalData.close}"
                    } ?: ""
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedData.value.let {
                        it?.let { (date, _) ->
                            formatDate(date, timePeriod)
                        } ?: ""
                    },
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 1.25.sp,
                )
                Text(
                    text = selectedData.value.let {
                        it?.let { (_, historicalData) ->
                            stringResource(R.string.feature_quotes_volume) + " ${historicalData.volume}"
                        } ?: ""
                    },
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 1.25.sp,
                )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)
            ) {
                Box(modifier = modifier
                    .pointerInput(data) {
                        detectTapGestures(
                            onPress = { offset ->
                                val spacePerHour = (size.width - SPACING) / data.size
                                val index = ((offset.x - SPACING) / spacePerHour).toInt()

                                if (index in data.entries.indices) {
                                    selectedData.value = data.entries
                                        .elementAt(index)
                                        .toPair()
                                } else {
                                    selectedData.value = null
                                }
                                scope.launch { listState.animateScrollToItem(0) }
                            },
                            onDoubleTap = {
                                selectedData.value = null
                            },
                            onTap = {
                                selectedData.value = null
                            }
                        )
                    }
                    .pointerInput(data) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                val spacePerHour = (size.width - SPACING) / data.size
                                // Calculate the index of the selected data based on the x-coordinate of the drag
                                val index =
                                    ((change.position.x - SPACING) / spacePerHour).toInt()
                                if (index in data.entries.indices
                                ) {
                                    // Update the selected data
                                    selectedData.value = data.entries
                                        .elementAt(index)
                                        .toPair()
                                } else {
                                    // Clear the selected data if the drag is outside the data range
                                    selectedData.value = null
                                }
                                scope.launch { listState.animateScrollToItem(0) }
                            },
                            onDragEnd = {
                                // Clear the selected data when the drag ends
                                selectedData.value = null
                            }
                        )
                    }
                    .drawWithCache {
                        val topShader =
                            if (positiveChart) positiveTextColor.copy(alpha = .5f) else negativeTextColor.copy(
                                alpha = .5f
                            )
                        val bottomShader =
                            if (positiveChart) positiveBackgroundColor else negativeBackgroundColor
                        val textColor =
                            if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                        val textPaint = Paint().apply {
                            color = textColor
                            textAlign = Paint.Align.CENTER
                            textSize = density.run { 12.sp.toPx() }
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        onDrawBehind {
                            drawChart(
                                timeSeries = data,
                                size = size,
                                lowerValue = lowerValue,
                                upperValue = upperValue,
                                topShader = topShader,
                                bottomShader = bottomShader,
                                guidelineColor = guidelineColor,
                                textPaint = textPaint,
                                density = density,
                                selectedData = selectedData,
                                timePeriod = timePeriod
                            )
                        }
                    }
                )

                TimePeriodBar(
                    modifier = Modifier.fillMaxWidth(),
                    currentTimePeriod = timePeriod,
                    updateTimeSeries = { timePeriod = it },
                )
            }
        }
    }
}

@Composable
private fun TimePeriodBar(
    modifier: Modifier = Modifier,
    currentTimePeriod: TimePeriod,
    updateTimeSeries: (TimePeriod) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimePeriod.entries.forEach { timePeriod ->
            VxmRadioButton(
                selected = timePeriod == currentTimePeriod,
                onClick = { updateTimeSeries(timePeriod) },
                label = timePeriod.value.uppercase(),
            )
        }
    }
}

@Composable
private fun StockChartSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 400.dp)
            .padding(16.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun StockChartError(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 400.dp)
            .padding(16.dp)
            .background(color),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.Warning, contentDescription = stringResource(R.string.feature_quotes_chart_error))
        Text(
            text = stringResource(R.string.feature_quotes_chart_error),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Helper function to draw the chart given [timeSeries]
 */
private fun DrawScope.drawChart(
    timeSeries: Map<String, HistoricalData>,
    size: Size,
    lowerValue: Float,
    upperValue: Float,
    topShader: Color,
    bottomShader: Color,
    guidelineColor: Color,
    textPaint: Paint,
    density: Density,
    selectedData: MutableState<Pair<String, HistoricalData>?>,
    timePeriod: TimePeriod
) {
    val spacePerHour = (size.width - SPACING) / timeSeries.size

    // Draw the y-axis labels
    drawYAxis(
        drawScope = this,
        textPaint = textPaint,
        size = size,
        lowerValue = lowerValue,
        upperValue = upperValue
    )

    // Draw the x-axis labels
    drawXAxis(
        drawScope = this,
        textPaint = textPaint,
        size = size,
        spacePerHour = spacePerHour,
        data = timeSeries,
        timePeriod = timePeriod
    )
    // Draw the stroke and fill path
    drawPaths(
        drawScope = this,
        data = timeSeries,
        size = size,
        lowerValue = lowerValue,
        upperValue = upperValue,
        spacePerHour = spacePerHour,
        topShader = topShader,
        bottomShader = bottomShader,
        density = density
    )

    // Draw the selected data
    drawSelectedData(
        drawScope = this,
        selectedData = selectedData.value,
        data = timeSeries,
        size = size,
        lowerValue = lowerValue,
        upperValue = upperValue,
        spacePerHour = spacePerHour,
        guidelineColor = guidelineColor,
        density = density
    )
}

/**
 * Helper function to draw the y-axis labels
 */
private fun drawYAxis(
    drawScope: DrawScope,
    textPaint: Paint,
    size: Size,
    lowerValue: Float,
    upperValue: Float
) {
    val priceStep = (upperValue - lowerValue) / 5f
    (0..4).forEach { i ->
        drawScope.drawContext.canvas.nativeCanvas.apply {
            val price = (lowerValue + priceStep * i).roundToInt().toString()
            val yPos = size.height - SPACING - i * size.height / 5f

            drawText(price, 5f, yPos - SPACING / 2 + 50f, textPaint)
        }
    }
}

/**
 * Helper function to draw the x-axis labels and line
 */
private fun drawXAxis(
    drawScope: DrawScope,
    textPaint: Paint,
    size: Size,
    spacePerHour: Float,
    data: Map<String, HistoricalData>,
    timePeriod: TimePeriod
) {
    val stepSize = maxOf((data.size / 4.5).toInt(), 1)
    for (i in data.entries.indices step stepSize) {
        var date = data.entries.elementAt(i).key
        var dateIndex = i
        // Adjust the date for one day time period to show time that is a multiple of 15 minutes
        if (timePeriod == TimePeriod.ONE_DAY) {
            val result  = findNextClosestTime(date, data, i)
            date = result.first
            dateIndex = result.second
        }

        val formattedDate = formatDate(date, timePeriod)
        val x = SPACING + dateIndex * spacePerHour

        drawScope.drawContext.canvas.nativeCanvas.drawText(
            formattedDate,
            x + 10f,
            size.height + 5f,
            textPaint
        )

        // Draw the tick above the label
        drawScope.drawLine(
            color = Color(textPaint.color),
            start = Offset(x, size.height - SPACING),
            end = Offset(x, size.height - 50f),
            strokeWidth = 5f
        )

        // Draw the x-axis line
        drawScope.drawLine(
            color = Color(textPaint.color),
            start = Offset(SPACING, size.height - SPACING),
            end = Offset(
                size.width,
                size.height - SPACING
            ),
            strokeWidth = 2f
        )
    }
}

/**
 * Helper function to draw the stroke and fill path
 */
private fun drawPaths(
    drawScope: DrawScope,
    data: Map<String, HistoricalData>,
    size: Size,
    lowerValue: Float,
    upperValue: Float,
    spacePerHour: Float,
    topShader: Color,
    bottomShader: Color,
    density: Density
) {
    var lastX = 0f

    val strokePath = Path().apply {
        val height = size.height
        for (i in data.entries.indices) {
            val info = data.entries.elementAt(i).value
            val leftRatio = (info.close - lowerValue) / (upperValue - lowerValue)

            val x = SPACING + i * spacePerHour
            val y = height - SPACING - (leftRatio * height)
            if (i == 0) {
                moveTo(x, y)
            } else {
                lineTo(x, y)
            }
            if (i == data.entries.size - 1) {
                lastX = x
            }
        }
    }

    val fillPath = Path().apply {
        addPath(strokePath)
        lineTo(lastX, size.height - SPACING)
        lineTo(SPACING, size.height - SPACING)
        close()
    }

    // Draw the fill path
    drawScope.drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                bottomShader,
                Color.Transparent
            ),
            endY = size.height - SPACING
        )
    )

    // Draw the stroke path
    drawScope.drawPath(
        path = strokePath,
        color = topShader,
        style = Stroke(
            width = with(density) { 3.dp.toPx() },
            cap = StrokeCap.Round
        )
    )
}

/**
 * Helper function to draw the selected data with guideline/indicator on drag or tap
 */
private fun drawSelectedData(
    drawScope: DrawScope,
    selectedData: Pair<String, HistoricalData>?,
    data: Map<String, HistoricalData>,
    size: Size,
    lowerValue: Float,
    upperValue: Float,
    spacePerHour: Float,
    guidelineColor: Color,
    density: Density
) {
    // Draw the selected data
    selectedData?.let { (date, historicalData) ->
        val index = data.entries.indexOfFirst { it.key == date }
        val x = SPACING + index * spacePerHour
        val ratio = (historicalData.close - lowerValue) / (upperValue - lowerValue)
        val y = size.height - SPACING - (ratio * size.height)

        // Draw the guideline
        drawScope.drawLine(
            color = guidelineColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height - SPACING),
            strokeWidth = with(density) { 1.dp.toPx() },
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 1f)
        )

        // Draw a marker
        drawScope.drawCircle(
            color = Color.Black,
            center = Offset(x, y),
            radius = with(density) { 5.dp.toPx() }
        )
    }
}

/**
 * Helper function to format the date based on the number of days since the earliest date
 * @see formatDateForTime
 * @see formatDateForMonth
 * @see formatDateForYear
 * @see formatDateForYTD
 */
private fun formatDate(date: String, timePeriod: TimePeriod): String {
    return when (timePeriod) {
        TimePeriod.ONE_DAY -> formatDateForTime(date)
        TimePeriod.FIVE_DAY, TimePeriod.ONE_MONTH -> formatDateForMonth(date)
        TimePeriod.YEAR_TO_DATE -> formatDateForYTD(date)
        else -> formatDateForYear(date)
    }
}

/**
 * Helper function to format the date for time in the form of "h:mm a"
 * Ex. "Jul 1, 2021 9:30 AM" -> "9:30 AM"
 */
private fun formatDateForTime(date: String): String {
    val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
    val parsedDate = formatter.parse(date)
    val outputFormatter = SimpleDateFormat("h:mm a", Locale.US)
    return parsedDate?.let { outputFormatter.format(it) } ?: date
}

/**
 * Helper function to format the date for month in the form of "MMM d"
 * Ex. "Jul 1, 2021 9:30 AM" -> "Jul 1"
 */
private fun formatDateForMonth(date: String): String {
    val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
    val parsedDate = formatter.parse(date)
    val outputFormatter = SimpleDateFormat("MMM d", Locale.US)
    return parsedDate?.let { outputFormatter.format(it) } ?: date
}

/**
 * Helper function to format the date for year in the form of "MMM yyyy"
 * Ex. "Jul 1, 2021 9:30 AM" -> "Jul 2021"
 */
private fun formatDateForYear(date: String): String {
    val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
    val parsedDate = formatter.parse(date)
    val outputFormatter = SimpleDateFormat("MMM yyyy", Locale.US)
    return parsedDate?.let { outputFormatter.format(it) } ?: date
}

/**
 * Helper function to format the date for year-to-date adaptive to the current date
 * Ex. "Jul 1, 2021 9:30 AM" -> "9:30 AM" if the date is today
 * Ex. "Jul 1, 2021 9:30 AM" -> "Jul 1" if the date is within the last 30 days
 * Ex. "Jul 1, 2021 9:30 AM" -> "Jul 2021" if the date is older than 30 days
 */
private fun formatDateForYTD(date: String): String {
    val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
    val parsedDate = formatter.parse(date) ?: return date

    val currentDate = Calendar.getInstance().time
    val diffInMillis = currentDate.time - parsedDate.time
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

    return when {
        diffInDays <= 1 -> formatDateForTime(date)
        diffInDays <= 30 -> formatDateForMonth(date)
        else -> formatDateForYear(date)
    }
}

/**
 * Helper function to find the next closest time in the data that is a multiple of 15 minutes
 * Ex. "Jul 1, 2021 9:31 AM" -> "Jul 1, 2021 9:45 AM"
 * @param date the date to find the next closest time for
 * @param data the historical data to search for the next closest time
 */
private fun findNextClosestTime(date: String, data: Map<String, HistoricalData>, startIndex: Int): Pair<String, Int> {
    val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
    val parsedDate = formatter.parse(date) ?: return Pair(date, startIndex)

    val calendar = Calendar.getInstance().apply { time = parsedDate }
    val minutes = calendar.get(Calendar.MINUTE)

    if (minutes % 15 == 0) {
        return Pair(date, startIndex)
    }

    for (i in startIndex until data.entries.size) {
        val entry = data.entries.elementAt(i)
        val entryDate = formatter.parse(entry.key) ?: continue
        val entryCalendar = Calendar.getInstance().apply { time = entryDate }
        val entryMinutes = entryCalendar.get(Calendar.MINUTE)

        if (entryMinutes % 15 == 0 && entryDate.after(parsedDate)) {
            return Pair(entry.key, i)
        }
    }

    return Pair(date, startIndex)
}
