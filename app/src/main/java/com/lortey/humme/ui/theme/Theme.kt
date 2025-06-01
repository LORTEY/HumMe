package com.lortey.cardflare.ui.theme

import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.lortey.humme.AppSettings
import com.lortey.humme.Themes


private val DarkColorScheme = darkColorScheme(
    primary = Pink80,
    onPrimary = Pink20,
    primaryContainer = Pink30,
    onPrimaryContainer = Pink90,
    inversePrimary = Pink40,
    secondary = Violet80,
    onSecondary = Violet20,
    secondaryContainer = Violet30,
    onSecondaryContainer = Violet90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    tertiary = Orange80,
    onTertiary = Orange20,
    tertiaryContainer = Orange30,
    onTertiaryContainer = Orange90,
    background = Grey10,
    onBackground = Grey90,
    surface = PinkGrey30,
    onSurface = PinkGrey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey20,
    surfaceVariant = PinkGrey30,
    onSurfaceVariant = PinkGrey80,
    outline = PinkGrey80
)

private val LightColorScheme = lightColorScheme(
    primary = Pink30,
    onPrimary = Color.White,
    primaryContainer = Pink90,
    onPrimaryContainer = Pink10,
    inversePrimary = Pink80,
    secondary = Violet40,
    onSecondary = Color.White,
    secondaryContainer = Violet90,
    onSecondaryContainer = Violet10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    tertiary = Orange40,
    onTertiary = Color.White,
    tertiaryContainer = Orange90,
    onTertiaryContainer = Orange10,
    background = Grey99,
    onBackground = Grey10,
    surface = PinkGrey90,
    onSurface = PinkGrey30,
    inverseSurface = Grey20,
    inverseOnSurface = Grey90,
    surfaceVariant = PinkGrey90,
    onSurfaceVariant = PinkGrey30,
    outline = PinkGrey50

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun HumMeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit){
    val appSettings = AppSettings
    require(appSettings["Use Dynamic Color"]?.state is Boolean)
    val useDynamicColors = (appSettings["Use Dynamic Color"]?.state ?: false) as Boolean && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
    val colors = when{
        useDynamicColors && ((darkTheme && appSettings["Choose Theme"]?.state == Themes.AUTO) || appSettings["Choose Theme"]?.state == Themes.DARK )-> dynamicDarkColorScheme(LocalContext.current)
        useDynamicColors && ((!darkTheme && appSettings["Choose Theme"]?.state == Themes.AUTO) || appSettings["Choose Theme"]?.state == Themes.LIGHT ) -> dynamicLightColorScheme(LocalContext.current)
        ((darkTheme && appSettings["Choose Theme"]?.state == Themes.AUTO) || appSettings["Choose Theme"]?.state == Themes.DARK ) -> DarkColorScheme
        else -> LightColorScheme
    }
    Log.d("ThemeDebug", "Using dynamic colors: ${MaterialTheme.colorScheme.primary}")

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
