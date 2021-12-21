package ru.emkn.kotlin.sms.utils

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val graySurface = Color(0xFF2A2A2A)
val lightGray = Color(0xFFD3D3D3)
val green700 = Color(0xff388e3c)
val appBlack = Color(0xff100c08)

val DarkGreenColorPalette = darkColors(
    primary = green700,
    primaryVariant = green700,
    secondary = graySurface,
    background = appBlack,
    surface = appBlack,
    onPrimary = appBlack,
    onSecondary = lightGray,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color.Red,
)

val LightGreenColorPalette = lightColors(
    primary = green700,
    primaryVariant = green700,
    secondary = lightGray,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = graySurface,
    onBackground = appBlack,
    onSurface = appBlack
)