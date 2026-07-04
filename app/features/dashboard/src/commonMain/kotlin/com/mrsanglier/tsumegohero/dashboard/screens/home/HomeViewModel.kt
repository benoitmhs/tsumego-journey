package com.mrsanglier.tsumegohero.dashboard.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrsanglier.tsumegohero.core.extension.handleResult
import com.mrsanglier.tsumegohero.coreui.componants.loading.LoadingManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.THSnackbarState
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showDone
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.showError
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveDailyStreakUseCase
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveProgressDataUseCase
import com.mrsanglier.tsumegohero.dashboardgame.usecase.ObserveUserUseCase
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.game.usecase.GetNextTsumegoIdUseCase
import com.mrsanglier.tsumegohero.game.usecase.ImportTsumegoUseCase
import com.mrsanglier.tsumegohero.rankestimation.usecase.GetNextRankEstimationTsumegoUseCase
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
) : ViewModel() {

    internal val uiState: StateFlow<HomeViewModelState> = combine(
        observeProgressDataUseCase(),
        observeDailyStreakUseCase(),
        observeUserUseCase().map { it?.level == null }.distinctUntilChanged(),
    ) { progressData, dailyStreak, levelIsNull ->
        HomeViewModelState(
            dailyStreakData = dailyStreak.data?.toCellData() ?: PlaceHolder.DailyStreak,
            rankProgressBarData = progressData?.getRankProgressBarData() ?: PlaceHolder.RankProgressBar,
            problemStreakData = progressData?.getProblemStreakData() ?: PlaceHolder.ProblemStreak,
            mainAction = getMainAction(levelIsNull),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        HomeViewModelState(),
    )

    internal val navEvent = MutableStateFlow<NavEvent?>(null)

    internal fun openRankBottomSheet() {
        viewModelScope.launch {
            getNextRankEstimationTsumegoUseCase().handleResult(
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
        viewModelScope.launch {
            getNextTsumegoIdUseCase().handleResult(
                onSuccess = { tsumegoId ->
                    navEvent.value = NavEvent.Training(tsumegoId)
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
        data class Training(val tsumegoId: String) : NavEvent
        data class RankEstimation(val tsumegoId: String) : NavEvent
    }

}
