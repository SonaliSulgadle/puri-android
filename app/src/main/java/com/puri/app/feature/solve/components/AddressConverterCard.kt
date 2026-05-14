package com.puri.app.feature.solve.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puri.app.R
import com.puri.app.core.ui.theme.CeladonPrimary
import com.puri.app.core.ui.theme.CeramicWhite
import com.puri.app.core.ui.theme.PuriTheme

@Composable
fun AddressConverterCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Subtle shimmer sweep — slower than SnapAndSolve, less distracting
    val shimmer = rememberInfiniteTransition(label = "addr_shimmer")
    val shimmerX by shimmer.animateFloat(
        initialValue = -400f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_xl)))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0C0D0C),
                        Color(0xFF121412),
                        Color(0xFF1A2B20)
                    )
                )
            )
            .clickable { onClick() }
    ) {
        // Shimmer overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CeramicWhite.copy(alpha = 0f),
                            CeramicWhite.copy(alpha = 0.04f),
                            CeramicWhite.copy(alpha = 0f)
                        ),
                        start = Offset(shimmerX - 200f, 0f),
                        end = Offset(shimmerX + 200f, 200f)
                    )
                )
        )

        // Large decorative pin emoji
        Text(
            text = stringResource(R.string.address_pin_emoji),
            fontSize = 72.sp,
            color = CeramicWhite.copy(alpha = 0.07f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_xl)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
        ) {
            // Icon circle with pin
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CeladonPrimary.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.address_pin_emoji), fontSize = 20.sp)
            }

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(CeramicWhite.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.address_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFDCE6DE),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.address_card_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = CeramicWhite,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.address_card_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = CeramicWhite.copy(alpha = 0.55f)
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = CeramicWhite.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddressConverterCardPreview() {
    PuriTheme {
        AddressConverterCard(
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}