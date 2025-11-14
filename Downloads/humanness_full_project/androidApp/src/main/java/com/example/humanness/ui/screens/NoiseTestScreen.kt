package com.example.humanness.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import shared.NoiseLevel

@Composable
fun NoiseTestScreen(
    onPassTest: () -> Unit,
    onFailTest: () -> Unit
) {
    val isTestStarted = remember { mutableStateOf(false) }
    val currentDb = remember { mutableStateOf(0) }
    val testResult = remember { mutableStateOf<NoiseLevel?>(null) }
    val testProgress = remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Noise Level Test",
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (testResult.value == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "dB Level",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${currentDb.value}",
                        fontSize = 48.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    if (isTestStarted.value) {
                        CircularProgressIndicator(
                            progress = { testProgress.value },
                            modifier = Modifier
                                .size(100.dp)
                                .padding(top = 16.dp)
                        )
                    }
                }
            } else {
                val (messageText, color) = when (testResult.value) {
                    NoiseLevel.GOOD -> "Good to proceed" to Color.Green
                    NoiseLevel.NEEDS_QUIET -> "Please move to a quieter place" to Color.Red
                    else -> "Unknown" to Color.Gray
                }
                Text(
                    text = messageText,
                    fontSize = 20.sp,
                    color = color,
                    textAlign = TextAlign.Center,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }

        if (testResult.value == null) {
            Button(
                onClick = {
                    isTestStarted.value = true
                    currentDb.value = 0
                    testProgress.value = 0f
                },
                enabled = !isTestStarted.value,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Start Test")
            }

            // Simulate test when started
            if (isTestStarted.value) {
                LaunchedEffect(Unit) {
                    repeat(30) {
                        delay(100)
                        currentDb.value = (0..60).random()
                        testProgress.value = (it + 1) / 30f
                    }
                    
                    // Determine result
                    testResult.value = if (currentDb.value < 40) {
                        NoiseLevel.GOOD
                    } else {
                        NoiseLevel.NEEDS_QUIET
                    }
                }
            }
        } else {
            Button(
                onClick = {
                    if (testResult.value == NoiseLevel.GOOD) {
                        onPassTest()
                    } else {
                        isTestStarted.value = false
                        currentDb.value = 0
                        testResult.value = null
                        testProgress.value = 0f
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    if (testResult.value == NoiseLevel.GOOD) "Continue to Task Selection" else "Retry Test"
                )
            }
        }
    }
}
