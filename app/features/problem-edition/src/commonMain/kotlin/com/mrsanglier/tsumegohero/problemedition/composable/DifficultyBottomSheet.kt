package com.mrsanglier.tsumegohero.problemedition.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_info_difficultyLabel
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_info_noDifficulty
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.button.THButton
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.THModalBottomSheetState
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.data.model.game.Rank

private val RankListMaxHeight = 420.dp

internal class DifficultyBottomSheet(
    private val onRankSelected: (Rank?) -> Unit,
    override val onDismiss: () -> Unit,
) : THModalBottomSheetState {

    @Composable
    override fun Content(hideBottomSheet: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(THTheme.spacing.large),
        ) {
            THText(
                text = THString.tsumegoEdition_info_difficultyLabel.toTextSpec(),
                style = THTheme.typography.title100,
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = RankListMaxHeight)
                    .padding(vertical = THTheme.spacing.medium),
            ) {
                items(
                    items = Rank.entries,
                    key = { it.rawValue },
                    contentType = { "rank.item" },
                ) { rank ->
                    THText(
                        text = rank.rawValue.lowercase().toTextSpec(),
                        style = THTheme.typography.label200,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .thClickable(onClick = { onRankSelected(rank) })
                            .padding(THTheme.spacing.medium),
                    )
                }
            }

            THButton(
                text = THString.tsumegoEdition_info_noDifficulty.toTextSpec(),
                onClick = { onRankSelected(null) },
                style = ButtonStyle.Secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = THTheme.spacing.large),
            )
        }
    }
}
