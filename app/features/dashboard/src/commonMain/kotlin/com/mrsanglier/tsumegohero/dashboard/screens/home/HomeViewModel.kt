package com.mrsanglier.tsumegohero.dashboard.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.coreui.componants.loading.LoadingManager
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.THModalBottomSheetState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.THSnackbarState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showDone
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.DailyObjectiveCard
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.DailyObjectiveCardState
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveDailyStreakUseCase
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveProgressDataUseCase
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveUserUseCase
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.DeclareRankBottomSheet
import com.mrsanglier.tsumegohero.dashboard.screens.home.composable.TrainingModeBottomSheet
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveDailyObjectiveUseCase
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import com.mrsanglier.tsumegohero.game.usecase.GetNextTsumegoIdUseCase
import com.mrsanglier.tsumegohero.game.usecase.ImportTsumegoUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetNextRankEstimationTsumegoUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.ObserveRankEstimationInProgressUseCase
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val importTsumegoUseCase: ImportTsumegoUseCase,
    private val loadingManager: LoadingManager,
    private val snackbarManager: SnackbarManager,
    private val getNextTsumegoIdUseCase: GetNextTsumegoIdUseCase,
    private val getNextRankEstimationTsumegoUseCase: GetNextRankEstimationTsumegoUseCase,
    observeUserUseCase: ObserveUserUseCase,
    observeProgressDataUseCase: ObserveProgressDataUseCase,
    observeDailyStreakUseCase: ObserveDailyStreakUseCase,
    observeRankEstimationInProgressUseCase: ObserveRankEstimationInProgressUseCase,
    observeDailyObjectiveUseCase: ObserveDailyObjectiveUseCase,
) : ViewModel() {

    internal val uiState: StateFlow<HomeViewModelState> = combine(
        observeProgressDataUseCase(),
        observeDailyStreakUseCase(),
        observeUserUseCase().map { it?.level == null }.distinctUntilChanged(),
        observeRankEstimationInProgressUseCase(),
        observeDailyObjectiveUseCase(),
    ) { progressData, dailyStreak, levelIsNull, estimationInProgress, dailyObjectives ->
        HomeViewModelState(
            dailyStreakData = dailyStreak.data?.toCellData() ?: PlaceHolder.DailyStreak,
            rankProgressBarData = progressData?.getRankProgressBarData() ?: PlaceHolder.RankProgressBar,
            mainAction = getMainAction(
                rankIsNull = levelIsNull,
                estimationInProgress = estimationInProgress,
            ),
            dailyObjectiveCards = listOf(
                DailyObjectiveCardState(
                    attempts = dailyObjectives.flashProblemResults.map { it?.result },
                    trainingMode = TrainingMode.Flash,
                    onClick = { startTraining(TrainingMode.Flash) }
                ),
                DailyObjectiveCardState(
                    attempts = dailyObjectives.classicalProblemResults.map { it?.result },
                    trainingMode = TrainingMode.Classical,
                    onClick = { startTraining(TrainingMode.Classical) }

                ),
                DailyObjectiveCardState(
                    attempts = dailyObjectives.difficultProblemResults.map { it?.result },
                    trainingMode = TrainingMode.Difficult,
                    onClick = { startTraining(TrainingMode.Difficult) }
                ),
            ),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        HomeViewModelState(),
    )

    internal val navEvent = MutableStateFlow<NavEvent?>(null)

    private val _bottomSheet = MutableStateFlow<THModalBottomSheetState?>(null)
    internal val bottomSheet: StateFlow<THModalBottomSheetState?> = _bottomSheet.asStateFlow()

    internal fun openRankBottomSheet() {
        _bottomSheet.value = DeclareRankBottomSheet(
            onRankSelected = ::startRankEstimation,
            onDismiss = { _bottomSheet.value = null },
        )
    }

    internal fun continueRankEstimation() {
        startRankEstimation(declaredRank = null)
    }

    private fun startRankEstimation(declaredRank: Rank?) {
        _bottomSheet.value = null
        viewModelScope.launch {
            getNextRankEstimationTsumegoUseCase(declaredRank).handleResult(
                onSuccess = { tsumegoId ->
                    if (tsumegoId != null) {
                        navEvent.value = NavEvent.RankEstimation(tsumegoId)
                    }
                },
                onError = snackbarManager::showError,
            )
        }
    }

    internal fun startTsumego() {
        _bottomSheet.value = TrainingModeBottomSheet(
            onModeSelected = ::startTraining,
            onDismiss = { _bottomSheet.value = null },
        )
    }

    private fun startTraining(trainingMode: TrainingMode) {
        _bottomSheet.value = null
        viewModelScope.launch {
            getNextTsumegoIdUseCase(trainingMode).handleResult(
                onSuccess = { tsumegoId ->
                    navEvent.value = NavEvent.Training(tsumegoId, trainingMode)
                },
                onError = snackbarManager::showError,
            )
        }
    }

    internal fun consumeNavigation() {
        navEvent.value = null
    }

    internal fun displayCountByRank() {
        viewModelScope.launch {
            importTsumegoUseCase.countRank().forEach { (rank, count) ->
                println("$rank: $count")
            }
        }
    }

    internal fun saveTsumegoFiles(files: List<PlatformFile>) {
        viewModelScope.launch {
            loadingManager.withLoading {
                val filesMap = files.associate {
                    it.nameWithoutExtension to it.readString()
                }

                val fileNamesError = mutableListOf<String>()

                filesMap.forEach { (fileName, data) ->
                    importTsumegoUseCase(
                        fileName = fileName,
                        sgfData = data,
                    ).handleResult(
                        onSuccess = {},
                        onError = {
                            fileNamesError.add(fileName)
                        },
                    )
                }

                if (fileNamesError.isNotEmpty()) {
                    snackbarManager.showSnackBar(
                        THSnackbarState.Error(
                            text = "Sgf format not supported for file ${fileNamesError.joinToString()}".toTextSpec(),
                        )
                    )
                } else {
                    snackbarManager.showDone("Tsumego imported".toTextSpec())
                }
            }
        }
    }

    internal sealed interface NavEvent {
        data class Training(val tsumegoId: String, val trainingMode: TrainingMode) : NavEvent
        data class RankEstimation(val tsumegoId: String) : NavEvent
    }

}
