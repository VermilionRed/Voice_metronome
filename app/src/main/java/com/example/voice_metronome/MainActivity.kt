package com.example.voice_metronome

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
    var tempo by remember { mutableStateOf(120) } // Темп в BPM
    var isPlaying by remember { mutableStateOf(false) } // Состояние метронома
    var currentBeat by remember { mutableStateOf(0) } // Текущая доля ритма
    val beatsPerMeasure = 4 // Количество долей в такте (4/4)
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        // ☝️ This remains System UI bars.
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (i in 0 until beatsPerMeasure) {
                    BeatDot(isActive = i == currentBeat)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Темп: $tempo BPM", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = { if (tempo > 44) tempo -= 5 }) {
                    Text("-5")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { if (tempo > 40) tempo -= 1 }) {
                    Text("-1")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { if (tempo < 200) tempo += 1 }) {
                    Text("+1")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { if (tempo < 196) tempo += 5 }) {
                    Text("+5")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                isPlaying = !isPlaying
                if (!isPlaying) {
                    currentBeat = 0 // Сбрасываем текущую долю при остановке
                }
            }) {
                Text(if (isPlaying) "Стоп" else "Старт")
            }
        }
        // Логика метронома
        if (isPlaying) {
            LaunchedEffect(tempo) {
                while (isPlaying) {
                    currentBeat = (currentBeat + 1) % beatsPerMeasure // Переход к следующей доле
                    scope.launch {
                        val mediaPlayer = MediaPlayer.create(context, R.raw.cowbell)
                        mediaPlayer.start()
                        mediaPlayer.setOnCompletionListener {
                            it.release()
                        }
                    }
                    delay(60000L / tempo) // Задержка в зависимости от темпа
                }
            }
        }
        // Сброс метронома при изменении темпа
        LaunchedEffect(tempo) {
            if (isPlaying) {
                isPlaying = false // Останавливаем метроном
                currentBeat = 0 // Сбрасываем текущую долю
                isPlaying = true // Перезапускаем метроном
            }
        }
    }
}
@Composable
fun BeatDot(isActive: Boolean) {
    val animatedColor by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f, // Анимация цвета
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                Color.LightGray.copy(alpha = 1f - animatedColor) // Серый цвет для неактивной точки
            )
            .background(
                Color.Red.copy(alpha = animatedColor) // Красный цвет для активной точки
            )
    )
}

@Preview(showBackground = true)
@Composable
fun MetronomePreview() {
    MetronomeBody()
}