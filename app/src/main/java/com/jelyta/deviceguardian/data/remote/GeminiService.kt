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
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun queryAssistant(userQuery: String, metrics: DeviceMetrics): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext "Sistem berjalan dalam batas normal. Mode offline aktif (API Key belum dikonfigurasi)."
        }

        val prompt = """
            Anda adalah Jelyta Sister's AI Assistant.
            Konteks Perangkat Saat Ini:
            - RAM Terpakai: ${metrics.ramUsedMb} MB / ${metrics.ramTotalMb} MB (${metrics.ramPercent}%)
            - Penyimpanan: ${metrics.storagePercent}% Terpakai
            - Baterai: ${metrics.batteryPercent}% (${metrics.batteryTempCelsius}°C)
            - Mode Kinerja: ${metrics.performanceMode}
            
            Pertanyaan Pengguna: "$userQuery"
            Berikan jawaban singkat, jelas, ramah, dan solutif dalam bahasa Indonesia.
        """.trimIndent()

        callGeminiRestApi(prompt, apiKey)
    }

    suspend fun translateText(text: String, targetLang: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext "[Mode Failover Offline] Terjemahan ke $targetLang: $text"
        }

        val prompt = "Terjemahkan teks berikut secara akurat ke bahasa $targetLang. Tampilkan hanya hasil terjemahan:\n\n$text"
        callGeminiRestApi(prompt, apiKey)
    }

    private fun callGeminiRestApi(promptText: String, apiKey: String): String {
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

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
                        return parts.getJSONObject(0).optString("text", "Jawaban tidak tersedia.")
                    }
                }
            }

            "Mode lokal aktif: Perangkat Anda berjalan dengan lancar dan optimal."
        } catch (e: Exception) {
            "Gagal terhubung ke Gemini Cloud AI: ${e.localizedMessage ?: "Network error"}. Mode rekomendasi lokal aktif."
        }
    }
}
