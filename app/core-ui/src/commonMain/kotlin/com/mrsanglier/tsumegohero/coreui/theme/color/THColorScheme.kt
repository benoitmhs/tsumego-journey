package com.mrsanglier.tsumegohero.coreui.theme.color

import androidx.compose.ui.graphics.Color

interface THColorScheme {
    // Content
    val content: Color
    val contentSecondary: Color
    val contentTint: Color
    val contentOnSurfaceTint: Color
    val contentDisable: Color
    val contentCritical: Color
    val contentInvert: Color

    // Stroke
    val stroke: Color
    val strokeSecondary: Color
    val strokeTint: Color
    val strokeDisable: Color
    val strokeCritical: Color
    val strokeSuccess: Color
    val strokeDivider: Color
    val strokeCard: Color
    val strokeElevatedDivider: Color

    // Surface
    val surface0: Color
    val surface1: Color
    val surface2: Color
    val surface3: Color
    val surfaceAccent: Color
    val surfaceAccentGradient: Color
    val surfaceFocus: Color
    val surfaceDisable: Color
    val surfaceBlur: Color
    val surfaceCritical: Color
    val surfaceInvert: Color

    // Background
    val background: Color

    // Modal
    val modal: Color
    val modalSoft: Color

    // Other
    val fadeImage: Color
    val detailGreen: Color
    val detailBlue: Color

    @Suppress("CyclomaticComplexMethod")
    fun copy(
        content: Color? = null,
        contentInvert: Color? = null,
        contentSecondary: Color? = null,
        contentTint: Color? = null,
        contentOnSurfaceTint: Color? = null,
        contentDisable: Color? = null,
        contentCritical: Color? = null,
        stroke: Color? = null,
        strokeSecondary: Color? = null,
        strokeTint: Color? = null,
        strokeDisable: Color? = null,
        strokeCritical: Color? = null,
        strokeSuccess: Color? = null,
        strokeDivider: Color? = null,
        strokeCard: Color? = null,
        strokeElevatedDivider: Color? = null,
        surface0: Color? = null,
        surface1: Color? = null,
        surface2: Color? = null,
        surface2Disable: Color? = null,
        surface3: Color? = null,
        surfaceTintPrimary: Color? = null,
        surfaceTintPrimarySoft: Color? = null,
        surfaceTintPrimaryStrong: Color? = null,
        surfaceTintSecondary: Color? = null,
        surfaceTintSecondarySoft: Color? = null,
        surfaceTintSecondaryStrong: Color? = null,
        surfaceAccentGradient: Color? = null,
        surfaceFocus: Color? = null,
        surfaceDisable: Color? = null,
        surfaceDark: Color? = null,
        surfaceLight: Color? = null,
        surfaceBlur: Color? = null,
        surfaceCritical: Color? = null,
        surfaceSecondary: Color? = null,
        surfaceInvert: Color? = null,
        background: Color? = null,
        modal: Color? = null,
        modalSoft: Color? = null,
        participationYes: Color? = null,
        participationYesSoft: Color? = null,
        participationNo: Color? = null,
        participationNoSoft: Color? = null,
        participationMaybe: Color? = null,
        participationMaybeSoft: Color? = null,
    ): THColorScheme = object : THColorScheme {
        override val content: Color = content ?: this@THColorScheme.content
        override val contentSecondary: Color = contentSecondary ?: this@THColorScheme.contentSecondary
        override val contentTint: Color = contentTint ?: this@THColorScheme.contentTint
        override val contentOnSurfaceTint: Color = contentOnSurfaceTint ?: this@THColorScheme.contentOnSurfaceTint
        override val contentDisable: Color = contentDisable ?: this@THColorScheme.contentDisable
        override val contentCritical: Color = contentCritical ?: this@THColorScheme.contentCritical
        override val contentInvert: Color = contentInvert ?: this@THColorScheme.contentInvert
        override val stroke: Color = stroke ?: this@THColorScheme.stroke
        override val strokeSecondary: Color = strokeSecondary ?: this@THColorScheme.strokeSecondary
        override val strokeTint: Color = strokeTint ?: this@THColorScheme.strokeTint
        override val strokeDisable: Color = strokeDisable ?: this@THColorScheme.strokeDisable
        override val strokeCritical: Color = strokeCritical ?: this@THColorScheme.strokeCritical
        override val strokeSuccess: Color = strokeCritical ?: this@THColorScheme.strokeSuccess
        override val strokeDivider: Color = strokeDivider ?: this@THColorScheme.strokeDivider
        override val strokeCard: Color = strokeCard ?: this@THColorScheme.strokeCard
        override val strokeElevatedDivider: Color = strokeElevatedDivider ?: this@THColorScheme.strokeElevatedDivider
        override val surface0: Color = surface0 ?: this@THColorScheme.surface0
        override val surface1: Color = surface1 ?: this@THColorScheme.surface1
        override val surface2: Color = surface2 ?: this@THColorScheme.surface2
        override val surface3: Color = surface3 ?: this@THColorScheme.surface3
        override val surfaceAccent: Color = surfaceTintPrimary ?: this@THColorScheme.surfaceAccent
        override val surfaceAccentGradient: Color = surfaceAccentGradient ?: this@THColorScheme.surfaceAccentGradient
        override val surfaceFocus: Color = surfaceFocus ?: this@THColorScheme.surfaceFocus
        override val surfaceDisable: Color = surfaceDisable ?: this@THColorScheme.surfaceDisable
        override val surfaceBlur: Color = surfaceBlur ?: this@THColorScheme.surfaceBlur
        override val surfaceCritical: Color = surfaceCritical ?: this@THColorScheme.surfaceCritical
        override val surfaceInvert: Color = surfaceInvert ?: this@THColorScheme.surfaceInvert
        override val background: Color = background ?: this@THColorScheme.background
        override val modal: Color = modal ?: this@THColorScheme.modal
        override val modalSoft: Color = modalSoft ?: this@THColorScheme.modalSoft
        override val fadeImage: Color = this@THColorScheme.fadeImage
        override val detailBlue: Color = this@THColorScheme.detailBlue
        override val detailGreen: Color = this@THColorScheme.detailGreen
    }
}
