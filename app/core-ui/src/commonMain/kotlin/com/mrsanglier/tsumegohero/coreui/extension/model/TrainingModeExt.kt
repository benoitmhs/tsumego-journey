package com.mrsanglier.tsumegohero.coreui.extension.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_brain
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_flash
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_puzzle
import com.mrsanglier.tsumegohero.app.coreui.resources.training_mode_classical
import com.mrsanglier.tsumegohero.app.coreui.resources.training_mode_difficult
import com.mrsanglier.tsumegohero.app.coreui.resources.training_mode_flash
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode

fun TrainingMode.icon(): IconSpec = when (this) {
    TrainingMode.Flash -> THDrawable.ic_flash
    TrainingMode.Classical -> THDrawable.ic_puzzle
    TrainingMode.Difficult -> THDrawable.ic_brain
}.toIconSpec(::tintColor)

@Composable
fun TrainingMode.tintColor(): Color = when (this) {
    TrainingMode.Flash -> THTheme.colors.contentTint
    TrainingMode.Classical -> THTheme.colors.detailGreen
    TrainingMode.Difficult -> THTheme.colors.detailBlue
}

fun TrainingMode.label(): TextSpec = when (this) {
    TrainingMode.Flash -> THString.training_mode_flash
    TrainingMode.Classical -> THString.training_mode_classical
    TrainingMode.Difficult -> THString.training_mode_difficult
}.toTextSpec()