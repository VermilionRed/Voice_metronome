package com.example.voice_metronome

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MetronomeBody(context = this)
        }
    }
}

@Composable
fun MetronomeBody(context: Context = LocalContext.current){
    var tempo by remember { mutableStateOf(120) }
    var isPlaying by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Tempo: $tempo BPM", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { if (tempo > 40) tempo -= 1 }) {
                Text("-")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { if (tempo < 200) tempo += 1 }) {
                Text("+")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { isPlaying = !isPlaying }) {
            Text(if (isPlaying) "Stop" else "Start")
        }
    }

    if (isPlaying) {
        LaunchedEffect(tempo) {
            while (isPlaying) {
                scope.launch {
                    val mediaPlayer = MediaPlayer.create(context, R.raw.cowbell)
                    mediaPlayer.start()
                    mediaPlayer.setOnCompletionListener {
                        it.release()
                    }
                }
                delay(60000L / tempo)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MetronomePreview() {
    MetronomeBody()
}