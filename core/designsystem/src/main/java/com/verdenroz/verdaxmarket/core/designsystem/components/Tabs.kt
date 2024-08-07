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
 * @param tabs content of the tabs
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VxmTabRowPager(
    state: PagerState,
    tabs: @Composable () -> Unit,
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
        tabs = tabs,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@ThemePreviews
@Composable
private fun VxmTabRowPagerPreview() {
    VxmTheme {
        VxmTabRowPager(
            state = rememberPagerState { 4 },
            tabs = {
                Tab(
                    text = { Text("Tab 1") },
                    onClick = { },
                    selected = true
                )
                Tab(
                    text = { Text("Tab 2") },
                    onClick = { },
                    selected = false
                )
                Tab(
                    text = { Text("Tab 3") },
                    onClick = { },
                    selected = false
                )
            }
        )
    }
}