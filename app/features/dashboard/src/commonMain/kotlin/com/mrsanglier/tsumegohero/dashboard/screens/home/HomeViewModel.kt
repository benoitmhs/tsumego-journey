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
import com.mrsanglier.tsumegohero.dashboardgame.usecase.UpdateUserRankUseCase
import com.mrsanglier.tsumegohero.data.model.game.Rank
import com.mrsanglier.tsumegohero.game.usecase.ImportTsumegoUseCase
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readString
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
    private val updateUserRankUseCase: UpdateUserRankUseCase,
    observeUserUseCase: ObserveUserUseCase,
    observeProgressDataUseCase: ObserveProgressDataUseCase,
    observeDailyStreakUseCase: ObserveDailyStreakUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeViewModelState> = combine(
        observeProgressDataUseCase(),
        observeDailyStreakUseCase(),
        observeUserUseCase().map { it?.rank == null }.distinctUntilChanged(),
    ) { progressData, dailyStreak, rankIsNull ->
        HomeViewModelState(
            dailyStreakData = dailyStreak.data?.toCellData() ?: PlaceHolder.DailyStreak,
            rankProgressBarData = progressData?.getRankProgressBarData() ?: PlaceHolder.RankProgressBar,
            problemStreakData = progressData?.getProblemStreakData() ?: PlaceHolder.ProblemStreak,
            mainAction = getMainAction(rankIsNull),
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        HomeViewModelState(),
    )

    internal fun openRankBottomSheet() {
        viewModelScope.launch {
            updateUserRankUseCase(Rank.`5K`).handleResult(
                onSuccess = { snackbarManager.showDone("rank update".toTextSpec()) },
                onError = snackbarManager::showError
            )
        }
    }

    internal fun startTsumego() {

    }

    fun displayCountByRank() {
        viewModelScope.launch {
            importTsumegoUseCase.countRank().forEach { (rank, count) ->
                println("$rank: $count")
            }
        }
    }

    fun saveTsumegoFiles(files: List<PlatformFile>) {
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
}
