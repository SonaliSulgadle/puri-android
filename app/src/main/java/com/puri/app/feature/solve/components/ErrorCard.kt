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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puri.app.R
import com.puri.app.core.common.PuriError
import com.puri.app.core.ui.theme.IndigoPrimary

@Composable
fun ErrorCard(
    error: PuriError,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val content = errorContent(error)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = content.emoji,
                fontSize = 56.sp
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            Text(
                text = content.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            Text(
                text = content.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

            // Retry — shown for all errors except daily limit
            // Daily limit is not retryable — no point showing the button
            if (content.showRetry) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.error_try_again),
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(stringResource(R.string.error_go_back))
            }
        }
    }
}

// ── Data class to hold display content ────────────────────────────────────────

private data class ErrorContent(
    val emoji: String,
    val title: String,
    val body: String,
    val showRetry: Boolean = true
)

@Composable
private fun errorContent(error: PuriError): ErrorContent = when (error) {

    PuriError.NoInternet -> ErrorContent(
        emoji = "📡",
        title = stringResource(R.string.error_no_internet_title),
        body = stringResource(R.string.error_no_internet_body)
    )

    PuriError.Timeout -> ErrorContent(
        emoji = "⏱️",
        title = stringResource(R.string.error_timeout_title),
        body = stringResource(R.string.error_timeout_body)
    )

    is PuriError.ApiError -> when (error.code) {
        429 -> ErrorContent(
            emoji = "⏱️",
            title = stringResource(R.string.error_rate_limit_title),
            body = stringResource(R.string.error_rate_limit_body)
        )

        503 -> ErrorContent(
            emoji = "🔧",
            title = stringResource(R.string.error_service_title),
            body = stringResource(R.string.error_service_body)
        )

        403 -> ErrorContent(
            emoji = "🔑",
            title = stringResource(R.string.error_access_title),
            body = stringResource(R.string.error_access_body)
        )

        else -> ErrorContent(
            emoji = "⚠️",
            title = stringResource(R.string.error_unknown_title),
            body = stringResource(R.string.error_unknown_body)
        )
    }

    else -> ErrorContent(
        emoji = "⚠️",
        title = stringResource(R.string.error_unknown_title),
        body = stringResource(R.string.error_unknown_body)
    )
}