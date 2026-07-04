package com.mrsanglier.tsumegohero.navigation.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry
import com.mrsanglier.tsumegohero.domain.choosepassword.ChoosePasswordDestination
import com.mrsanglier.tsumegohero.domain.choosepseudo.ChoosePseudoDestination
import com.mrsanglier.tsumegohero.domain.connection.ConnectionDestination
import com.mrsanglier.tsumegohero.coreui.animation.fadeInEnterTransition
import com.mrsanglier.tsumegohero.coreui.animation.fadeInPopEnterTransition
import com.mrsanglier.tsumegohero.coreui.animation.fadeOutExitTransition
import com.mrsanglier.tsumegohero.coreui.animation.fadeOutPopExitTransition
import com.mrsanglier.tsumegohero.coreui.animation.slideHorizontalEnterTransition
import com.mrsanglier.tsumegohero.coreui.animation.slideHorizontalExitTransition
import com.mrsanglier.tsumegohero.coreui.animation.slideHorizontalPopEnterTransition
import com.mrsanglier.tsumegohero.coreui.animation.slideHorizontalPopExitTransition
import com.mrsanglier.tsumegohero.coreui.animation.slideVerticalEnterTransition
import com.mrsanglier.tsumegohero.coreui.animation.slideVerticalExitTransition
import com.mrsanglier.tsumegohero.coreui.animation.slideVerticalPopEnterTransition
import com.mrsanglier.tsumegohero.coreui.animation.slideVerticalPopExitTransition
import com.mrsanglier.tsumegohero.dashboard.navigation.DashboardDestination
import com.mrsanglier.tsumegohero.game.rankestimation.RankEstimationDestination
import com.mrsanglier.tsumegohero.game.review.ReviewDestination
import com.mrsanglier.tsumegohero.game.training.TrainingDestination
import kotlin.jvm.JvmSuppressWildcards

enum class Transition(
    val enter: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition),
    val exit: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition),
    val popEnter: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition),
    val popExitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition),
) {

    Fading(
        enter = fadeInEnterTransition,
        exit = fadeOutExitTransition,
        popEnter = fadeInPopEnterTransition,
        popExitTransition = fadeOutPopExitTransition,
    ),
    Vertical(
        enter = slideVerticalEnterTransition,
        exit = slideVerticalExitTransition,
        popEnter = slideVerticalPopEnterTransition,
        popExitTransition = slideVerticalPopExitTransition,
    ),
    Horizontal(
        enter = slideHorizontalEnterTransition,
        exit = slideHorizontalExitTransition,
        popEnter = slideHorizontalPopEnterTransition,
        popExitTransition = slideHorizontalPopExitTransition,
    );

    companion object {
        fun fromDestination(route: String?): Transition = when {
            route == null -> Fading
            horizontalDestinations.any { route.contains(it) } -> Horizontal
            verticalDestinations.any { route.contains(it) } -> Vertical
            fadingDestinations.any { route.contains(it) } -> Fading
            else -> Fading
        }
    }
}

private val fadingDestinations: Set<String> = setOfNotNull(
    ConnectionDestination::class.simpleName,
    DashboardDestination::class.simpleName,
    ReviewDestination::class.simpleName,
)

private val horizontalDestinations: Set<String> = setOfNotNull(
    ChoosePasswordDestination::class.simpleName,
    ChoosePseudoDestination::class.simpleName,
    TrainingDestination::class.simpleName,
    RankEstimationDestination::class.simpleName,
)

private val verticalDestinations: Set<String> = setOfNotNull(
)
