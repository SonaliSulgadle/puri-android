package com.puri.app.feature.saved.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.domain.model.GuideSection
import com.puri.app.feature.saved.detail.SectionTitle

@Composable
fun StepsSection(section: GuideSection) {
    Column {
        SectionTitle(text = section.title)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        section.items.forEachIndexed { index, item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.spacing_md)
                ),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(IndigoPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = item.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp)
                )
            }
            if (index < section.items.lastIndex) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
            }
        }
    }
}