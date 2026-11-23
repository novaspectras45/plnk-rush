package com.Zihmaho6.Tekloc.presentation.tutorial

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Zihmaho6.Tekloc.R
import com.Zihmaho6.Tekloc.core.di.ServiceLocator
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.MonoSmall
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.theme.TextSecondary
import com.Zihmaho6.Tekloc.core.ui.BoardCanvas
import com.Zihmaho6.Tekloc.core.ui.HudFrame
import com.Zihmaho6.Tekloc.core.ui.PrimaryButton
import com.Zihmaho6.Tekloc.core.ui.ScreenBackdrop
import com.Zihmaho6.Tekloc.core.ui.SecondaryButton
import com.Zihmaho6.Tekloc.core.ui.SectionHeader
import com.Zihmaho6.Tekloc.domain.model.Placement

private const val TUTORIAL_STEPS = 4

@Composable
fun TutorialPanel(onBack: () -> Unit) {
    val tutorialViewModel: TutorialViewModel = viewModel(factory = ServiceLocator.viewModelFactory())
    val state by tutorialViewModel.state.collectAsStateWithLifecycle()
    var step by rememberSaveable { mutableIntStateOf(0) }

    ScreenBackdrop(
        overlay = listOf(
            BackgroundDeep.copy(alpha = 0.90f),
            BackgroundDeep.copy(alpha = 0.95f),
            BackgroundDeep.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.nav_tutorial),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tutorial_step, step + 1, TUTORIAL_STEPS),
                style = MonoSmall,
                color = AccentCyan,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    fadeIn(tween(260)).togetherWith(fadeOut(tween(180)))
                },
                label = "tutorialStep"
            ) { current ->
                HudFrame(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = stepTitle(current),
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stepBody(current),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val diagrams = state.diagrams
                        if (diagrams.isNotEmpty()) {
                            BoardCanvas(
                                layout = diagrams[current % diagrams.size],
                                placement = Placement(),
                                cell = 13.dp,
                                laneIndex = current % 3,
                                interactive = false
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.cta_back_step),
                    onClick = { if (step > 0) step -= 1 },
                    enabled = step > 0,
                    modifier = Modifier.weight(1f)
                )
                if (step < TUTORIAL_STEPS - 1) {
                    SecondaryButton(
                        text = stringResource(R.string.cta_next_step),
                        onClick = { step += 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (step == TUTORIAL_STEPS - 1) {
                Spacer(modifier = Modifier.height(10.dp))
                PrimaryButton(
                    text = stringResource(R.string.cta_got_it),
                    onClick = {
                        tutorialViewModel.markSeen()
                        onBack()
                    },
                    height = 56.dp,
                    fontSize = 16
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun stepTitle(step: Int): String = when (step) {
    0 -> stringResource(R.string.tutorial_one_title)
    1 -> stringResource(R.string.tutorial_two_title)
    2 -> stringResource(R.string.tutorial_three_title)
    else -> stringResource(R.string.tutorial_four_title)
}

@Composable
private fun stepBody(step: Int): String = when (step) {
    0 -> stringResource(R.string.tutorial_one_body)
    1 -> stringResource(R.string.tutorial_two_body)
    2 -> stringResource(R.string.tutorial_three_body)
    else -> stringResource(R.string.tutorial_four_body)
}
