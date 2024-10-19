package com.verdenroz.verdaxmarket.core.designsystem.components

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.verdenroz.verdaxmarket.core.designsystem.R

/**
 * VerdaxMarket wrapper around [AsyncImage] to load images asynchronously.
 * @param context The context to be used to load the image.
 * @param model The URL of the image to be loaded.
 * @param description The content description of the image.
 */
@Composable
fun VxmAsyncImage(
    context: Context,
    model: String,
    description: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .scale(Scale.FILL)
            .build(),
        contentDescription = description,
        modifier = modifier
    )
}

/**
 * VerdaxMarket wrapper around [SubcomposeAsyncImage] that composes loading and error states.
 * @param context The context to be used to load the image.
 * @param model The URL of the image to be loaded.
 * @param description The content description of the image.
 */
@Composable
fun VxmSubcomposeAsyncImage(
    context: Context,
    model: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .build(),
        contentDescription = description,
        loading = {
            Card(
                modifier = modifier,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {}
        },
        error = {
            Card(
                modifier = modifier,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = stringResource(id = R.string.core_designsystem_error_loading_data)
                )
            }
        },
        modifier = modifier
    )
}