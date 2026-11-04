package com.example.ui.screens.intro

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GymCyan
import com.example.ui.theme.GymDarkBg
import com.example.ui.theme.GymEmerald
import com.example.ui.theme.GymIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * IronPulse Splash Screen
 * Features:
 * 1. Monolithic Twin Bars (||) expanding with staggered 80ms offset using cubic-bezier [0.16, 1, 0.3, 1]
 * 2. Energy sweep internal gradient
 * 3. Typographic reveal: IRONPULSE wordmark expands from 0.18em to 0.36em letter tracking
 * 4. Concentric acoustic radial echoes
 * 5. Native AudioTrack harmonic synthesizer (65Hz -> 110Hz sub-bass pulse + 440Hz / 659Hz crystal chime)
 * 6. Smooth dissolve exit into dashboard
 */
@Composable
fun IronPulseIntroScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Custom Cubic-Bezier easing requested: [0.16, 1, 0.3, 1]
    val monolithicEasing = remember { CubicBezierEasing(0.16f, 1f, 0.3f, 1f) }

    val leftBarScale = remember { Animatable(0f) }
    val rightBarScale = remember { Animatable(0f) }
    val letterTracking = remember { Animatable(0.18f) }
    val textAlpha = remember { Animatable(0f) }
    val subtextAlpha = remember { Animatable(0f) }
    val rippleProgress = remember { Animatable(0f) }
    val screenAlpha = remember { Animatable(1f) }
    val screenScale = remember { Animatable(1f) }
    val sweepAnim = remember { Animatable(0f) }

    // Play native synthesized harmonic sub-bass & crystal chime
    LaunchedEffect(Unit) {
        launch(Dispatchers.Default) {
            try {
                playHarmonicAudioPulse()
            } catch (_: Throwable) {
                // Audio synthesis fail-safe
            }
        }
    }

    // Sequence animations
    LaunchedEffect(Unit) {
        // Continuous energy sweep loop
        launch {
            sweepAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }

        // Concentric radial echo ripple
        launch {
            rippleProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(1200, easing = FastOutSlowInEasing)
            )
        }

        // Monolithic Twin Bars expansion: staggered 80ms offset
        launch {
            leftBarScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 750, easing = monolithicEasing)
            )
        }
        launch {
            delay(80) // 80ms stagger
            rightBarScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 750, easing = monolithicEasing)
            )
        }

        // Typographic tracking reveal: 0.18em to 0.36em
        launch {
            delay(280)
            textAlpha.animateTo(1f, animationSpec = tween(500))
        }
        launch {
            delay(320)
            letterTracking.animateTo(
                targetValue = 0.36f,
                animationSpec = tween(durationMillis = 850, easing = monolithicEasing)
            )
        }
        launch {
            delay(600)
            subtextAlpha.animateTo(1f, animationSpec = tween(400))
        }

        // Hold reveal, then smoothly dissolve and exit
        delay(2200)
        launch {
            screenScale.animateTo(1.05f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        }
        screenAlpha.animateTo(0f, animationSpec = tween(450, easing = FastOutSlowInEasing))
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GymDarkBg)
            .scale(screenScale.value)
            .alpha(screenAlpha.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onFinished() // Tap anywhere to skip instantly
            }
            .testTag("intro_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Acoustic Radial Echoes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f - 40.dp.toPx())
            val maxRadius = size.width * 0.48f

            if (rippleProgress.value > 0f) {
                // Ring 1
                val r1 = maxRadius * rippleProgress.value
                val a1 = (1f - rippleProgress.value).coerceIn(0f, 0.45f)
                drawCircle(
                    color = GymCyan.copy(alpha = a1),
                    radius = r1,
                    center = centerOffset,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Ring 2 (delayed phase)
                val p2 = ((rippleProgress.value - 0.25f) / 0.75f).coerceIn(0f, 1f)
                if (p2 > 0f) {
                    val r2 = maxRadius * p2
                    val a2 = (1f - p2).coerceIn(0f, 0.3f)
                    drawCircle(
                        color = GymIndigo.copy(alpha = a2),
                        radius = r2,
                        center = centerOffset,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                // Ring 3 (outer harmonic wave)
                val p3 = ((rippleProgress.value - 0.5f) / 0.5f).coerceIn(0f, 1f)
                if (p3 > 0f) {
                    val r3 = maxRadius * p3
                    val a3 = (1f - p3).coerceIn(0f, 0.2f)
                    drawCircle(
                        color = GymEmerald.copy(alpha = a3),
                        radius = r3,
                        center = centerOffset,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Monolithic Twin Bars (||)
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Bar
                MonolithicBar(
                    scaleY = leftBarScale.value,
                    sweepOffset = sweepAnim.value,
                    accentColor = GymCyan
                )

                // Right Bar (Staggered 80ms)
                MonolithicBar(
                    scaleY = rightBarScale.value,
                    sweepOffset = (sweepAnim.value + 0.15f) % 1f,
                    accentColor = GymEmerald
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Typographic Reveal: IRONPULSE wordmark expanding from 0.18em to 0.36em letter tracking
            Text(
                text = "IRONPULSE",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = letterTracking.value.em,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .testTag("intro_brand_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Unboxed Subtext
            Text(
                text = "PERFORMANCE GYM & ATHLETE PROTOCOL",
                color = GymCyan.copy(alpha = 0.9f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.22.em,
                modifier = Modifier.alpha(subtextAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "DYNAMIC SCHEDULING • BIOMETRICS • 1RM PR TRACKING",
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.em,
                modifier = Modifier.alpha(subtextAlpha.value)
            )
        }

        // Tap to skip hint at bottom
        Text(
            text = "TAP TO ENTER",
            color = TextMuted.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(subtextAlpha.value)
        )
    }
}

@Composable
private fun MonolithicBar(
    scaleY: Float,
    sweepOffset: Float,
    accentColor: Color
) {
    val barHeight = 84.dp
    val barWidth = 14.dp

    Box(
        modifier = Modifier
            .width(barWidth)
            .height(barHeight)
            .scale(scaleX = 1f, scaleY = scaleY)
            .clip(RoundedCornerShape(7.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.35f),
                        accentColor,
                        Color.White,
                        GymIndigo,
                        accentColor
                    ),
                    startY = sweepOffset * 250f,
                    endY = (sweepOffset * 250f) + 120f
                )
            )
    )
}

/**
 * Harmonic Web Audio Synthesizer equivalent on Android:
 * Native AudioTrack synthesis generating:
 * - Sub-bass pulse sweep from 65Hz to 110Hz
 * - Crystalline chime overtones at 440Hz & 659Hz
 */
private suspend fun playHarmonicAudioPulse() = withContext(Dispatchers.Default) {
    try {
        val sampleRate = 44100
        val durationSec = 1.3
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = (i.toDouble() / numSamples).coerceIn(0.0, 1.0)

            // Sub-bass sweep: 65Hz to 110Hz
            val freqSub = 65.0 + (110.0 - 65.0) * progress
            val subBass = Math.sin(2.0 * Math.PI * freqSub * t)

            // Soft crystalline chime overtones: 440Hz (A4) and 659Hz (E5)
            val chime440 = Math.sin(2.0 * Math.PI * 440.0 * t) * Math.exp(-3.2 * t)
            val chime659 = Math.sin(2.0 * Math.PI * 659.0 * t) * Math.exp(-3.8 * t)

            // Smooth envelope: short attack then gentle decay
            val attackSec = 0.06
            val envelope = if (t < attackSec) {
                t / attackSec
            } else {
                Math.max(0.0, 1.0 - (t - attackSec) / (durationSec - attackSec))
            }

            val mixed = ((subBass * 0.50 + chime440 * 0.25 + chime659 * 0.20) * envelope)
                .coerceIn(-1.0, 1.0)
            buffer[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
        }

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build(),
            maxOf(buffer.size * 2, minBufferSize),
            AudioTrack.MODE_STATIC,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()

        delay((durationSec * 1000).toLong() + 200)
        audioTrack.stop()
        audioTrack.release()
    } catch (_: Throwable) {
        // Fallback gracefully on devices without direct audio access
    }
}
