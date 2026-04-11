package com.puri.app.feature.solve.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.puri.app.R
import com.puri.app.core.ui.mapper.toChipColor
import com.puri.app.core.ui.mapper.toLabelRes
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.domain.model.Category

@Composable
fun CategoryChip(
    category: Category,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val baseColor = category.toChipColor()
    val chipColor = if (isDark) baseColor.copy(
        red = (baseColor.red + 0.3f).coerceAtMost(1f),
        green = (baseColor.green + 0.3f).coerceAtMost(1f),
        blue = (baseColor.blue + 0.3f).coerceAtMost(1f)
    ) else baseColor
    Box(
        modifier = modifier
            .background(
                color = chipColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill))
            )
            .padding(
                horizontal = dimensionResource(R.dimen.spacing_md),
                vertical = dimensionResource(R.dimen.spacing_xs)
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = category.emoji,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_xs)))
            Text(
                text = stringResource(category.toLabelRes()).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = chipColor
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryChipPreview() {
    PuriTheme {
        CategoryChip(
            category = Category.FOOD,
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_sm))
        )
    }
}