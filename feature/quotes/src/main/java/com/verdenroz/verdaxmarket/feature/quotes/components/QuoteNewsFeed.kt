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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmSubcomposeAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.feature.quotes.R

@Composable
internal fun QuoteNewsFeed(
    news: List<News>,
    modifier: Modifier = Modifier
) {
    if (news.isEmpty()) {
        Box(
            modifier = modifier
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
                    .padding(16.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                items = news,
                key = { newsItem -> newsItem.title }
            ) { item ->
                QuoteNewsItem(news = item)
            }
        }
    }
}

@Composable
private fun QuoteNewsItem(
    news: News,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.link))
                context.startActivity(intent)
            },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                VxmSubcomposeAsyncImage(
                    context = context,
                    model = news.img,
                    description = stringResource(id = R.string.feature_quotes_news_image_description),
                    modifier = Modifier.fillMaxSize()
                )
            }
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
        }
    )
}

@ThemePreviews
@Composable
private fun PreviewNewsFeed() {
    VxmTheme {
        Column {
            QuoteNewsFeed(
                news =
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
            QuoteNewsFeed(
                news = emptyList()
            )
        }
    }
}