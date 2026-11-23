package com.Zihmaho6.Tekloc.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.core.di.ServiceLocator
import com.Zihmaho6.Tekloc.core.navigation.Screen
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.presentation.boardselect.BoardSelectPanel
import com.Zihmaho6.Tekloc.presentation.collection.CollectionPanel
import com.Zihmaho6.Tekloc.presentation.daily.DailyChallengePanel
import com.Zihmaho6.Tekloc.presentation.game.GameScreen
import com.Zihmaho6.Tekloc.presentation.menu.MenuScreen
import com.Zihmaho6.Tekloc.presentation.result.ResultScreen
import com.Zihmaho6.Tekloc.presentation.settings.SettingsPanel
import com.Zihmaho6.Tekloc.presentation.splash.SplashScreen
import com.Zihmaho6.Tekloc.presentation.tutorial.TutorialPanel

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.viewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()

    BackHandler(enabled = screen !is Screen.Menu) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                if (targetState is Screen.Game || initialState is Screen.Game) {
                    (slideInHorizontally(tween(300)) { it / 4 } + fadeIn(tween(300)))
                        .togetherWith(fadeOut(tween(200)))
                } else {
                    fadeIn(tween(320)).togetherWith(fadeOut(tween(220)))
                }
            },
            label = "screenTransition"
        ) { current ->
            when (current) {
                is Screen.Splash -> SplashScreen(
                    onFinished = { appViewModel.openMenu() }
                )

                is Screen.Menu -> MenuScreen(
                    onPlay = { stage ->
                        appViewModel.navigate(Screen.Game(stage, false, 0, false))
                    },
                    onBoardSelect = { appViewModel.navigate(Screen.BoardSelect) },
                    onDaily = { appViewModel.navigate(Screen.Daily) },
                    onCollection = { appViewModel.navigate(Screen.Collection) },
                    onTutorial = { appViewModel.navigate(Screen.Tutorial) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.BoardSelect -> BoardSelectPanel(
                    onOpenStage = { stage ->
                        appViewModel.navigate(Screen.Game(stage, false, 0, false))
                    },
                    onBack = { appViewModel.back() }
                )

                is Screen.Collection -> CollectionPanel(
                    onBack = { appViewModel.back() }
                )

                is Screen.Daily -> DailyChallengePanel(
                    onStart = {
                        appViewModel.navigate(
                            Screen.Game(GameConfig.DAILY_STAGE_INDEX, true, 0, false)
                        )
                    },
                    onBack = { appViewModel.back() }
                )

                is Screen.Tutorial -> TutorialPanel(
                    onBack = { appViewModel.back() }
                )

                is Screen.Settings -> SettingsPanel(
                    onBack = { appViewModel.back() }
                )

                is Screen.Game -> GameScreen(
                    stageIndex = current.stageIndex,
                    daily = current.daily,
                    attempt = current.attempt,
                    replan = current.replan,
                    onRoundFinished = { summary ->
                        appViewModel.replaceGameWith(Screen.GameOver(summary))
                    },
                    onMenu = { appViewModel.backToMenu() }
                )

                is Screen.GameOver -> ResultScreen(
                    summary = current.summary,
                    onAgain = { stage ->
                        appViewModel.replaceTop(
                            Screen.Game(
                                stage,
                                current.summary.daily,
                                current.summary.attempt + 1,
                                false
                            )
                        )
                    },
                    onReplan = {
                        appViewModel.replaceTop(
                            Screen.Game(
                                current.summary.stageIndex,
                                current.summary.daily,
                                current.summary.attempt,
                                true
                            )
                        )
                    },
                    onBoardSelect = { appViewModel.navigate(Screen.BoardSelect) },
                    onMenu = { appViewModel.backToMenu() }
                )
            }
        }
    }
}
