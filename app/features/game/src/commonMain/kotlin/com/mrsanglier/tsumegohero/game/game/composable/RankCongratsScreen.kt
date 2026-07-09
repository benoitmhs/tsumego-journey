package com.mrsanglier.tsumegohero.game.game.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrsanglier.tsumegohero.app.coreui.resources.rankEstimation_result_button
import com.mrsanglier.tsumegohero.coreui.componants.button.THButton
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import dev.chrisbanes.haze.HazeState

// Congratulation screen showing a rank in very large: a sentence, the rank, a "Super" button
@Composable
internal fun RankCongratsScreen(
    congratsText: TextSpec,
    rank: String,
    onClose: () -> Unit,
) {
    val topBarHazeState = remember { HazeState() }

    THScreen(
        topBar = {
            THTopBar(
                title = null,
                hazeState = topBarHazeState,
                elevation = 0.dp,
                navAction = TopBarAction.close(onClose),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = THTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            THText(
                text = congratsText,
                style = THTheme.typography.title100,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            THText(
                text = rank.lowercase().toTextSpec(),
                style = THTheme.typography.header200.copy(
                    fontSize = 96.sp,
                    lineHeight = 120.sp,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = THTheme.spacing.xlarge),
            )

            Spacer(modifier = Modifier.weight(1f))

            THButton(
                text = THString.rankEstimation_result_button.toTextSpec(),
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = THTheme.spacing.large),
            )
        }
    }
}
