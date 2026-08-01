package com.jelyta.deviceguardian.data.remote

import com.jelyta.deviceguardian.domain.model.DeviceMetrics
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class FastApiDeviceSyncRequest(
    @Json(name = "device_id") val deviceId: String,
    @Json(name = "health_score") val healthScore: Int,
    @Json(name = "cpu_usage") val cpuUsage: Int,
    @Json(name = "ram_usage") val ramUsage: Int,
    @Json(name = "storage_usage") val storageUsage: Int,
    @Json(name = "battery_percent") val batteryPercent: Int,
    @Json(name = "battery_temp") val batteryTemp: Float,
    @Json(name = "performance_mode") val performanceMode: String
)

@JsonClass(generateAdapter = true)
data class FastApiDeviceSyncResponse(
    @Json(name = "status") val status: String,
    @Json(name = "synced_at") val syncedAt: String,
    @Json(name = "message") val message: String
)

interface FastApiService {
    @GET("/api/v1/health")
    suspend fun healthCheck(): Map<String, String>

    @POST("/api/v1/device/sync")
    suspend fun syncDeviceMetrics(@Body request: FastApiDeviceSyncRequest): FastApiDeviceSyncResponse

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8000" // Default Android Emulator host bridge

        fun create(): FastApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(FastApiService::class.java)
        }
    }
}
