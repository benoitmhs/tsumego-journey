package com.mrsanglier.tsumegohero.dashboard.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.coreui.componants.bottombar.THBottomBar
import com.mrsanglier.tsumegohero.coreui.componants.screen.LocalPiScreenPadding
import com.mrsanglier.tsumegohero.coreui.componants.screen.THScreen
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.extension.rememberBottomBarElevation
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.navigation.safeNavigation
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.DailyStreakCellData
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.Header
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.HomeBottomBar
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.RankProgressBarData
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    navScope: HomeNavScope,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    safeNavigation(viewModel.navEvent, viewModel::consumeNavigation) { event ->
        when (event) {
            is HomeViewModel.NavEvent.Training -> navScope.navigateToTraining(event.tsumegoId)
            is HomeViewModel.NavEvent.RankEstimation -> navScope.navigateToRankEstimation(event.tsumegoId)
        }
    }

    HomeScreen(
        uiState = uiState,
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeViewModelState,
) {
    val scrollState = rememberScrollState()
    val bottomBarHazeState = remember { HazeState() }
    val bottomBarElevation by rememberBottomBarElevation(scrollState)

    THScreen(
        modifier = Modifier.padding(
            bottom = LocalPiScreenPadding.current.calculateBottomPadding(),
        ),
        bottomBar = {
            HomeBottomBar(
                hazeState = bottomBarHazeState,
                elevation = bottomBarElevation,
                primaryButton = uiState.mainAction,
                streakProgressBar = uiState.problemStreakData,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .hazeSource(bottomBarHazeState)
                .padding(innerPadding),
        ) {
            // Header
            TopHeader(uiState.dailyStreakData)

            // Rank & progress
            ProgressSection(uiState.rankProgressBarData)

            // Statistics
            StatsSection()

        }
    }
}

@Composable
private fun TopHeader(dailyStreakData: DailyStreakCellData) {
    Box(
        modifier = Modifier
            .padding(
                vertical = THTheme.spacing.medium,
                horizontal = THTheme.spacing.small,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        dailyStreakData.Composable()

        THText(
            "Tsumego".toTextSpec(),
            style = THTheme.typography.header200,
            color = THTheme.colors.content,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ColumnScope.ProgressSection(rankProgressBarData: RankProgressBarData) {
    Header(text = "Progress".toTextSpec()) // TODO: loco
    rankProgressBarData.Composable(modifier = Modifier.align(Alignment.CenterHorizontally))
}

@Composable
private fun ColumnScope.StatsSection() {
    Header(
        text = "Statistics".toTextSpec(),
        modifier = Modifier.padding(top = THTheme.spacing.xlarge),
    ) // TODO: loco
}
