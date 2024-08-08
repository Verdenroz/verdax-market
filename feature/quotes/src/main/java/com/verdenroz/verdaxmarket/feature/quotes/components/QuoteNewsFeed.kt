package com.verdenroz.verdaxmarket.feature.quotes.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmSubcomposeAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.feature.quotes.R

@Composable
internal fun QuoteNewsFeed(
    snackbarHostState: SnackbarHostState,
    news: Result<List<News>, DataError.Network>
) {
    val context = LocalContext.current
    when (news) {

        is Result.Loading -> {
            StockNewsFeedSkeleton()
        }

        is Result.Error -> {
            StockNewsFeedSkeleton()

            LaunchedEffect(news.error) {
                snackbarHostState.showSnackbar(
                    message = news.error.asUiText().asString(context),
                    actionLabel = UiText.StringResource(R.string.feature_quotes_dismiss)
                        .asString(context),
                    duration = SnackbarDuration.Short
                )
            }
        }

        is Result.Success -> {
            if (news.data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_quotes_no_news),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(100))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(
                        items = news.data,
                        key = { news -> news.link },
                    ) { item ->
                        StockNewsItem(news = item)
                    }
                }
            }
        }
    }
}


@Composable
private fun StockNewsItem(news: News) {
    val context = LocalContext.current
    ListItem(
        leadingContent = {
            VxmSubcomposeAsyncImage(
                context = context,
                model = news.img,
                description = stringResource(id = R.string.feature_quotes_news_image_description)
            )
        },
        headlineContent = {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = news.source,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = news.time,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.link))
                context.startActivity(intent)
            }
    )
}

@Composable
private fun StockNewsFeedSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        repeat(5) {
            item(key = it) {
                ListItem(
                    headlineContent = { /*skeleton*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewNewsFeed() {
    VxmTheme {
        Column {
            QuoteNewsFeed(
                snackbarHostState = SnackbarHostState(),
                news = Result.Success(
                    listOf(
                        News(
                            title = "Title",
                            link = "https://www.google.com",
                            source = "Source",
                            time = "Time",
                            img = "img"
                        ),
                        News(
                            title = "Title",
                            link = "https://www.yahoo.com",
                            source = "Source",
                            time = "Time",
                            img = "img"
                        )
                    )
                )
            )
            QuoteNewsFeed(
                snackbarHostState = SnackbarHostState(),
                news = Result.Loading()
            )
        }
    }
}