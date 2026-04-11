package com.puri.app.feature.solve.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary

@Composable
fun SectionHeader(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = IndigoPrimary,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_md))
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = IndigoPrimary
        )
    }
}