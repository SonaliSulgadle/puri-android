package com.puri.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.PuriTheme

@Composable
fun PuriTopBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = IndigoPrimary,
            fontWeight = FontWeight.ExtraBold
        )
        IconButton(onClick = { /* navigate to profile */ }) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = stringResource(R.string.cd_user_avatar),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.avatar_size))
                    .clip(CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PuriTopBarPreview() {
    PuriTheme { PuriTopBar() }
}