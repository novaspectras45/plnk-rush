package com.Zihmaho6.Tekloc.presentation.daily

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.Zihmaho6.Tekloc.core.theme.AccentAmber
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.MonoMedium
import com.Zihmaho6.Tekloc.core.theme.TextMuted
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.theme.TextSecondary
import com.Zihmaho6.Tekloc.core.ui.BoardCanvas
import com.Zihmaho6.Tekloc.core.ui.HudFrame
import com.Zihmaho6.Tekloc.core.ui.PrimaryButton
import com.Zihmaho6.Tekloc.core.ui.ScreenBackdrop
import com.Zihmaho6.Tekloc.core.ui.SectionHeader
import com.Zihmaho6.Tekloc.domain.model.Placement

@Composable
fun DailyChallengePanel(
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    val dailyViewModel: DailyChallengeViewModel =
        viewModel(factory = ServiceLocator.viewModelFactory())
    val state by dailyViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        dailyViewModel.refresh()
    }

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
                title = stringResource(R.string.nav_daily_drop),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack,
                starTotal = state.totalStars,
                starDescription = stringResource(R.string.cd_star)
            )
            Spacer(modifier = Modifier.height(12.dp))
            HudFrame(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.daily_day, state.day),
                    style = MonoMedium,
                    color = AccentCyan,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val preview = state.preview
                    if (preview != null) {
                        BoardCanvas(
                            layout = preview,
                            placement = Placement(),
                            cell = 11.dp,
                            interactive = false
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.daily_par),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        letterSpacing = 1.5.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = stringResource(R.string.daily_reward),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        letterSpacing = 1.5.sp,
                        color = AccentAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (state.done) {
                    stringResource(R.string.daily_done_message)
                } else {
                    stringResource(R.string.daily_open_message)
                },
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 1.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.done) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.daily_solved, state.lastStars),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = if (state.done) {
                    stringResource(R.string.cta_back_tomorrow)
                } else {
                    stringResource(R.string.nav_daily_drop)
                },
                onClick = onStart,
                enabled = !state.done,
                height = 60.dp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
