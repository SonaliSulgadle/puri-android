package com.puri.app.feature.solve.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.CeladonPrimary
import com.puri.app.core.ui.theme.CeramicWhite
import com.puri.app.core.ui.theme.PuriTheme
import kotlinx.coroutines.delay

enum class LoadingType {
    IMAGE,  // Snap & Solve — analysing photo
    TEXT    // Ask anything — finding answer
}

@Composable
fun LoadingContent(
    type: LoadingType = LoadingType.IMAGE,
    modifier: Modifier = Modifier
) {
    val loadingMessages = remember(type) {
        when (type) {
            LoadingType.IMAGE -> listOf(
                R.string.loading_image_1,
                R.string.loading_image_2,
                R.string.loading_image_3,
                R.string.loading_image_4
            )

            LoadingType.TEXT -> listOf(
                R.string.loading_text_1,
                R.string.loading_text_2,
                R.string.loading_text_3,
                R.string.loading_text_4
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ShimmerResponseCard()

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        ) {
            LoadingPill(loadingMessages)
        }
    }
}

@Composable
private fun LoadingPill(loadingMessages: List<Int>) {
    var messageIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            messageIndex = (messageIndex + 1) % loadingMessages.size
        }
    }

    Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill)),
        color = CeladonPrimary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.spacing_sm)
            )
        ) {
            CircularProgressIndicator(
                color = CeramicWhite,
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            AnimatedContent(
                targetState = messageIndex,
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                },
                label = "loading_message"
            ) { index ->
                Text(
                    text = stringResource(loadingMessages[index]),
                    color = CeramicWhite,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewLoadingContent() {
    PuriTheme {
        LoadingContent()
    }
}