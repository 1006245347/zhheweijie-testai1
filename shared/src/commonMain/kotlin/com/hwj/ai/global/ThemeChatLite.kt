package com.hwj.ai.global

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hwj.ai.setColorScheme
import kotlinx.coroutines.launch

/**
 * @author by jason-何伟杰，2025/2/25
 * des:不能随便引入主题，会重置主题色
 */
@Composable
fun ThemeChatLite(
    content: @Composable () -> Unit
) {
    //这异步会影响？
    val dark = remember { mutableStateOf(false) }
    val subScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        subScope.launch {
            dark.value = getCacheBoolean(CODE_IS_DARK)
        }
    }
    //兼容平台的主题色
    val colorScheme = setColorScheme(dark.value)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = ColorTextGPT
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = Purple80,
    tertiary = PrimaryColor,
    background = BackGroundColor2,
    surface = PrimaryColor,
)

val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = Purple40,
    tertiary = PrimaryColor,
    background = BackGroundColor1,
    surface = BackGroundColor1,

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