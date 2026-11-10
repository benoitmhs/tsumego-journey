package com.mrsanglier.tsumegohero.coreui.componants.fabmenu

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec

@Immutable
data class THFabMenuItem(
    val label: TextSpec,
    val icon: IconSpec,
    val onClick: () -> Unit,
)
