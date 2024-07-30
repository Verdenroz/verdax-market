package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.network.model.NewsResponse

fun List<NewsResponse>.asExternalModel() = map {
    News(
        title = it.title,
        link = it.link,
        source = it.source,
        img = it.img,
        time = it.time,
    )
}