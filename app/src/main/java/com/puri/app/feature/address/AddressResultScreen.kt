package com.puri.app.feature.address

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.analytics.Analytics
import com.puri.app.core.analytics.LocalAnalytics
import com.puri.app.core.analytics.PuriEvent
import com.puri.app.core.analytics.ScreenNames
import com.puri.app.core.analytics.TrackScreen
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.CeladonPrimary
import com.puri.app.domain.model.AddressConfidence
import com.puri.app.domain.model.AddressResult
import com.puri.app.domain.model.AddressType
import kotlinx.coroutines.delay

private const val WEB_APP_ADDRESS = "https://puri-address.vercel.app"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressResultScreen(
    onBack: () -> Unit,
    viewModel: AddressViewModel = hiltViewModel()
) {
    val analytics = LocalAnalytics.current
    TrackScreen(ScreenNames.ADDRESS_RESULT)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { onBack() }

    LaunchedEffect(uiState.result) {
        if (uiState.result == null) onBack()
    }
    val result = uiState.result ?: return

    Scaffold(
        topBar = {
            PuriTopBar(
                title = stringResource(R.string.address_result_title),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = CeladonPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            // Original input
            Text(
                text = stringResource(R.string.address_original_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = result.original,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            // Converted address card
            Text(
                text = stringResource(R.string.address_converted_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = CeladonPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_xl)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_xl))
                ) {
                    Text(
                        text = result.normalized,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (result.shortForm != result.normalized) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.shortForm,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ConfidencePill(confidence = result.confidence)
                        Spacer(modifier = Modifier.width(8.dp))
                        AddressTypePill(type = result.addressType)
                    }
                }
            }

            result.locationDetail?.let { detail ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_lg)))
                        .background(CeladonPrimary.copy(alpha = 0.08f))
                        .padding(dimensionResource(R.dimen.spacing_md)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
                ) {
                    Text(
                        text = stringResource(R.string.address_pin_emoji),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.address_detail_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = CeladonPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Warning note
            result.note?.let { note ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_lg)))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(dimensionResource(R.dimen.spacing_md))
                ) {
                    Text(
                        text = "ℹ️  $note",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

            AddressResultActions(result, context, analytics)

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

            Text(
                text = stringResource(R.string.address_disclaimer),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
        }
    }
}

@Composable
private fun AddressResultActions(
    result: AddressResult,
    context: Context,
    analytics: Analytics
) {
    val spacing = dimensionResource(R.dimen.spacing_md)

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        var copied by remember { mutableStateOf(false) }
        val clipboardManager = LocalClipboardManager.current

        Button(
            onClick = {
                clipboardManager.setText(AnnotatedString(result.normalized))
                copied = true
                analytics.log(PuriEvent.AddressCopied)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CeladonPrimary
            )
        ) {
            Icon(
                imageVector = if (copied) Icons.Outlined.Check
                else Icons.Outlined.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (copied) stringResource(R.string.address_copied)
                else stringResource(R.string.address_copy_button),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        LaunchedEffect(copied) {
            if (copied) {
                delay(2000)
                copied = false
            }
        }

        // ── Map buttons — side by side ───────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            // Naver Map
            OutlinedButton(
                onClick = {
                    analytics.log(PuriEvent.AddressOpenedNaver)
                    openNaverMap(context, result.shortForm)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, CeladonPrimary)
            ) {
                Text(
                    text = stringResource(R.string.address_open_naver_map),
                    color = CeladonPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Kakao Map
            OutlinedButton(
                onClick = {
                    analytics.log(PuriEvent.AddressOpenedKakao)
                    openKakaoMap(context, result.shortForm)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, CeladonPrimary)
            ) {
                Text(
                    text = stringResource(R.string.address_open_kakao_map),
                    color = CeladonPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        val webUrl = "https://puri-address.vercel.app?q=${Uri.encode(result.normalized)}"

        // ── Web tool link — always shown ─────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_lg)))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .clickable {
                    analytics.log(PuriEvent.WebToolOpened)
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            webUrl.toUri()
                        )
                    )
                }
                .padding(dimensionResource(R.dimen.spacing_md)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.spacing_sm)
            )
        ) {
            Text(text = "🌐", fontSize = 16.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.address_web_tool_title),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.address_web_tool_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = CeladonPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Map open helpers — app first, browser fallback automatic
private fun openNaverMap(context: Context, shortForm: String) {
    val encoded = Uri.encode(shortForm)
    val appIntent = Intent(
        Intent.ACTION_VIEW,
        "nmap://search?query=$encoded&appname=com.puri.app".toUri()
    )
    val webIntent = Intent(
        Intent.ACTION_VIEW,
        "https://map.naver.com/p/search/$encoded".toUri()
    )

    // Try app, fall back to browser — both handled by ACTION_VIEW automatically
    // Android will show browser if Naver Map is not installed
    try {
        context.startActivity(appIntent)
    } catch (e: Exception) {
        context.startActivity(webIntent)
    }
}

private fun openKakaoMap(context: Context, shortForm: String) {
    val encoded = Uri.encode(shortForm)
    val appIntent = Intent(
        Intent.ACTION_VIEW,
        "kakaomap://search?q=$encoded".toUri()
    ).apply {
        setPackage("net.daum.android.map")
    }
    val webIntent = Intent(
        Intent.ACTION_VIEW,
        "https://map.kakao.com/?q=$encoded".toUri()
    )

    val canOpenApp = context.packageManager
        .resolveActivity(appIntent, PackageManager.MATCH_DEFAULT_ONLY) != null

    context.startActivity(if (canOpenApp) appIntent else webIntent)
}

@Composable
private fun ConfidencePill(confidence: AddressConfidence) {
    val (bgColor, textColor, label) = when (confidence) {
        AddressConfidence.HIGH ->
            Triple(Color(0xFF2E7D32).copy(alpha = 0.12f), Color(0xFF2E7D32), "High confidence")

        AddressConfidence.MEDIUM ->
            Triple(Color(0xFFE65100).copy(alpha = 0.12f), Color(0xFFE65100), "Verify recommended")

        AddressConfidence.LOW ->
            Triple(
                MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                MaterialTheme.colorScheme.error,
                "Verify before visiting"
            )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AddressTypePill(type: AddressType) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(CeladonPrimary.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = type.label,
            style = MaterialTheme.typography.labelSmall,
            color = CeladonPrimary
        )
    }
}