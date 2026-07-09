package com.mrsanglier.tsumegohero.game.rankestimation.result

import androidx.compose.runtime.Composable
import com.mrsanglier.tsumegohero.app.coreui.resources.rankEstimation_result_congrats
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.game.game.composable.RankCongratsScreen

@Composable
internal fun RankEstimationResultRoute(
    args: RankEstimationResultDestination,
    navScope: RankEstimationResultNavScope,
) {
    RankCongratsScreen(
        congratsText = THString.rankEstimation_result_congrats.toTextSpec(),
        rank = args.rank,
        onClose = navScope.close,
    )
}
