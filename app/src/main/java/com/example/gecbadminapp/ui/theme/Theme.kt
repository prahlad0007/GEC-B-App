package com.example.gecbadminapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CustomDarkColorScheme = darkColorScheme(
    primary = AdminAccent,
    secondary = AdminDark,
    tertiary = Pink80,
    background = Grey900,
    surface = Grey800,
    onPrimary = WhiteText,
    onSecondary = WhiteText,
    onTertiary = WhiteText,
    onBackground = WhiteText,
    onSurface = WhiteText,
    error = ErrorRed,
    onError = WhiteText
)

private val CustomLightColorScheme = lightColorScheme(
    primary = ButtonPrimary,
    secondary = ButtonSecondary,
    tertiary = Pink40,
    background = ScreenBackground,
    surface = CardBackground,
    onPrimary = WhiteText,
    onSecondary = WhiteText,
    onTertiary = WhiteText,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    error = ErrorRed,
    onError = WhiteText
)

@Composable
fun GECBAdminAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CustomDarkColorScheme
        else -> CustomLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
