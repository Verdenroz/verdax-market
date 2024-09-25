package com.verdenroz.verdaxmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.NetworkMonitor
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.ui.VxmApp
import com.verdenroz.verdaxmarket.ui.rememberVxmAppState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var marketMonitor: MarketMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberVxmAppState(
                networkMonitor = networkMonitor,
                marketMonitor = marketMonitor
            )
            VxmTheme {
                VxmApp(appState = appState)
            }
        }
    }
}