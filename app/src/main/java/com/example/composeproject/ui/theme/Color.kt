package com.example.composeproject.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Blue60 = Color(0xFF007DFD)

val Gray70 = Color(0xFF999999)

/**
 * 常用顏色
 * */
@Immutable
data class Colors(
    val white: Color = Color(0xFFFFFFFF),
    val black: Color = Color(0xFF000000),
    val pinkRed250: Color = Color(0xFFFFBEB8),
    val pinkRed30: Color = Color(0xFFFEF4F6),
    val pinkRed50: Color = Color(0xFFFEE8ED),
    val pinkRed100: Color = Color(0xFFFED2DC),
    val pinkRed200: Color = Color(0xFFFDBBCA),
    val pinkRed300: Color = Color(0xFFFDA5B9),
    val pinkRed400: Color = Color(0xFFFC8EA7),
    val pinkRed500: Color = Color(0xFFFB6184),
    val pinkRed600: Color = Color(0xFFFA3461),
    val pinkRed700: Color = Color(0xFFFA1E50),
    val pinkRed800: Color = Color(0xFFC81840),
    val pinkRed900: Color = Color(0xFFC81840),
    val darkBlue3: Color = Color(0xFFFCFCFC),
    val darkBlue5: Color = Color(0xFFF8F9F9),
    val darkBlue10: Color = Color(0xFFF4F5F5),
    val darkBlue20: Color = Color(0xFFE9EAEB),
    val darkBlue30: Color = Color(0xFFD5D6D9),
    val darkBlue50: Color = Color(0xFFBFC1C5),
    val darkBlue100: Color = Color(0xFFAAADB3),
    val darkBlue200: Color = Color(0xFF95999F),
    val darkBlue300: Color = Color(0xFF80858C),
    val darkBlue400: Color = Color(0xFF6A7079),
    val darkBlue500: Color = Color(0xFF555C66),
    val darkBlue600: Color = Color(0xFF404753),
    val darkBlue700: Color = Color(0xFF353D49),
    val darkBlue800: Color = Color(0xFF2B3340),
    val darkBlue900: Color = Color(0xFF232A35),
    val blueGray100: Color = Color(0xFFFAFBFD),
    val blueGray200: Color = Color(0xFFF6F7FA),
    val blueGray300: Color = Color(0xFFF2F5F9),
    val blueGray400: Color = Color(0xFFEBEFF5),
    val blueGray500: Color = Color(0xFFE3E8F0),
    val blue50: Color = Color(0xFFE9F0FF),
    val blue200: Color = Color(0xFFBDD2FF),
    val blue500: Color = Color(0xFF6596FF),
    val blue700: Color = Color(0xFF2369FF),
    val dividerLineColor: Color = Color(0xFFD5D6D9),
    val maskColor: Color = Color(0x66666666),
    val deepMaskColor: Color = Color(0xEE888888),
    val aiStartBlue: Color = Color(0xFF00F6FF),
    val aiEndBlue: Color = Color(0xFF3978FF)
)

val LocalCustomColors = staticCompositionLocalOf {
    Colors()
}

@Composable
fun CustomTheme(
    content: @Composable () -> Unit
) {
    // 要改深色模式顏色在這邊改
    val customColors = if (isSystemInDarkTheme()) {
        Colors()
    } else {
        Colors()
    }

    CompositionLocalProvider(
        LocalCustomColors provides customColors,
        content = content
    )
}