package com.mrsanglier.tsumegohero.game.game.section

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

@Composable
fun GameActionsSection(
    gameActionState: GameActionState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(THTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
    ) {
        gameActionState.actionButton?.Content(
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.large),
        ) {
            gameActionState.skipButton?.Content(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize(),
            )
            gameActionState.validateButton.Content(
                modifier = Modifier
                    .weight(1f)
                    .animateContentSize(),
            )
        }
    }

}

@Immutable
data class GameActionState(
    val validateButton: THButtonState,
    val skipButton: THButtonState?,
    val actionButton: THButtonState?,
)
