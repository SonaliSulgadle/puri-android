package com.puri.app.feature.solve.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.PuriTheme

@Composable
fun SavedGuidesFeatureCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_xl)))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1A1040),
                        Color(0xFF2D1B69)
                    )
                )
            )
            .clickable { onClick() }
            .padding(dimensionResource(R.dimen.spacing_xl))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_md)))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_md))
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.home_guides_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.home_guides_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(dimensionResource(R.dimen.icon_md))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedGuidesFeatureCardPreview() {
    PuriTheme { SavedGuidesFeatureCard(onClick = {}) }
}