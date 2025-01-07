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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Slider
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
import androidx.compose.ui.unit.Dp
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
fun MetronomeBody(context: Context = LocalContext.current) {
    var tempo by remember { mutableStateOf(120) } // Темп в BPM
    var isPlaying by remember { mutableStateOf(false) } // Состояние метронома
    var currentBeat by remember { mutableStateOf(0) } // Текущая доля ритма
    val beatsPerMeasure = 4 // Количество долей в такте (4/4)
    val scope = rememberCoroutineScope()
    // Звуковые ресурсы для разных долей
    val soundResources = listOf(
        R.raw.goal_1, // Звук для сильной доли (первая доля)
        R.raw.goal_2,   // Звук для слабых долей
        R.raw.goal_2,
        R.raw.goal_2
    )
    // Размеры точек для разных долей
    val dotSizes = listOf(60.dp, 40.dp, 40.dp, 40.dp) // Первая точка больше

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until beatsPerMeasure) {
                    BeatDot(
                        isActive = i == currentBeat,
                        size = dotSizes[i] // Размер точки зависит от её позиции
                    )
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
            Slider(
                value = tempo.toFloat(),
                onValueChange = { tempo = it.toInt() },
                valueRange = 40f..200f,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                isPlaying = !isPlaying
                if (isPlaying) {
                    // Воспроизведение первого звука сразу при старте
                    playSound(context, soundResources[currentBeat])
                } else {
                    currentBeat = 0 // Сбрасываем текущую долю при остановке
                }
            }) {
                Text(if (isPlaying) "Стоп" else "Старт")
            }
        }

        // Логика метронома
        LaunchedEffect(isPlaying, tempo) {
            if (isPlaying) {
                while (isPlaying) {
                    delay(60000L / tempo) // Задержка в зависимости от темпа
                    currentBeat = (currentBeat + 1) % beatsPerMeasure // Переход к следующей доле
                    playSound(context, soundResources[currentBeat])
                }
            }
        }
    }
}

@Composable
fun BeatDot(isActive: Boolean, size: Dp) {
    val animatedColor by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f, // Анимация цвета
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Color.LightGray.copy(alpha = 1f - animatedColor) // Серый цвет для неактивной точки
            )
            .background(
                Color.Red.copy(alpha = animatedColor) // Красный цвет для активной точки
            )
    )
}

// Функция для воспроизведения звука
fun playSound(context: Context, soundResource: Int) {
    val mediaPlayer = MediaPlayer.create(context, soundResource)
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}

@Preview(showBackground = true)
@Composable
fun MetronomePreview() {
    MetronomeBody()
}