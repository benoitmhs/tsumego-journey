package com.mrsanglier.tsumegohero.problemedition.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_grade
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_info_difficultyLabel
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_info_notesLabel
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_info_titleLabel
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionViewModelState
import com.mrsanglier.tsumegohero.coreui.componants.pickers.PickerCell
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.textfield.THTextField
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

/** Step 3: optional title, difficulty and notes. */
@Composable
internal fun InfoStepContent(
    uiState: ProblemEditionViewModelState,
    onTitleChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onClickDifficulty: () -> Unit,
    onClearDifficulty: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
    ) {
        THTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            description = THString.tsumegoEdition_info_titleLabel.toTextSpec(),
            title = THString.tsumegoEdition_info_titleLabel.toTextSpec(),
            maxLines = 1,
        )
        Column(verticalArrangement = Arrangement.spacedBy(THTheme.spacing.small)) {
            THText(
                text = THString.tsumegoEdition_info_difficultyLabel.toTextSpec(),
                style = THTheme.typography.title100,
            )
            PickerCell(
                label = uiState.difficulty ?: "-".toTextSpec(),
                leadingIcon = THDrawable.ic_grade.toIconSpec(),
                onClick = onClickDifficulty,
                onClose = onClearDifficulty.takeIf { uiState.difficulty != null },
            )
        }
        THTextField(
            value = uiState.notes,
            onValueChange = onNotesChange,
            description = THString.tsumegoEdition_info_notesLabel.toTextSpec(),
            title = THString.tsumegoEdition_info_notesLabel.toTextSpec(),
            minLines = NotesMinLines,
        )
    }
}

private const val NotesMinLines: Int = 4
