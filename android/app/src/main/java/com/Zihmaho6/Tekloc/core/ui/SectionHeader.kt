package com.Zihmaho6.Tekloc.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Zihmaho6.Tekloc.R
import com.Zihmaho6.Tekloc.core.theme.AccentAmber
import com.Zihmaho6.Tekloc.core.theme.AccentCyan
import com.Zihmaho6.Tekloc.core.theme.BorderIdle
import com.Zihmaho6.Tekloc.core.theme.MonoSmall
import com.Zihmaho6.Tekloc.core.theme.SurfaceNight
import com.Zihmaho6.Tekloc.core.theme.TextPrimary

@Composable
fun SectionHeader(
    title: String,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    starTotal: Int = -1,
    starDescription: String = ""
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceNight.copy(alpha = 0.76f))
                .border(1.dp, BorderIdle, RoundedCornerShape(12.dp))
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = backDescription,
                tint = AccentCyan,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            letterSpacing = 2.sp,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        if (starTotal >= 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceNight.copy(alpha = 0.76f))
                    .border(1.dp, AccentAmber.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = starDescription,
                    tint = AccentAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier.height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatCount(starTotal),
                        style = MonoSmall,
                        color = AccentAmber
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}
