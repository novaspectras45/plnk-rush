package com.Zihmaho6.Tekloc.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.Zihmaho6.Tekloc.core.theme.AccentMagenta
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.TextFaint
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.theme.TextSecondary
import com.Zihmaho6.Tekloc.core.ui.ChipRow
import com.Zihmaho6.Tekloc.core.ui.HudFrame
import com.Zihmaho6.Tekloc.core.ui.PrimaryButton
import com.Zihmaho6.Tekloc.core.ui.ScreenBackdrop
import com.Zihmaho6.Tekloc.core.ui.SectionHeader
import com.Zihmaho6.Tekloc.presentation.dialog.ConfirmDialog

@Composable
fun SettingsPanel(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = ServiceLocator.viewModelFactory())
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    var confirming by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingsViewModel.refresh()
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
                title = stringResource(R.string.nav_settings),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack,
                starTotal = state.totalStars,
                starDescription = stringResource(R.string.cd_star)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SettingRow(
                        title = stringResource(R.string.setting_trajectory),
                        body = stringResource(R.string.setting_trajectory_body),
                        options = listOf(
                            stringResource(R.string.opt_full),
                            stringResource(R.string.opt_endpoint),
                            stringResource(R.string.opt_off)
                        ),
                        selectedIndex = state.settings.trajectory.ordinal,
                        accent = AccentCyan,
                        onSelect = { settingsViewModel.setTrajectory(it) }
                    )
                }
                item {
                    SettingRow(
                        title = stringResource(R.string.setting_speed),
                        body = stringResource(R.string.setting_speed_body),
                        options = listOf(
                            stringResource(R.string.opt_calm),
                            stringResource(R.string.opt_standard),
                            stringResource(R.string.opt_rush)
                        ),
                        selectedIndex = state.settings.dropSpeed.ordinal,
                        accent = AccentMagenta,
                        onSelect = { settingsViewModel.setDropSpeed(it) }
                    )
                }
                item {
                    SettingRow(
                        title = stringResource(R.string.setting_clock),
                        body = stringResource(R.string.setting_clock_body),
                        options = listOf(
                            stringResource(R.string.opt_on),
                            stringResource(R.string.opt_relaxed)
                        ),
                        selectedIndex = state.settings.clockMode.ordinal,
                        accent = AccentCyan,
                        onSelect = { settingsViewModel.setClockMode(it) }
                    )
                }
                item {
                    HudFrame(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.setting_progress),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.setting_progress_body),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PrimaryButton(
                            text = stringResource(R.string.cta_reset_progress),
                            onClick = { confirming = true },
                            height = 52.dp,
                            fontSize = 15
                        )
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.setting_footer),
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 1.5.sp,
                        color = TextFaint,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }

        if (confirming) {
            ConfirmDialog(
                title = stringResource(R.string.dialog_reset_title),
                body = stringResource(R.string.dialog_reset_body),
                confirmText = stringResource(R.string.cta_reset),
                dismissText = stringResource(R.string.cta_cancel),
                onConfirm = {
                    settingsViewModel.resetProgress()
                    confirming = false
                },
                onDismiss = { confirming = false }
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    body: String,
    options: List<String>,
    selectedIndex: Int,
    accent: Color,
    onSelect: (Int) -> Unit
) {
    HudFrame(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(14.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))
        ChipRow(
            options = options,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            accent = accent,
            chipHeight = 48.dp,
            selectedStateLabel = stringResource(R.string.state_selected)
        )
    }
}
