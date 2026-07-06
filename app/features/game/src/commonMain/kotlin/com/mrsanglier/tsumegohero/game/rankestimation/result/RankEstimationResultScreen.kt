package com.mrsanglier.tsumegohero.game.rankestimation.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrsanglier.tsumegohero.app.coreui.resources.Res
import com.mrsanglier.tsumegohero.app.coreui.resources.rankEstimation_result_button
import com.mrsanglier.tsumegohero.app.coreui.resources.rankEstimation_result_congrats
import com.mrsanglier.tsumegohero.coreui.componants.button.THButton
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.topbar.THTopBar
import com.mrsanglier.tsumegohero.coreui.componants.topbar.TopBarAction
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import dev.chrisbanes.haze.HazeState
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
internal fun RankEstimationResultRoute(
    args: RankEstimationResultDestination,
    navScope: RankEstimationResultNavScope,
) {
    RankEstimationResultScreen(
        rank = args.rank,
        onClose = navScope.close,
    )
}

@Composable
private fun RankEstimationResultScreen(
    rank: String,
    onClose: () -> Unit,
) {
    val topBarHazeState = remember { HazeState() }
    val composition by rememberLottieComposition {
        LottieCompositionSpec.DotLottie(
            Res.readBytes("files/confetti.lottie")
        )
    }
    val animationProgress by animateLottieCompositionAsState(composition)


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
                text = THString.rankEstimation_result_congrats.toTextSpec(),
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

    if (animationProgress < 1f) {
        Image(
            painter = rememberLottiePainter(composition),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
