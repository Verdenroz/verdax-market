package com.verdenroz.verdaxmarket.core.designsystem.components

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.verdenroz.verdaxmarket.core.designsystem.R

@Composable
fun VxmAsyncImage(
    context: Context,
    imgUrl: String,
    description: String,
    size: Pair<Int, Int>
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(imgUrl)
            .crossfade(true)
            .build(),
        contentDescription = description,
        loading = {
            Card(
                modifier = Modifier.size(size.first.dp, size.second.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {}
        },
        error = {
            Card(
                modifier = Modifier.size(size.first.dp, size.second.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = stringResource(id = R.string.core_designsystem_error_loading_data)
                )
            }
        },
        imageLoader = ImageLoader(context),
    )
}