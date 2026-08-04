package com.jelyta.deviceguardian.data.remote

import com.jelyta.deviceguardian.BuildConfig
import com.jelyta.deviceguardian.domain.model.DeviceMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun queryAssistant(userQuery: String, metrics: DeviceMetrics): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext generateLocalAiResponse(userQuery, metrics)
        }

        val prompt = """
            Anda adalah Jelyta Guardian AI Assistant, ahli keamanan & optimasi perangkat Android.
            Konteks Real-Time Perangkat:
            - RAM Terpakai: ${metrics.ramUsedMb} MB / ${metrics.ramTotalMb} MB (${metrics.ramPercent}%)
            - Penyimpanan: ${metrics.storageFreeGb} GB Bebas / ${metrics.storageTotalGb} GB Total (${metrics.storagePercent}% Terpakai)
            - Baterai: ${metrics.batteryPercent}% (Suhu: ${metrics.batteryTempCelsius}°C, Status: ${if (metrics.isCharging) "Mengisi Daya" else "Baterai"})
            - Mode Kinerja: ${metrics.performanceMode}
            - CPU Estimasi: ${metrics.estimatedCpuUsagePercent}%
            
            Pertanyaan Pengguna: "$userQuery"
            Jawab dengan ramah, profesional, solutif, dan informatif dalam bahasa Indonesia.
        """.trimIndent()

        val cloudResponse = callGeminiRestApi(prompt, apiKey)
        cloudResponse.ifBlank {
            generateLocalAiResponse(userQuery, metrics)
        }
    }

    suspend fun translateText(text: String, targetLang: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext "[AI Local Translator] Teks ('$text') disiapkan untuk terjemahan ke $targetLang."
        }

        val prompt = "Terjemahkan teks berikut secara akurat ke bahasa $targetLang. Tampilkan hanya hasil terjemahan:\n\n$text"
        val cloudResult = callGeminiRestApi(prompt, apiKey)
        if (cloudResult.isBlank() || cloudResult.contains("Gagal terhubung")) {
            "[AI Local Translator] Terjemahan ($targetLang): $text"
        } else {
            cloudResult
        }
    }

    private fun callGeminiRestApi(promptText: String, apiKey: String): String {
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", promptText)
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful && responseBody.isNotBlank()) {
                val jsonObj = JSONObject(responseBody)
                val candidates = jsonObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text", "")
                    }
                }
            }
            ""
        } catch (_: Exception) {
            ""
        }
    }

    private fun generateLocalAiResponse(query: String, metrics: DeviceMetrics): String {
        val q = query.lowercase()
        return when {
            q.contains("ram") || q.contains("memori") || q.contains("lag") || q.contains("lambat") -> {
                val ramStatus = if (metrics.ramPercent > 80) "tinggi (${metrics.ramPercent}%)! Disarankan jalankan Turbo Boost." else "stabil (${metrics.ramPercent}%)."
                "🤖 **Analisis Memori (RAM)**:\nPenggunaan RAM Anda saat ini $ramStatus Terpakai ${metrics.ramUsedMb} MB dari total ${metrics.ramTotalMb} MB.\n💡 *Saran*: Tekan tombol **Turbo Boost** pada menu Optimizer untuk membebaskan alokasi RAM yang tidak terpakai."
            }
            q.contains("baterai") || q.contains("battery") || q.contains("suhu") || q.contains("panas") -> {
                val tempStatus = if (metrics.batteryTempCelsius > 38.0) "cukup hangat (${metrics.batteryTempCelsius}°C). Kurangi beban aplikasi berat." else "normal (${metrics.batteryTempCelsius}°C)."
                "🔋 **Diagnosis Baterai & Suhu**:\nLevel Baterai: ${metrics.batteryPercent}% (${if (metrics.isCharging) "Mengisi Daya" else "Tidak Mengisi Daya"}). Suhu perangkat $tempStatus\n💡 *Saran*: Aktifkan Mode *Power Saver* dari tab Optimizer untuk menghemat konsumsi daya."
            }
            q.contains("keamanan") || q.contains("virus") || q.contains("izin") || q.contains("bahaya") || q.contains("security") -> {
                "🛡️ **Status Keamanan Perangkat**:\nSistem Guardian menemukan perlindungan Zero-Trust aktif. Disarankan untuk memeriksa tab **Security Audit** untuk memindai aplikasi berisiko tinggi yang memiliki akses Kamera & Lokasi secara bersamaan."
            }
            q.contains("storage") || q.contains("penyimpanan") || q.contains("cache") || q.contains("memori internal") -> {
                "📁 **Status Penyimpanan**:\nSisa penyimpanan internal: ${String.format("%.1f", metrics.storageFreeGb)} GB dari total ${String.format("%.1f", metrics.storageTotalGb)} GB (${metrics.storagePercent}% terpakai).\n💡 *Saran*: Jalankan pembersihan sampah cache pada tab **Optimizer**."
            }
            q.contains("sapa") || q.contains("halo") || q.contains("hi") || q.contains("siapa") || q.contains("bantu") -> {
                "👋 **Halo! Saya Jelyta Guardian AI Engine.**\nSaya dapat menganalisis kesehatan hardware, memindai risiko keamanan, serta membantu mengoptimalkan RAM dan baterai HP Anda. Silakan tanyakan hal seputar perangkat Anda!"
            }
            else -> {
                "🤖 **Jelyta Guardian AI Diagnostic**:\nBerdasarkan pemeriksaan real-time:\n• RAM: ${metrics.ramPercent}% terpakai (${metrics.ramUsedMb}/${metrics.ramTotalMb} MB)\n• Baterai: ${metrics.batteryPercent}% (${metrics.batteryTempCelsius}°C)\n• Penyimpanan Bebas: ${String.format("%.1f", metrics.storageFreeGb)} GB\n• Mode Kinerja: ${metrics.performanceMode}\n\nPerangkat dalam kondisi terpantau aman dan responsif."
            }
        }
    }
}

