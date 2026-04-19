package com.puri.app.feature.saved.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.domain.model.GuideSection
import com.puri.app.feature.saved.detail.SectionTitle

@Composable
fun ListSection(section: GuideSection) {
    Column {
        SectionTitle(text = section.title)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        section.items.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IndigoPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = item.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
        }
    }
}