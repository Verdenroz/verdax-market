package com.verdenroz.verdaxmarket.feature.quotes.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.verdenroz.verdaxmarket.feature.quotes.QuotesRoute

const val QUOTES_ROUTE = "quote_route"
const val QUOTE_SYMBOL_ARG = "symbol"

fun NavController.navigateToQuote(
    symbol: String,
    navOptions: NavOptions? = null
) {
    this.navigate("$QUOTES_ROUTE/$symbol", navOptions)
}

fun NavGraphBuilder.quotesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
) {
    composable(
        route = "$QUOTES_ROUTE/{$QUOTE_SYMBOL_ARG}",
    ) { backStackEntry ->
        val symbol = backStackEntry.arguments?.getString(QUOTE_SYMBOL_ARG)
            ?: throw IllegalStateException("Symbol is required for QuotesScreen")

        QuotesRoute(
            symbol = symbol,
            onNavigateBack = onNavigateBack,
            onNavigateToQuote = onNavigateToQuote,
        )
    }
}
