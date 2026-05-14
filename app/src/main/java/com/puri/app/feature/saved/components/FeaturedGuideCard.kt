package com.puri.app.feature.saved.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.mapper.displayDescription
import com.puri.app.core.ui.mapper.displayTitle
import com.puri.app.core.ui.mapper.toLabelRes
import com.puri.app.core.ui.theme.CeramicWhite
import com.puri.app.core.ui.theme.GradientHeroEnd
import com.puri.app.core.ui.theme.GradientHeroStart
import com.puri.app.domain.model.SavedGuide

@Composable
fun FeaturedGuideCard(
    guide: SavedGuide,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_xl)))
            .background(Brush.linearGradient(listOf(GradientHeroStart, GradientHeroEnd)))
            .clickable { onClick() }
            .padding(dimensionResource(R.dimen.spacing_xl))
    ) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(
                text = guide.category.emoji + "  " +
                        stringResource(guide.category.toLabelRes()).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = CeramicWhite.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
            Text(
                text = guide.displayTitle(),
                style = MaterialTheme.typography.titleLarge,
                color = CeramicWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
            Text(
                text = guide.displayDescription(),
                style = MaterialTheme.typography.bodySmall,
                color = CeramicWhite.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}