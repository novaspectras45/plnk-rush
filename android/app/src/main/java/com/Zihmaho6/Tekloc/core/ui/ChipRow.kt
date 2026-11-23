package com.Zihmaho6.Tekloc.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Zihmaho6.Tekloc.core.theme.AccentMagenta
import com.Zihmaho6.Tekloc.core.theme.BorderIdle
import com.Zihmaho6.Tekloc.core.theme.SurfaceNight
import com.Zihmaho6.Tekloc.core.theme.TextPrimary
import com.Zihmaho6.Tekloc.core.theme.TextSecondary

@Composable
fun ChipRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = AccentMagenta,
    chipHeight: Dp = 48.dp,
    selectedStateLabel: String = "",
    gap: Dp = 8.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Chip(
                label = label,
                selected = selected,
                accent = accent,
                chipHeight = chipHeight,
                stateLabel = if (selected && selectedStateLabel.isNotEmpty()) {
                    label + " " + selectedStateLabel
                } else {
                    label
                },
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RowScope.Chip(
    label: String,
    selected: Boolean,
    accent: Color,
    chipHeight: Dp,
    stateLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .height(chipHeight)
            .clip(shape)
            .background(if (selected) accent.copy(alpha = 0.14f) else SurfaceNight.copy(alpha = 0.76f))
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) accent else BorderIdle,
                shape = shape
            )
            .semantics { stateDescription = stateLabel }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 1.5.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (selected) TextPrimary else TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
