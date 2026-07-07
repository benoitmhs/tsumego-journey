package com.mrsanglier.tsumegohero.coreui.theme.color

import androidx.compose.ui.graphics.Color

internal object DefaultDarkColorScheme : THColorScheme {
    // Content
    override val content: Color = Ivory
    override val contentSecondary: Color = IvoryT75
    override val contentTint: Color = Yellow
    override val contentOnSurfaceTint: Color = Grey900
    override val contentDisable: Color = IvoryT30
    override val contentCritical: Color = Pink
    override val contentInvert: Color = Purple950

    // Stroke
    override val stroke: Color = Ivory
    override val strokeSecondary: Color = IvoryT75
    override val strokeTint: Color = Yellow
    override val strokeDisable: Color = IvoryT30
    override val strokeCritical: Color = Pink
    override val strokeSuccess: Color = Green
    override val strokeDivider: Color = IvoryT75
    override val strokeCard: Color = Transparent
    override val strokeElevatedDivider: Color = Purple950

    // Surface
    override val surface0: Color = Transparent
    override val surface1: Color = Purple900
    override val surface2: Color = Purple800
    override val surface3: Color = Purple700
    override val surfaceAccent: Color = Yellow
    override val surfaceAccentGradient: Color = Orange
    override val surfaceFocus: Color = IvoryT20
    override val surfaceDisable: Color = IvoryT10
    override val surfaceBlur: Color = surface1.copy(alpha = 0.4f)
    override val surfaceCritical: Color = Pink
    override val surfaceInvert: Color = Ivory

    // Background
    override val background: Color = Purple950

    // Modal
    override val modal: Color = Purple950.copy(alpha = 0.8f)
    override val modalSoft: Color = Purple950.copy(alpha = 0.35f)

    // Other
    override val fadeImage: Color = Purple950.copy(alpha = 0.30f)
    override val detailBlue: Color = PastelBlue
    override val detailGreen: Color = PastelGreen
}
