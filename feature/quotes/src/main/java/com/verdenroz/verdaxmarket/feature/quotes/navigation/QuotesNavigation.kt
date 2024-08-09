package com.verdenroz.verdaxmarket.feature.quotes.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.feature.quotes.QuotesRoute
import com.verdenroz.verdaxmarket.feature.quotes.R

private const val QUOTE_ARG = "symbol"
private const val QUOTE_ROUTE = "quote_route"

fun NavController.navigateToQuote(symbol: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(createQuoteRoute(symbol)) {
        navOptions()
    }
}

fun createQuoteRoute(symbol: String): String {
    return "$QUOTE_ROUTE/$symbol"
}


fun NavGraphBuilder.quoteScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    composable(
        route = "quote_route/{$QUOTE_ARG}",
        arguments = listOf(
            navArgument(QUOTE_ARG) { type = NavType.StringType },
        ),
    ) {
        val symbol = it.arguments?.getString(QUOTE_ARG) ?: ""
        val context = LocalContext.current
        if (symbol.isEmpty()) {
            LaunchedEffect(Unit) {
                navController.popBackStack()
                snackbarHostState.showSnackbar(
                    UiText.StringResource(R.string.feature_quotes_error).asString(context)
                )
            }
        } else {
            QuotesRoute(
                symbol = symbol,
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }

    }
}
