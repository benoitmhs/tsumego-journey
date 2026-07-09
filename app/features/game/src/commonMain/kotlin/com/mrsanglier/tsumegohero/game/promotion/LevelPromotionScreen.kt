package com.mrsanglier.tsumegohero.game.promotion

import androidx.compose.runtime.Composable
import com.mrsanglier.tsumegohero.app.coreui.resources.training_promotion_congrats
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.game.game.composable.RankCongratsScreen

@Composable
internal fun LevelPromotionRoute(
    args: LevelPromotionDestination,
    navScope: LevelPromotionNavScope,
) {
    RankCongratsScreen(
        congratsText = THString.training_promotion_congrats.toTextSpec(),
        rank = args.rank,
        onClose = navScope.close,
    )
}
