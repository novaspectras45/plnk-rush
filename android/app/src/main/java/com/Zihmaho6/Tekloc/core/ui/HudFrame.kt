package com.Zihmaho6.Tekloc.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.BorderGlow
import com.Zihmaho6.Tekloc.core.theme.SurfaceNight

fun Modifier.cornerBrackets(
    color: Color,
    length: Dp = 18.dp,
    thickness: Dp = 1.5.dp,
    inset: Dp = 6.dp
): Modifier = this.drawBehind {
    val len = length.toPx()
    val stroke = thickness.toPx()
    val pad = inset.toPx()
    val right = size.width - pad
    val bottom = size.height - pad
    drawLine(color, Offset(pad, pad), Offset(pad + len, pad), stroke)
    drawLine(color, Offset(pad, pad), Offset(pad, pad + len), stroke)
    drawLine(color, Offset(right, pad), Offset(right - len, pad), stroke)
    drawLine(color, Offset(right, pad), Offset(right, pad + len), stroke)
    drawLine(color, Offset(pad, bottom), Offset(pad + len, bottom), stroke)
    drawLine(color, Offset(pad, bottom), Offset(pad, bottom - len), stroke)
    drawLine(color, Offset(right, bottom), Offset(right - len, bottom), stroke)
    drawLine(color, Offset(right, bottom), Offset(right, bottom - len), stroke)
}

@Composable
fun HudFrame(
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceNight.copy(alpha = 0.90f),
    borderColor: Color = BorderGlow,
    bracketColor: Color = AccentCyan,
    cornerRadius: Dp = 18.dp,
    contentPadding: PaddingValues = PaddingValues(14.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
            .cornerBrackets(bracketColor)
            .padding(contentPadding),
        content = content
    )
}
