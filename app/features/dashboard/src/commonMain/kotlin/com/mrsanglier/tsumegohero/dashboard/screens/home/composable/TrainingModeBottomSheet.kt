package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_brain
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_flash
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_puzzle
import com.mrsanglier.tsumegohero.app.coreui.resources.training_mode_classical
import com.mrsanglier.tsumegohero.app.coreui.resources.training_mode_difficult
import com.mrsanglier.tsumegohero.app.coreui.resources.training_mode_flash
import com.mrsanglier.tsumegohero.app.coreui.resources.training_mode_title
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.THModalBottomSheetState
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import org.jetbrains.compose.resources.StringResource

internal class TrainingModeBottomSheet(
    private val onModeSelected: (TrainingMode) -> Unit,
    override val onDismiss: () -> Unit,
) : THModalBottomSheetState {

    @Composable
    override fun Content(hideBottomSheet: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = THTheme.spacing.large),
        ) {
            THText(
                modifier = Modifier.padding(horizontal = THTheme.spacing.large),
                text = THString.training_mode_title.toTextSpec(),
                style = THTheme.typography.title100,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = THTheme.spacing.medium),
            ) {
                TrainingModeRow(
                    icon = THDrawable.ic_flash.toIconSpec(),
                    tintColor = THTheme.colors.contentTint,
                    title = THString.training_mode_flash.toTextSpec(),
                    description = "Tsumego facile. Travaille la rapidité et la mémorisation de forme & Tesuji.".toTextSpec(), // TODO: loco
                    onClick = {
                        onModeSelected(TrainingMode.Flash)
                        hideBottomSheet()
                    },
                )
                TrainingModeRow(
                    icon = THDrawable.ic_puzzle.toIconSpec(),
                    tintColor = THTheme.colors.detailGreen,
                    title = THString.training_mode_classical.toTextSpec(),
                    description = "Tsumego moyen. Travaille la précision de lecture.".toTextSpec(), // TODO: loco
                    onClick = {
                        onModeSelected(TrainingMode.Classical)
                        hideBottomSheet()
                    },
                )
                TrainingModeRow(
                    icon = THDrawable.ic_brain.toIconSpec(),
                    tintColor = THTheme.colors.detailBlue,
                    title = THString.training_mode_difficult.toTextSpec(),
                    description = "Tsumeogo difficile. Travaille la profondeur de lecture.".toTextSpec(), // TODO: loco
                    onClick = {
                        onModeSelected(TrainingMode.Difficult)
                        hideBottomSheet()
                    },
                )
            }
        }
    }
}

private fun TrainingMode.labelRes(): StringResource = when (this) {
    TrainingMode.Flash -> THString.training_mode_flash
    TrainingMode.Classical -> THString.training_mode_classical
    TrainingMode.Difficult -> THString.training_mode_difficult
}
