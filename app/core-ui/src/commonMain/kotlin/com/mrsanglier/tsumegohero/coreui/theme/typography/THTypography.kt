package com.mrsanglier.tsumegohero.coreui.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object THTypography {
    // Header
    val header200: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = AntonFontFamily,
            fontSize = 30.sp,
            lineHeight = 45.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (0.9).sp,
        )

    // Header
    val header100: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = AntonFontFamily,
            fontSize = 20.sp,
            lineHeight = 30.1.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (0.6).sp,
        )

    // Title
    val title200: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 24.sp,
            lineHeight = 27.4.sp,
            fontWeight = FontWeight.SemiBold,
        )
    val title100: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 19.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.SemiBold,
        )

    // Content
    val content200: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.54).sp,
        )
    val content200SemiBold: TextStyle
        @Composable
        get() = content200.copy(fontWeight = FontWeight.SemiBold)

    val content100: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 14.sp,
            lineHeight = 16.3.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.42).sp,
        )
    val content100Semibold: TextStyle
        @Composable
        get() = content100.copy(fontWeight = FontWeight.SemiBold)

    val content50: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 11.sp,
            lineHeight = 12.8.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.33).sp,
        )

    // Label
    val label200: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 18.sp,
            lineHeight = 22.5.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.36.sp,
        )
    val label100: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 14.sp,
            lineHeight = 17.5.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.28.sp,
        )
    val label100Medium: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 14.sp,
            lineHeight = 17.5.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.28.sp,
        )
    val label50: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 11.sp,
            lineHeight = 13.8.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.22.sp,
        )
    val label50Light: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 11.sp,
            lineHeight = 13.8.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 0.22.sp,
        )
    val label50Semibold: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 11.sp,
            lineHeight = 13.8.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.22.sp,
        )
    val label10: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = LexendFontFamily,
            fontSize = 8.sp,
            lineHeight = 8.sp,
            fontWeight = FontWeight.Normal,
        )
}