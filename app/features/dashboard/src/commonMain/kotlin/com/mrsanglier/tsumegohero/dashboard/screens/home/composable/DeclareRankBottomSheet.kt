package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

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
import com.mrsanglier.tsumegohero.app.coreui.resources.rankEstimation_declareRank_title
import com.mrsanglier.tsumegohero.app.coreui.resources.rankEstimation_declareRank_unknown
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

internal class DeclareRankBottomSheet(
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
                text = THString.rankEstimation_declareRank_title.toTextSpec(),
                style = THTheme.typography.title100,
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = RankListMaxHeight)
                    .padding(vertical = THTheme.spacing.medium),
            ) {
                items(
                    key = { it.rawValue },
                    items = fullRanks,
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
                text = THString.rankEstimation_declareRank_unknown.toTextSpec(),
                onClick = { onRankSelected(null) },
                style = ButtonStyle.Secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = THTheme.spacing.large),
            )
        }
    }

    companion object {
        // Half ranks ("15K+") are irrelevant for an approximate self-declared level
        private val fullRanks: List<Rank> = Rank.entries.filterNot { it.rawValue.endsWith("+") }
    }
}
