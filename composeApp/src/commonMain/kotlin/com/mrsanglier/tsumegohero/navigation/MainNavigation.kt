package com.mrsanglier.tsumegohero.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.mrsanglier.tsumegohero.collection.detail.CollectionDetailDestination
import com.mrsanglier.tsumegohero.collection.detail.CollectionDetailNavScope
import com.mrsanglier.tsumegohero.collection.edition.TsumegoEditionDestination
import com.mrsanglier.tsumegohero.collection.edition.TsumegoEditionNavScope
import com.mrsanglier.tsumegohero.dashboard.navigation.DashboardDestination
import com.mrsanglier.tsumegohero.dashboard.navigation.DashboardNavScope
import com.mrsanglier.tsumegohero.game.promotion.LevelPromotionDestination
import com.mrsanglier.tsumegohero.game.promotion.LevelPromotionNavScope
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
        startDestination = DashboardDestination,
    ) {
        DashboardDestination.composable(
            navGraphBuilder = this,
            navScope = DashboardNavScope(
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
                navigateToCollection = { collectionId ->
                    navController.navigate(CollectionDetailDestination(collectionId = collectionId))
                },
            ),
        )

        CollectionDetailDestination.composable(
            navGraphBuilder = this,
            navScope = CollectionDetailNavScope(
                navigateBack = navController::popBackStack,
                navigateToTsumegoEdition = { collectionId ->
                    navController.navigate(TsumegoEditionDestination(collectionId = collectionId))
                },
            ),
        )

        TsumegoEditionDestination.composable(
            navGraphBuilder = this,
            navScope = TsumegoEditionNavScope(
                navigateBack = navController::popBackStack,
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
                navigateToPromotion = { rank ->
                    navController.navigate(LevelPromotionDestination(rank = rank.rawValue))
                },
            )
        )

        LevelPromotionDestination.composable(
            navGraphBuilder = this,
            navScope = LevelPromotionNavScope(
                close = navController::popBackStack,
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
