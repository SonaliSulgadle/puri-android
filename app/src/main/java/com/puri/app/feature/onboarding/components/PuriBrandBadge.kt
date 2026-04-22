package com.puri.app.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puri.app.R
import com.puri.app.core.ui.theme.GradientSnapEnd
import com.puri.app.core.ui.theme.GradientSnapStart

@Composable
fun PuriBrandBadge(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(GradientSnapStart, GradientSnapEnd))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.puri_brand_badge_label),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))

        Text(
            text = stringResource(R.string.puri_label),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Accent line
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(accentColor.copy(alpha = 0.6f))
        )
    }
}