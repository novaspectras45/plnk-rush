package com.Zihmaho6.Tekloc.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Zihmaho6.Tekloc.R
import com.Zihmaho6.Tekloc.core.di.ServiceLocator
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.AccentMagenta
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.SurfaceRaised
import com.Zihmaho6.Tekloc.core.theme.TextMuted
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.ui.cornerBrackets

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = ServiceLocator.viewModelFactory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    val transition = rememberInfiniteTransition(label = "splashLoops")
    val bracketAlpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bracketAlpha"
    )
    val railProgress by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.82f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "railProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0f to Color(0xFF16213E),
                        0.55f to Color(0xFF0D0D18),
                        1f to Color(0xFF060609)
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.38f),
                    radius = size.maxDimension * 0.82f
                )
            )
            val pitch = 34.dp.toPx()
            val dot = 2.dp.toPx()
            val startX = (size.width - pitch * 6) / 2f
            val startY = size.height * 0.1f
            for (row in 0 until 9) {
                for (column in 0 until 7) {
                    drawCircle(
                        color = AccentCyan.copy(alpha = 0.14f),
                        radius = dot,
                        center = Offset(startX + pitch * column, startY + pitch * row)
                    )
                }
            }
            val leftRail = size.width * 0.22f
            val rightRail = size.width * 0.78f
            drawLine(
                color = AccentMagenta.copy(alpha = 0.28f),
                start = Offset(leftRail, 0f),
                end = Offset(leftRail, size.height),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = AccentMagenta.copy(alpha = 0.28f),
                start = Offset(rightRail, 0f),
                end = Offset(rightRail, size.height),
                strokeWidth = 1.dp.toPx()
            )
            val tokenY = size.height * railProgress
            drawLine(
                color = AccentCyan.copy(alpha = 0.45f),
                start = Offset(leftRail, tokenY - 60.dp.toPx()),
                end = Offset(leftRail, tokenY),
                strokeWidth = 3.dp.toPx()
            )
            drawCircle(
                color = AccentMagenta,
                radius = 7.dp.toPx(),
                center = Offset(leftRail, tokenY)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(208.dp)
                    .cornerBrackets(
                        AccentCyan.copy(alpha = bracketAlpha),
                        length = 20.dp,
                        thickness = 2.dp,
                        inset = 0.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(176.dp)
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.app_title),
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    color = AccentMagenta.copy(alpha = 0.45f),
                    modifier = Modifier.offset(y = 2.dp)
                )
                Text(
                    text = stringResource(R.string.app_title),
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                letterSpacing = 4.sp,
                color = TextMuted
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(3.dp),
                color = AccentCyan,
                trackColor = SurfaceRaised
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.splash_loading),
                fontSize = 10.sp,
                lineHeight = 13.sp,
                letterSpacing = 3.sp,
                color = TextMuted
            )
        }
    }
}
