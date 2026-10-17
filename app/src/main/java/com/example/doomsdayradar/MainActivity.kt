package com.example.doomsdayradar

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

// STEP 1: Our Offline Fake Asteroid Blueprint & Test Data
data class MockAsteroid(
    val id: String,
    val name: String,
    val distanceMillionKm: String,
    val speedKmh: String,
    val sizeComparison: String,
    val isHazardous: Boolean,
    val orbitRadiusFactor: Float, // How close the curve gets to Earth
    val angleDegrees: Float       // Where the asteroid sits on the curve
)

val sampleAsteroids = listOf(
    MockAsteroid(
        id = "1",
        name = "ASTEROID 2026 RQ4",
        distanceMillionKm = "4.8 Million km",
        speedKmh = "58,000 km/h",
        sizeComparison = "~120m (Bigger than a Football Stadium!)",
        isHazardous = true,
        orbitRadiusFactor = 0.42f,
        angleDegrees = 195f
    ),
    MockAsteroid(
        id = "2",
        name = "APOPHIS 99942",
        distanceMillionKm = "1.9 Million km",
        speedKmh = "74,200 km/h",
        sizeComparison = "~370m (Taller than the Eiffel Tower!)",
        isHazardous = true,
        orbitRadiusFactor = 0.28f,
        angleDegrees = 140f
    ),
    MockAsteroid(
        id = "3",
        name = "2026 BG4",
        distanceMillionKm = "11.2 Million km",
        speedKmh = "31,500 km/h",
        sizeComparison = "~35m (About 3 School Buses parked end-to-end)",
        isHazardous = false,
        orbitRadiusFactor = 0.65f,
        angleDegrees = 235f
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DoomsdayRadarScreen()
            }
        }
    }
}

// STEP 2: The Main Sci-Fi Screen Layout
@Composable
fun DoomsdayRadarScreen() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val selectedAsteroid = sampleAsteroids[selectedIndex]

    val deepSpaceBg = Color(0xFF040914)
    val neonCyan = Color(0xFF00F5D4)
    val hazardRed = Color(0xFFFF2A54)
    val warningOrange = Color(0xFFFF7B00)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(deepSpaceBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Sci-Fi Header HUD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, neonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .background(Color(0xFF081526).copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                .padding(vertical = 10.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DOOMSDAY THREAT MONITOR",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "● SCENE SCAN ACTIVE (OFFLINE SIM)",
                    color = neonCyan,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // STEP 3: The Interactive Orbital Trajectory Map (Middle of Screen)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            OrbitalTrajectoryCanvas(
                asteroids = sampleAsteroids,
                selectedAsteroid = selectedAsteroid,
                neonCyan = neonCyan,
                hazardRed = hazardRed,
                warningOrange = warningOrange
            )
        }

        // STEP 4: Bottom Threat Detail HUD Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        if (selectedAsteroid.isHazardous) listOf(hazardRed, warningOrange, neonCyan)
                        else listOf(neonCyan, Color.Blue)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF071324))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THREAT DETAIL",
                        color = neonCyan,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (selectedAsteroid.isHazardous) "⚠️ CRITICAL CORRIDOR" else "✓ SAFE FLYBY",
                        color = if (selectedAsteroid.isHazardous) hazardRed else neonCyan,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "INCOMING: ${selectedAsteroid.name}",
                    color = if (selectedAsteroid.isHazardous) hazardRed else Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Miss Distance: ${selectedAsteroid.distanceMillionKm}",
                    color = Color(0xFFB0C4DE),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Speed: ${selectedAsteroid.speedKmh}",
                    color = Color(0xFFB0C4DE),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Size: ${selectedAsteroid.sizeComparison}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button to cycle through fake asteroids!
                OutlinedButton(
                    onClick = {
                        selectedIndex = (selectedIndex + 1) % sampleAsteroids.size
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, neonCyan),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF0B2239)
                    )
                ) {
                    Text(
                        text = "TRAJECTORY SIMULATOR (NEXT ROCK ➔)",
                        color = neonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// STEP 5: Custom Sci-Fi Orbital Canvas
@Composable
fun OrbitalTrajectoryCanvas(
    asteroids: List<MockAsteroid>,
    selectedAsteroid: MockAsteroid,
    neonCyan: Color,
    hazardRed: Color,
    warningOrange: Color
) {
    // Infinite radar sweep animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            isAntiAlias = true
            typeface = android.graphics.Typeface.MONOSPACE
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Place Earth slightly off-center to the right like our sci-fi design!
        val earthCenter = Offset(size.width * 0.62f, size.height * 0.50f)
        val maxRadius = size.minDimension * 0.78f

        // 1. Background perspective telemetry lines
        for (i in -5..5) {
            val angleRad = Math.toRadians((i * 30).toDouble())
            val endX = earthCenter.x + (maxRadius * 1.3f * cos(angleRad)).toFloat()
            val endY = earthCenter.y + (maxRadius * 1.3f * sin(angleRad)).toFloat()
            drawLine(
                color = neonCyan.copy(alpha = 0.08f),
                start = earthCenter,
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // 2. Concentric orbital distance rings around off-center Earth
        for (i in 1..5) {
            val ringRadius = maxRadius * (i / 5f)
            drawCircle(
                color = neonCyan.copy(alpha = if (i % 2 == 0) 0.22f else 0.10f),
                radius = ringRadius,
                center = earthCenter,
                style = Stroke(width = if (i == 3) 2.dp.toPx() else 1.dp.toPx())
            )
        }

        // 3. Red "Hazard Zone" sector wedge on the left approach side
        drawArc(
            brush = Brush.radialGradient(
                colors = listOf(hazardRed.copy(alpha = 0.35f), Color.Transparent),
                center = earthCenter,
                radius = maxRadius
            ),
            startAngle = 145f,
            sweepAngle = 55f,
            useCenter = true,
            topLeft = Offset(earthCenter.x - maxRadius, earthCenter.y - maxRadius),
            size = Size(maxRadius * 2, maxRadius * 2)
        )

        // 4. Sweeping Cyan Scanner Cone
        rotate(degrees = sweepAngle, pivot = earthCenter) {
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to Color.Transparent,
                    0.85f to Color.Transparent,
                    1.0f to neonCyan.copy(alpha = 0.35f),
                    center = earthCenter
                ),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = true,
                topLeft = Offset(earthCenter.x - maxRadius, earthCenter.y - maxRadius),
                size = Size(maxRadius * 2, maxRadius * 2)
            )
        }

        // 5. Curved Orbital Approach Vectors & Asteroid Blips
        asteroids.forEach { asteroid ->
            val isSelected = asteroid.id == selectedAsteroid.id
            val orbitRadius = maxRadius * asteroid.orbitRadiusFactor
            val trackColor = when {
                isSelected && asteroid.isHazardous -> hazardRed
                asteroid.isHazardous -> warningOrange
                else -> neonCyan
            }

            // Draw swooping elliptical trajectory arc
            val arcRectSize = Size(orbitRadius * 2.3f, orbitRadius * 1.85f)
            val arcTopLeft = Offset(
                earthCenter.x - arcRectSize.width * 0.52f,
                earthCenter.y - arcRectSize.height * 0.5f
            )

            drawArc(
                color = trackColor.copy(alpha = if (isSelected) 0.95f else 0.4f),
                startAngle = 95f,
                sweepAngle = 185f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcRectSize,
                style = Stroke(width = if (isSelected) 3.5.dp.toPx() else 1.5.dp.toPx())
            )

            // Calculate asteroid position along that elliptical curve
            val rad = Math.toRadians(asteroid.angleDegrees.toDouble())
            val blipX = arcTopLeft.x + arcRectSize.width / 2f + (arcRectSize.width / 2f) * cos(rad).toFloat()
            val blipY = arcTopLeft.y + arcRectSize.height / 2f + (arcRectSize.height / 2f) * sin(rad).toFloat()
            val blipCenter = Offset(blipX, blipY)

            // Outer glow if selected
            if (isSelected) {
                drawCircle(
                    color = trackColor.copy(alpha = 0.3f),
                    radius = 18.dp.toPx(),
                    center = blipCenter
                )
            }

            // Asteroid Core Dot
            drawCircle(
                color = trackColor,
                radius = if (isSelected) 7.dp.toPx() else 5.dp.toPx(),
                center = blipCenter
            )

            // Sci-Fi Target Reticle Box around the asteroid
            val boxHalf = if (isSelected) 13.dp.toPx() else 9.dp.toPx()
            drawRect(
                color = trackColor.copy(alpha = 0.85f),
                topLeft = Offset(blipCenter.x - boxHalf, blipCenter.y - boxHalf),
                size = Size(boxHalf * 2, boxHalf * 2),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Draw Asteroid Name Label next to the blip
            drawContext.canvas.nativeCanvas.drawText(
                asteroid.name,
                blipCenter.x + 16.dp.toPx(),
                blipCenter.y + 5.dp.toPx(),
                textPaint
            )
        }

        // 6. Draw Glowing Holographic Earth
        // Outer atmospheric glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(neonCyan.copy(alpha = 0.55f), Color.Transparent),
                center = earthCenter,
                radius = 56.dp.toPx()
            ),
            radius = 56.dp.toPx(),
            center = earthCenter
        )
        // Deep blue planet body
        drawCircle(
            color = Color(0xFF0A4D8C),
            radius = 34.dp.toPx(),
            center = earthCenter
        )
        // Holographic wireframe rings on Earth
        drawCircle(
            color = neonCyan,
            radius = 34.dp.toPx(),
            center = earthCenter,
            style = Stroke(width = 2.dp.toPx())
        )
        drawOval(
            color = neonCyan.copy(alpha = 0.5f),
            topLeft = Offset(earthCenter.x - 34.dp.toPx(), earthCenter.y - 14.dp.toPx()),
            size = Size(68.dp.toPx(), 28.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}