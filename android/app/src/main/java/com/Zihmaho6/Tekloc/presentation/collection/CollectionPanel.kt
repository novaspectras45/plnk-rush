package com.Zihmaho6.Tekloc.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.BackgroundDeep
import com.Zihmaho6.Tekloc.core.theme.BorderIdle
import com.Zihmaho6.Tekloc.core.theme.SurfaceNight
import com.Zihmaho6.Tekloc.core.theme.TextMuted
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.ui.ScreenBackdrop
import com.Zihmaho6.Tekloc.core.ui.SectionHeader

@Composable
fun CollectionPanel(onBack: () -> Unit) {
    val collectionViewModel: CollectionViewModel =
        viewModel(factory = ServiceLocator.viewModelFactory())
    val state by collectionViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        collectionViewModel.refresh()
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
                title = stringResource(R.string.nav_collection),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack,
                starTotal = state.totalStars,
                starDescription = stringResource(R.string.cd_star)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.cores, key = { it.id }) { core ->
                    CoreCell(core = core, onSelect = { collectionViewModel.select(core.id) })
                }
            }
        }
    }
}

@Composable
private fun CoreCell(core: CoreRow, onSelect: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val base = Modifier
        .fillMaxWidth()
        .height(168.dp)
        .clip(shape)
        .background(SurfaceNight.copy(alpha = 0.88f))
        .border(
            width = if (core.selected) 1.5.dp else 1.dp,
            color = if (core.selected) AccentCyan else BorderIdle,
            shape = shape
        )
    Box(
        modifier = if (core.unlocked) base.clickable(onClick = onSelect) else base,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    Color(core.tint).copy(alpha = if (core.unlocked) 0.6f else 0.2f),
                    BlendMode.SrcAtop
                ),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = coreName(core.id),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (core.unlocked) TextPrimary else TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (!core.unlocked) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = stringResource(R.string.cd_locked),
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(R.string.core_locked, core.starsToUnlock),
                        fontSize = 9.sp,
                        lineHeight = 12.sp,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )
                }
            } else {
                Text(
                    text = if (core.selected) {
                        stringResource(R.string.cta_active)
                    } else {
                        stringResource(R.string.cta_select)
                    },
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (core.selected) AccentCyan else TextMuted
                )
            }
        }
    }
}

@Composable
private fun coreName(id: Int): String = when (id) {
    0 -> stringResource(R.string.core_one)
    1 -> stringResource(R.string.core_two)
    2 -> stringResource(R.string.core_three)
    3 -> stringResource(R.string.core_four)
    4 -> stringResource(R.string.core_five)
    else -> stringResource(R.string.core_six)
}
