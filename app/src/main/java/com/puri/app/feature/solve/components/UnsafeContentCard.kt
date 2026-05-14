package com.puri.app.feature.solve.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.puri.app.R
import com.puri.app.core.ui.theme.CeramicWhite
import com.puri.app.core.ui.theme.MossTertiary
import com.puri.app.core.ui.theme.PuriTheme

@Composable
fun UnsafeContentCard(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onCallEmergency: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.spacing_xl)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.daily_limit_icon_container))
                .background(MossTertiary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.HealthAndSafety,
                contentDescription = null,
                tint = MossTertiary,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_xl))
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

        Text(
            text = stringResource(R.string.unsafe_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

        Text(
            text = stringResource(R.string.unsafe_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_3xl)))

        Button(
            onClick = onCallEmergency,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill)),
            colors = ButtonDefaults.buttonColors(containerColor = MossTertiary)
        ) {
            Text(
                text = stringResource(R.string.unsafe_emergency_call),
                color = CeramicWhite
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill))
        ) {
            Text(stringResource(R.string.unsafe_dismiss))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UnsafeContentCardPreview() {
    PuriTheme { UnsafeContentCard(onDismiss = {}) }
}