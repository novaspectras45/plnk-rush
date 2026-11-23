package com.Zihmaho6.Tekloc.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Zihmaho6.Tekloc.R
import com.Zihmaho6.Tekloc.core.config.GameConfig
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.AccentLime
import com.Zihmaho6.Tekloc.core.theme.AccentMagenta
import com.Zihmaho6.Tekloc.core.theme.BorderGlow
import com.Zihmaho6.Tekloc.core.theme.BorderIdle
import com.Zihmaho6.Tekloc.core.theme.SurfaceNight
import com.Zihmaho6.Tekloc.core.theme.SurfaceRaised
import com.Zihmaho6.Tekloc.domain.model.BoardLayout
import com.Zihmaho6.Tekloc.domain.model.BumperKind
import com.Zihmaho6.Tekloc.domain.model.Cell
import com.Zihmaho6.Tekloc.domain.model.DropStep
import com.Zihmaho6.Tekloc.domain.model.GateColor
import com.Zihmaho6.Tekloc.domain.model.PegPolarity
import com.Zihmaho6.Tekloc.domain.model.Placement

val BOARD_PAD = 8.dp
val BOARD_BORDER = 2.dp
val BOARD_FRAME = BOARD_PAD + BOARD_BORDER

fun gateColor(color: GateColor): Color = when (color) {
    GateColor.MAGENTA -> AccentMagenta
    GateColor.CYAN -> AccentCyan
    GateColor.LIME -> AccentLime
}

fun boardWidth(cell: Dp, cols: Int): Dp = cell * cols + BOARD_FRAME * 2

fun boardHeight(cell: Dp, rows: Int): Dp = cell * rows + cell * 1.3f + BOARD_FRAME * 2

@Composable
fun BoardCanvas(
    layout: BoardLayout,
    placement: Placement,
    cell: Dp,
    modifier: Modifier = Modifier,
    laneIndex: Int = 0,
    previewPath: List<DropStep> = emptyList(),
    previewZone: Int = -1,
    previewEndpointOnly: Boolean = false,
    tokenX: Int = -1,
    tokenY: Int = -1,
    trail: List<DropStep> = emptyList(),
    litGates: Set<Int> = emptySet(),
    barrierStep: Int = -1,
    tokenTint: Color = AccentMagenta,
    ghostAlpha: Float = 0.5f,
    showGhostSlots: Boolean = false,
    landedZone: Int = -1,
    interactive: Boolean = false,
    onSlotTap: (Int, Int) -> Unit = { _, _ -> }
) {
    val cols = layout.cols
    val rows = layout.rows
    val width = boardWidth(cell, cols)
    val height = boardHeight(cell, rows)
    val shape = RoundedCornerShape(16.dp)
    val pegSize = cell * 0.52f
    val gateSize = cell * 0.9f
    val bumperSize = cell * 0.86f
    val tokenSize = cell * 0.74f

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(SurfaceNight.copy(alpha = 0.84f))
            .border(BOARD_BORDER, BorderGlow, shape)
            .cornerBrackets(AccentCyan, length = 16.dp, inset = 5.dp)
            .pointerInput(interactive, layout.seed, cell) {
                if (!interactive) return@pointerInput
                detectTapGestures { position ->
                    val framePx = BOARD_FRAME.toPx()
                    val cellPx = cell.toPx()
                    val cx = ((position.x - framePx) / cellPx).toInt()
                    val cy = ((position.y - framePx) / cellPx).toInt()
                    if (cx in 0 until cols && cy in 0 until rows) onSlotTap(cx, cy)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val framePx = BOARD_FRAME.toPx()
            val cellPx = cell.toPx()
            val gridRight = framePx + cellPx * cols
            val gridBottom = framePx + cellPx * rows

            for (i in 0..cols) {
                val x = framePx + cellPx * i
                drawLine(SurfaceRaised, Offset(x, framePx), Offset(x, gridBottom), 1f)
            }
            for (i in 0..rows) {
                val y = framePx + cellPx * i
                val tone = if (i % 4 == 0) AccentCyan.copy(alpha = 0.08f) else SurfaceRaised
                drawLine(tone, Offset(framePx, y), Offset(gridRight, y), 1f)
            }

            drawZoneStrip(
                framePx = framePx,
                cellPx = cellPx,
                cols = cols,
                top = gridBottom,
                targetZone = layout.targetZone,
                landedZone = landedZone
            )

            if (showGhostSlots) {
                for (index in layout.slots) {
                    if (placement.at(index % cols, index / cols) != null) continue
                    val gx = framePx + cellPx * (index % cols) + cellPx / 2f
                    val gy = framePx + cellPx * (index / cols) + cellPx / 2f
                    drawCircle(
                        color = AccentCyan.copy(alpha = ghostAlpha * 0.55f),
                        radius = cellPx * 0.3f,
                        center = Offset(gx, gy),
                        style = Stroke(
                            width = 1.4f * density,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                        )
                    )
                }
            }

            if (previewPath.isNotEmpty() && !previewEndpointOnly) {
                val path = Path()
                val startX = framePx + cellPx * GameConfig.LANE_COLUMNS[
                    laneIndex.coerceIn(0, GameConfig.LANE_COLUMNS.size - 1)
                ] + cellPx / 2f
                path.moveTo(startX, framePx)
                previewPath.forEach { step ->
                    path.lineTo(
                        framePx + cellPx * step.x + cellPx / 2f,
                        framePx + cellPx * step.y + cellPx / 2f
                    )
                }
                drawPath(
                    path = path,
                    color = AccentCyan.copy(alpha = 0.72f),
                    style = Stroke(
                        width = 2.2f * density,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 9f))
                    )
                )
            }

            if (previewZone in 0 until GameConfig.ZONES) {
                val span = zoneSpan(previewZone, cols)
                val centre = framePx + cellPx * (span.first + span.second / 2f)
                drawCircle(
                    color = AccentCyan.copy(alpha = 0.85f),
                    radius = cellPx * 0.42f,
                    center = Offset(centre, gridBottom + cellPx * 0.65f),
                    style = Stroke(width = 2f * density)
                )
            }
        }

        for (y in 0 until rows) {
            for (x in 0 until cols) {
                val bumper = placement.at(x, y)
                if (bumper != null) {
                    Image(
                        painter = painterResource(R.drawable.sprite_2),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            if (bumper.kind == BumperKind.DEFLECT) AccentCyan else AccentLime,
                            BlendMode.SrcAtop
                        ),
                        modifier = Modifier
                            .offset(
                                x = BOARD_FRAME + cell * x + (cell - bumperSize) / 2,
                                y = BOARD_FRAME + cell * y + (cell - bumperSize) / 2
                            )
                            .size(bumperSize)
                            .rotate(bumper.rotation.toFloat())
                    )
                    continue
                }
                when (val content = layout.cellAt(x, y)) {
                    is Cell.Peg -> Image(
                        painter = painterResource(R.drawable.sprite_1),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            AccentCyan.copy(alpha = 0.55f),
                            BlendMode.SrcAtop
                        ),
                        modifier = Modifier
                            .offset(
                                x = BOARD_FRAME + cell * x + (cell - pegSize) / 2,
                                y = BOARD_FRAME + cell * y + (cell - pegSize) / 2
                            )
                            .size(pegSize)
                            .rotate(if (content.polarity == PegPolarity.LEFT) 0f else 180f)
                    )

                    is Cell.GateCell -> Image(
                        painter = painterResource(R.drawable.sprite_3),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            gateColor(content.color).copy(
                                alpha = if (litGates.contains(content.index)) 1f else 0.5f
                            ),
                            BlendMode.SrcAtop
                        ),
                        modifier = Modifier
                            .offset(
                                x = BOARD_FRAME + cell * x + (cell - gateSize) / 2,
                                y = BOARD_FRAME + cell * y + (cell - gateSize) / 2
                            )
                            .size(gateSize)
                    )

                    Cell.Empty -> Unit
                }
            }
        }

        layout.barriers.forEach { barrier ->
            val step = if (barrierStep < 0) barrier.y else barrierStep
            val column = barrier.homeX + (((step + barrier.phase) % 3) - 1)
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    AccentMagenta.copy(alpha = 0.85f),
                    BlendMode.SrcAtop
                ),
                modifier = Modifier
                    .offset(
                        x = BOARD_FRAME + cell * column.coerceIn(0, cols - 1) +
                            (cell - gateSize) / 2,
                        y = BOARD_FRAME + cell * barrier.y + (cell - gateSize) / 2
                    )
                    .size(gateSize)
            )
        }

        trail.forEachIndexed { index, step ->
            val alpha = when (index) {
                0 -> 0.45f
                1 -> 0.25f
                else -> 0.12f
            }
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    tokenTint.copy(alpha = alpha),
                    BlendMode.SrcAtop
                ),
                modifier = Modifier
                    .offset(
                        x = BOARD_FRAME + cell * step.x + (cell - tokenSize) / 2,
                        y = BOARD_FRAME + cell * step.y + (cell - tokenSize) / 2
                    )
                    .size(tokenSize)
            )
        }

        if (tokenX >= 0 && tokenY >= 0) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    tokenTint.copy(alpha = 0.6f),
                    BlendMode.SrcAtop
                ),
                modifier = Modifier
                    .offset(
                        x = BOARD_FRAME + cell * tokenX + (cell - tokenSize) / 2,
                        y = BOARD_FRAME + cell * tokenY + (cell - tokenSize) / 2
                    )
                    .size(tokenSize)
            )
        }
    }
}

private fun zoneSpan(zone: Int, cols: Int): Pair<Int, Float> {
    val start = zone * 2
    val span = if (start + 1 < cols) 2f else 1f
    return Pair(start, span)
}

private fun DrawScope.drawZoneStrip(
    framePx: Float,
    cellPx: Float,
    cols: Int,
    top: Float,
    targetZone: Int,
    landedZone: Int
) {
    val stripHeight = cellPx * 1.25f
    for (zone in 0 until GameConfig.ZONES) {
        val span = zoneSpan(zone, cols)
        val left = framePx + cellPx * span.first
        val widthPx = cellPx * span.second
        val fill = when {
            zone == targetZone && zone == landedZone -> AccentCyan.copy(alpha = 0.26f)
            zone == targetZone -> AccentCyan.copy(alpha = 0.14f)
            zone == landedZone -> AccentMagenta.copy(alpha = 0.22f)
            else -> SurfaceRaised.copy(alpha = 0.5f)
        }
        drawRect(
            color = fill,
            topLeft = Offset(left + 2f, top + 4f),
            size = Size(widthPx - 4f, stripHeight - 8f)
        )
        val outline = when {
            zone == targetZone -> AccentCyan
            zone == landedZone -> AccentMagenta
            else -> BorderIdle
        }
        drawRect(
            color = outline.copy(alpha = 0.7f),
            topLeft = Offset(left + 2f, top + 4f),
            size = Size(widthPx - 4f, stripHeight - 8f),
            style = Stroke(width = 1.4f * density)
        )
    }
}
