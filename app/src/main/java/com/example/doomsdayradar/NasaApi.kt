package com.example.doomsdayradar

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class NasaFeedResponse(
    val near_earth_objects: Map<String, List<NasaAsteroidRaw>>
)

data class NasaAsteroidRaw(
    val id: String,
    val name: String,
    val estimated_diameter: DiameterContainer,
    val is_potentially_hazardous_asteroid: Boolean,
    val close_approach_data: List<CloseApproachData>
)

data class DiameterContainer(val meters: MetersData)
data class MetersData(val estimated_diameter_max: Double)

data class CloseApproachData(
    val relative_velocity: VelocityData,
    val miss_distance: DistanceData
)
data class VelocityData(val kilometers_per_hour: String)
data class DistanceData(val kilometers: String)

interface NasaService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = "DEMO_KEY"
    ): NasaFeedResponse
}

suspend fun fetchTodayAsteroids(): List<Asteroid> {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(NasaService::class.java)
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val response = service.getAsteroids(startDate = today, endDate = today)
    val rawList = response.near_earth_objects.values.flatten()

    return rawList.take(6).mapIndexed { index, raw ->
        val sizeMeters = raw.estimated_diameter.meters.estimated_diameter_max.roundToInt()
        val approach = raw.close_approach_data.firstOrNull()

        val rawKm = approach?.miss_distance?.kilometers?.toDoubleOrNull() ?: 5000000.0
        val millionKm = String.format(Locale.US, "%.1f Million km", rawKm / 1_000_000.0)

        val speed = approach?.relative_velocity?.kilometers_per_hour
            ?.toDoubleOrNull()?.roundToInt() ?: 45000

        val funComparison = when {
            sizeMeters < 15 -> "~${sizeMeters}m (Size of a Double-Decker Bus!)"
            sizeMeters < 50 -> "~${sizeMeters}m (About 4 School Buses parked end-to-end!)"
            sizeMeters < 100 -> "~${sizeMeters}m (Taller than the Taj Mahal!)"
            sizeMeters < 250 -> "~${sizeMeters}m (Bigger than a Football Stadium!)"
            else -> "~${sizeMeters}m (Taller than the Eiffel Tower!!)"
        }

        val normalizedRadius = ((rawKm / 75_000_000.0).toFloat() + 0.22f).coerceIn(0.25f, 0.72f)
        val angle = 125f + (index * 22f)

        Asteroid(
            id = raw.id,
            name = "ASTEROID ${raw.name.replace("(", "").replace(")", "")}",
            distanceMillionKm = millionKm,
            speedKmh = "$speed km/h",
            sizeComparison = funComparison,
            isHazardous = raw.is_potentially_hazardous_asteroid,
            orbitRadiusFactor = normalizedRadius,
            angleDegrees = angle
        )
    }
}