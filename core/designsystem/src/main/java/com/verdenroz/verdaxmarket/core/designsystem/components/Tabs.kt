package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket wrapper around [TabRow] and [PagerState] to display a tab row with a pager.
 * @param state [PagerState] to control the pager
 * @param tabTitles The titles of the tabs
 * @param onClick The callback to be invoked when a tab is clicked
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VxmTabRowPager(
    state: PagerState,
    tabTitles: List<String>,
    onClick: () -> Unit
) {
    TabRow(
        selectedTabIndex = state.currentPage,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[state.currentPage]),
                color = MaterialTheme.colorScheme.secondary,
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = state.currentPage == index,
                onClick = onClick
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ThemePreviews
@Composable
fun VxmTabRowPagerPreview() {
    VxmTheme {
        VxmTabRowPager(
            state = rememberPagerState { 4 },
            tabTitles = listOf("Tab 1", "Tab 2", "Tab 3"),
            onClick = {}
        )
    }
}