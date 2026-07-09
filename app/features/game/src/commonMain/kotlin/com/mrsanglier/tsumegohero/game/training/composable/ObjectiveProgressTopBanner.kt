package com.mrsanglier.tsumegohero.game.training.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.coreui.componants.cellobjective.CellObjective
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.componants.topbanner.THTopBannerState
import com.mrsanglier.tsumegohero.coreui.extension.composed
import com.mrsanglier.tsumegohero.coreui.extension.model.icon
import com.mrsanglier.tsumegohero.coreui.extension.model.label
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.utils.ComposeProvider
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode

@Composable
private fun ObjectiveProgressTopBanner(
    title: TextSpec,
    icon: IconSpec,
    attempts: ComposeProvider<List<Color>>,
    currentAttemptIndex: Int?,
    doneText: TextSpec,
    totalText: TextSpec,
    modifier: Modifier = Modifier,
) {
    CellObjective(
        title = title,
        icon = icon,
        trailingIcon = null,
        attempts = attempts,
        doneText = doneText,
        totalText = totalText,
        animateAttemptIndex = currentAttemptIndex,
        modifier = modifier
            .fillMaxWidth()
            .padding(THTheme.spacing.large),
    )
}

data class ObjectiveProgressTopBannerState(
    val attempts: List<Attempt.Result?>,
    val trainingMode: TrainingMode,
) : THTopBannerState {
    @Composable
    override fun Content() {
        ObjectiveProgressTopBanner(
            title = trainingMode.label(),
            icon = trainingMode.icon(),
            attempts = THTheme.composed {
                attempts.map { result ->
                    when (result) {
                        Attempt.Result.Success -> colors.detailGreen
                        Attempt.Result.Failure -> colors.contentCritical
                        Attempt.Result.Skip -> colors.contentSecondary
                        null -> colors.contentDisable
                    }
                }
            },
            doneText = attempts.count { it != null }.toString().toTextSpec(),
            totalText = "/${attempts.count()}".toTextSpec(),
            currentAttemptIndex = attempts.indexOfLast { it != null },
        )
    }
}
