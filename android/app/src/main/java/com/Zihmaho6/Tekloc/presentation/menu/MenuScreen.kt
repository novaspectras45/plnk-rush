package com.Zihmaho6.Tekloc.presentation.menu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.Zihmaho6.Tekloc.core.theme.AccentMagenta
import com.Zihmaho6.Tekloc.core.theme.AccentMagentaDark
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.BorderIdle
import com.Zihmaho6.Tekloc.core.theme.BorderMagenta
import com.Zihmaho6.Tekloc.core.theme.MonoMedium
import com.Zihmaho6.Tekloc.core.theme.SurfaceNight
import com.Zihmaho6.Tekloc.core.theme.TextFaint
import com.Zihmaho6.Tekloc.core.theme.TextOnAccent
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.theme.TextSecondary
import com.Zihmaho6.Tekloc.core.ui.ScreenBackdrop
import com.Zihmaho6.Tekloc.core.ui.StatCard
import com.Zihmaho6.Tekloc.core.ui.cornerBrackets
import com.Zihmaho6.Tekloc.core.ui.formatCount

@Composable
fun MenuScreen(
    onPlay: (Int) -> Unit,
    onBoardSelect: () -> Unit,
    onDaily: () -> Unit,
    onCollection: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.viewModelFactory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    MenuContent(
        state = state,
        onPlay = { onPlay(state.currentStage) },
        onBoardSelect = onBoardSelect,
        onDaily = onDaily,
        onCollection = onCollection,
        onTutorial = onTutorial,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onBoardSelect: () -> Unit,
    onDaily: () -> Unit,
    onCollection: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    val showStats = state.totalStars > 0 && state.bestScore > 0
    ScreenBackdrop(
        overlay = listOf(
            BackgroundDeep.copy(alpha = 0.85f),
            Color(0xFF12121F).copy(alpha = 0.78f),
            BackgroundDeep.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.app_title),
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceNight.copy(alpha = 0.8f))
                        .border(1.dp, AccentAmber.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = stringResource(R.string.cd_star),
                        tint = AccentAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatCount(state.totalStars),
                        style = MonoMedium,
                        color = AccentAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.35f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlayTile(
                    onPlay = onPlay,
                    tint = Color(state.coreTint),
                    modifier = Modifier.weight(if (showStats) 2f else 3f)
                )
                if (showStats) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            label = stringResource(R.string.stat_stars),
                            value = state.totalStars,
                            accent = AccentAmber,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.stat_best),
                            value = state.bestScore,
                            accent = AccentCyan,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoTile(
                    label = stringResource(R.string.nav_board_select),
                    icon = painterResource(R.drawable.ic_target),
                    accent = AccentCyan,
                    onClick = onBoardSelect,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    label = stringResource(R.string.nav_daily_drop),
                    icon = painterResource(R.drawable.ic_clock),
                    accent = AccentMagenta,
                    onClick = onDaily,
                    status = if (state.dailyDone) {
                        stringResource(R.string.menu_daily_done, state.dailyStars)
                    } else {
                        stringResource(R.string.menu_daily_ready)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoTile(
                    label = stringResource(R.string.nav_collection),
                    icon = painterResource(R.drawable.ic_core),
                    accent = AccentCyan,
                    onClick = onCollection,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    label = stringResource(R.string.nav_tutorial),
                    icon = painterResource(R.drawable.ic_help),
                    accent = AccentAmber,
                    onClick = onTutorial,
                    highlight = !state.tutorialSeen,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    label = stringResource(R.string.nav_settings),
                    icon = painterResource(R.drawable.ic_sliders),
                    accent = AccentCyan,
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.menu_footer),
                fontSize = 10.sp,
                lineHeight = 14.sp,
                letterSpacing = 1.5.sp,
                color = TextFaint,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )
        }
    }
}

@Composable
private fun RowScope.PlayTile(
    onPlay: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 650f),
        label = "playPress"
    )
    val transition = rememberInfiniteTransition(label = "playTile")
    val bracket by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "playBracket"
    )
    val shape = RoundedCornerShape(20.dp)
    Button(
        onClick = onPlay,
        interactionSource = interaction,
        shape = shape,
        contentPadding = PaddingValues(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextOnAccent
        ),
        modifier = modifier
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(
                Brush.linearGradient(listOf(AccentMagenta, AccentMagentaDark)),
                shape
            )
            .border(1.dp, BorderMagenta, shape)
            .cornerBrackets(AccentCyan.copy(alpha = bracket), length = 20.dp, inset = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(tint.copy(alpha = 0.6f), BlendMode.SrcAtop),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.cta_play),
                fontSize = 24.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = TextOnAccent
            )
        }
    }
}

@Composable
private fun RowScope.BentoTile(
    label: String,
    icon: Painter,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    status: String = "",
    highlight: Boolean = false
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 650f),
        label = "tilePress"
    )
    val shape = RoundedCornerShape(16.dp)
    Button(
        onClick = onClick,
        interactionSource = interaction,
        shape = shape,
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceNight.copy(alpha = 0.88f),
            contentColor = TextPrimary
        ),
        modifier = modifier
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(1.dp, if (highlight) accent else BorderIdle, shape)
    ) {
        TileBody(label = label, icon = icon, accent = accent, status = status, highlight = highlight)
    }
}

@Composable
private fun TileBody(
    label: String,
    icon: Painter,
    accent: Color,
    status: String,
    highlight: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(24.dp)
            )
            if (highlight) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentCyan)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        if (status.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status,
                fontSize = 9.sp,
                lineHeight = 12.sp,
                letterSpacing = 1.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
