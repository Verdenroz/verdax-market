package com.verdenroz.verdaxmarket.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val positiveTextColorLight = Color(0xFF29816D)
val positiveTextColorDark = Color(0xFF11FFB2)
val positiveBackgroundColorLight = Color(0xFFA2EBD3)
val positiveBackgroundColorDark = Color(0xFF004D40)
val negativeTextColorLight = Color(0xFFFF0000)
val negativeTextColorDark = Color(0xFFFFDDDD)
val negativeBackgroundColorLight = Color(0x55DF4661)
val negativeBackgroundColorDark = Color(0xFFB71C1C)

@Composable
fun getPositiveTextColor(): Color {
    val isDarkTheme = LocalTheme.current
    return if (isDarkTheme) positiveTextColorDark else positiveTextColorLight
}

@Composable
fun getNegativeTextColor(): Color {
    val isDarkTheme = LocalTheme.current
    return if (isDarkTheme) negativeTextColorDark else negativeTextColorLight
}

@Composable
fun getPositiveBackgroundColor(): Color {
    val isDarkTheme = LocalTheme.current
    return if (isDarkTheme) positiveBackgroundColorDark else positiveBackgroundColorLight
}

@Composable
fun getNegativeBackgroundColor(): Color {
    val isDarkTheme = LocalTheme.current
    return if (isDarkTheme) negativeBackgroundColorDark else negativeBackgroundColorLight
}

val authActionColor = Color(0xFF1E88E5)
val primaryLight = Color(0xFFFFFFFF)
val onPrimaryLight = Color(0xFF1A1C18)
val primaryContainerLight = Color(0xFFB5C4B5)
val onPrimaryContainerLight = Color(0xFF1A1C18)
val secondaryLight = Color(0xFF2D8D66)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFF191B1B)
val onSecondaryContainerLight = Color(0xFFADDDDB)
val tertiaryLight = Color(0xFF00181B)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFF409288)
val onTertiaryContainerLight = Color(0xFFA4CAD0)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFF9DEDC)
val onErrorContainerLight = Color(0xFFB3261E)
val backgroundLight = Color(0xFFFAFAFA)
val onBackgroundLight = Color(0xFF1A1C18)
val surfaceLight = Color(0xFFFFFFFF)
val onSurfaceLight = Color(0xFF1A1C18)
val surfaceVariantLight = Color(0xFFF3F3F3)
val onSurfaceVariantLight = Color(0xFF44483E)
val outlineLight = Color(0xFF74776D)
val outlineVariantLight = Color(0xFFC4C7BC)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF1A1C18)
val inverseOnSurfaceLight = Color(0xFFF1F1EA)
val inversePrimaryLight = Color(0xFFB3DFCA)
val surfaceDimLight = Color(0xFFF8F8F8)
val surfaceBrightLight = Color(0xFFFFFFFF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFAFAFA)
val surfaceContainerLight = Color(0xFFF5F5F5)
val surfaceContainerHighLight = Color(0xFFF0F0F0)
val surfaceContainerHighestLight = Color(0xFFEBEBEB)

val primaryDark = Color(0xFF010407)
val onPrimaryDark = Color(0xFFFFFFFF)
val primaryContainerDark = Color(0xFF394D50)
val onPrimaryContainerDark = Color(0xFFDBE2EB)
val secondaryDark = Color(0xFFA0CFCD)
val onSecondaryDark = Color(0xFF011F1E)
val secondaryContainerDark = Color(0xFF002D2D)
val onSecondaryContainerDark = Color(0xFF8DBCBA)
val tertiaryDark = Color(0xFF92C9BC)
val onTertiaryDark = Color(0xFF0D353B)
val tertiaryContainerDark = Color(0xFF295249)
val onTertiaryContainerDark = Color(0xFF8AB0B6)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF121315)
val onBackgroundDark = Color(0xFFE3E2E4)
val surfaceDark = Color(0xFF121315)
val onSurfaceDark = Color(0xFFE3E2E4)
val surfaceVariantDark = Color(0xFF43474C)
val onSurfaceVariantDark = Color(0xFFC3C7CD)
val outlineDark = Color(0xFF8D9197)
val outlineVariantDark = Color(0xFF43474C)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE3E2E4)
val inverseOnSurfaceDark = Color(0xFF303032)
val inversePrimaryDark = Color(0xFF4B6076)
val surfaceDimDark = Color(0xFF121315)
val surfaceBrightDark = Color(0xFF38393B)
val surfaceContainerLowestDark = Color(0xFF0D0E10)
val surfaceContainerLowDark = Color(0xFF1B1C1D)
val surfaceContainerDark = Color(0xFF1F2021)
val surfaceContainerHighDark = Color(0xFF292A2C)
val surfaceContainerHighestDark = Color(0xFF343536)






