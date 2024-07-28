package com.verdenroz.verdaxmarket.data.model

import com.verdenroz.verdaxmarket.model.News
import com.verdenroz.verdaxmarket.network.model.NewsResponse

fun List<NewsResponse>.asExternalModel() = map {
    News(
        title = it.title,
        link = it.link,
        source = it.source,
        img = it.img,
        time = it.time,
    )
}