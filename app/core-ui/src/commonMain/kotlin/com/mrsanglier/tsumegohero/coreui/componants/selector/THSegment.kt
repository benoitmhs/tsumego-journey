package com.mrsanglier.tsumegohero.coreui.componants.selector

import androidx.compose.runtime.Immutable
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec

@Immutable
data class THSegment(
    val label: TextSpec,
    val icon: IconSpec? = null,
)
