package com.puri.app.feature.solve.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import kotlinx.coroutines.delay

// Rotating messages to make the 2-8 second wait feel engaged not frozen
private val loadingMessages = listOf(
    R.string.loading_message_1,
    R.string.loading_message_2,
    R.string.loading_message_3,
    R.string.loading_message_4
)

@Composable
fun LoadingContent(bitmap: Bitmap?) {
    var messageIndex by remember { mutableIntStateOf(0) }

    // Cycle through loading messages every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            messageIndex = (messageIndex + 1) % loadingMessages.size
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        bitmap?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(30.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(
                color = IndigoPrimary,
                modifier = Modifier.size(56.dp),
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(loadingMessages[messageIndex]),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
