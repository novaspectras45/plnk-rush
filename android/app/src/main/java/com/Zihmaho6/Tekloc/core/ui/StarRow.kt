package com.Zihmaho6.Tekloc.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Zihmaho6.Tekloc.R
import com.Zihmaho6.Tekloc.core.theme.AccentAmber
import com.Zihmaho6.Tekloc.core.theme.BorderIdle
import com.Zihmaho6.Tekloc.core.theme.TextMuted
import kotlinx.coroutines.delay

@Composable
fun StarRow(
    earned: Int,
    total: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 36.dp,
    captions: List<String> = emptyList(),
    staggered: Boolean = false
) {
    var revealed by remember(earned, total, staggered) {
        mutableIntStateOf(if (staggered) 0 else total)
    }
    LaunchedEffect(earned, total, staggered) {
        if (!staggered) return@LaunchedEffect
        for (index in 0 until total) {
            delay(140L)
            revealed = index + 1
        }
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top
    ) {
        for (index in 0 until total) {
            val shown = index < revealed
            val scale by animateFloatAsState(
                targetValue = if (shown) 1f else 0.4f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 420f),
                label = "starScale"
            )
            val alpha by animateFloatAsState(
                targetValue = if (shown) 1f else 0f,
                animationSpec = spring(dampingRatio = 0.9f, stiffness = 420f),
                label = "starAlpha"
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(starSize),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (index < earned) R.drawable.ic_star else R.drawable.ic_star_outline
                        ),
                        contentDescription = null,
                        tint = if (index < earned) AccentAmber else BorderIdle,
                        modifier = Modifier
                            .size(starSize)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                    )
                }
                if (captions.size > index) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = captions[index],
                        fontSize = 9.sp,
                        lineHeight = 12.sp,
                        letterSpacing = 1.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(starSize + 24.dp)
                    )
                }
            }
        }
    }
}
