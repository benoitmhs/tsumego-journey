package com.mrsanglier.tsumegohero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.mrsanglier.tsumegohero.dashboard.screens.home.HomeDestination
import com.mrsanglier.tsumegohero.dashboard.screens.home.HomeNavScope
import com.mrsanglier.tsumegohero.game.rankestimation.RankEstimationDestination
import com.mrsanglier.tsumegohero.game.rankestimation.RankEstimationNavScope
import com.mrsanglier.tsumegohero.game.rankestimation.result.RankEstimationResultDestination
import com.mrsanglier.tsumegohero.game.rankestimation.result.RankEstimationResultNavScope
import com.mrsanglier.tsumegohero.game.review.ReviewDestination
import com.mrsanglier.tsumegohero.game.review.ReviewNavScope
import com.mrsanglier.tsumegohero.game.training.TrainingDestination
import com.mrsanglier.tsumegohero.game.training.TrainingNavScope
import com.mrsanglier.tsumegohero.navigation.animation.THNavHost
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation(
    viewModel: MainNavigationViewModel = koinViewModel(),
) {
    val navController = rememberNavController()

    THNavHost(
        navController = navController,
        startDestination = HomeDestination,
    ) {
        HomeDestination.composable(
            this,
            HomeNavScope(
                navigateToTraining = { tsumegoId, trainingMode ->
                    navController.navigate(
                        TrainingDestination(
                            tsumegoId = tsumegoId,
                            trainingMode = trainingMode,
                        )
                    )
                },
                navigateToRankEstimation = { tsumegoId ->
                    navController.navigate(RankEstimationDestination(tsumegoId = tsumegoId))
                },
            ),
        )

        TrainingDestination.composable(
            navGraphBuilder = this,
            navScope = TrainingNavScope(
                navigateBack = navController::popBackStack,
                navigateToReview = { tsumegoId, boardConfig ->
                    navController.navigate(
                        ReviewDestination(
                            tsumegoId = tsumegoId,
                            rotation = boardConfig.rotation,
                            changeColor = boardConfig.changeColor,
                        )
                    )
                },
            )
        )

        RankEstimationDestination.composable(
            navGraphBuilder = this,
            navScope = RankEstimationNavScope(
                navigateBack = navController::popBackStack,
                navigateToReview = { tsumegoId, boardConfig ->
                    navController.navigate(
                        ReviewDestination(
                            tsumegoId = tsumegoId,
                            rotation = boardConfig.rotation,
                            changeColor = boardConfig.changeColor,
                        )
                    )
                },
                navigateToResult = { level ->
                    navController.navigate(
                        RankEstimationResultDestination(rank = level.classicalRank.rawValue)
                    ) {
                        // The estimation is over: close, "Super" or a back gesture all lead back home
                        popUpTo<RankEstimationDestination> { inclusive = true }
                    }
                },
            )
        )

        RankEstimationResultDestination.composable(
            navGraphBuilder = this,
            navScope = RankEstimationResultNavScope(
                close = navController::popBackStack,
            )
        )

        ReviewDestination.composable(
            navGraphBuilder = this,
            navScope = ReviewNavScope(
                navigateBack = navController::popBackStack,
            )
        )
    }
}
