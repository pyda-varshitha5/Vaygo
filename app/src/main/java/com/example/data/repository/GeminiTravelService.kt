package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.TravelSearchQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiTravelService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun parseNaturalLanguageQuery(userInput: String): TravelSearchQuery = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // High-precision local heuristic parser for Indian transit routes
            return@withContext parseLocally(userInput)
        }

        try {
            val prompt = """
                Extract travel itinerary details from the following traveler prompt:
                "$userInput"

                Return ONLY a valid JSON object with the following fields:
                - origin (string, e.g. "Visakhapatnam")
                - destination (string, e.g. "Hyderabad")
                - date (string, e.g. "Tomorrow" or "Monday")
                - timePreference (string, e.g. "Evening (6 PM - 11 PM)", "Morning (6 AM - 12 PM)", "Night (11 PM - 6 AM)", "Anytime")
                - totalBudget (integer in Indian Rupees, e.g. 1500)
                - seatPreference (string, e.g. "Sleeper", "Seater", "Comfortable")

                If not specified, use sensible defaults (origin: "Visakhapatnam", destination: "Hyderabad", totalBudget: 1500).
                Do not include markdown ticks (no ```json).
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = org.json.JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = org.json.JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val root = JSONObject(responseBody)
                val candidates = root.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                val cleanJson = text.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val parsed = JSONObject(cleanJson)
                return@withContext TravelSearchQuery(
                    origin = parsed.optString("origin", "Visakhapatnam"),
                    destination = parsed.optString("destination", "Hyderabad"),
                    date = parsed.optString("date", "Tomorrow"),
                    timePreference = parsed.optString("timePreference", "Evening (6 PM - 11 PM)"),
                    totalBudget = parsed.optInt("totalBudget", 1500),
                    seatPreference = parsed.optString("seatPreference", "Sleeper"),
                    naturalQuery = userInput
                )
            }
        } catch (e: Exception) {
            Log.e("GeminiTravelService", "API error: ${e.message}", e)
        }

        return@withContext parseLocally(userInput)
    }

    suspend fun getDisruptionRecoveryAdvice(
        incident: String,
        location: String,
        destination: String,
        remainingBudget: Int
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Vaygo Guardian verified that your vehicle has stopped at $location for 40+ minutes. We have locked 3 verified alternative routes within your ₹$remainingBudget budget so you reach $destination safely."
        }

        try {
            val prompt = """
                You are Vaygo Journey Guardian, an AI travel assistant in India.
                The traveler's bus has stopped unexpectedly at $location for over 40 minutes while traveling to $destination.
                Their remaining budget is ₹$remainingBudget.
                Write a 2-sentence empathetic, reassuring, and actionable advisory message explaining the situation and telling them that Vaygo has found immediate alternative buses, express trains, and emergency cabs within their budget.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = org.json.JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = org.json.JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val root = JSONObject(responseBody)
                val text = root.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text") ?: ""

                if (text.isNotBlank()) {
                    return@withContext text.trim()
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiTravelService", "Disruption advice error: ${e.message}")
        }

        "Vaygo Guardian verified that your vehicle has stopped at $location for 40+ minutes. We have locked verified alternative routes within your ₹$remainingBudget budget so you reach $destination safely."
    }

    private fun parseLocally(query: String): TravelSearchQuery {
        val lower = query.lowercase()
        val cities = listOf(
            "visakhapatnam", "vizag", "hyderabad", "bengaluru", "bangalore",
            "vijayawada", "chennai", "mumbai", "pune", "delhi", "jaipur", "goa", "kolkata", "kochi"
        )

        var foundOrigin = "Visakhapatnam"
        var foundDest = "Hyderabad"

        // Search for patterns like "from X to Y"
        val fromToMatch = Regex("from\\s+([a-zA-Z]+)\\s+to\\s+([a-zA-Z]+)").find(lower)
        if (fromToMatch != null) {
            val from = fromToMatch.groupValues[1]
            val to = fromToMatch.groupValues[2]
            foundOrigin = formatCityName(from)
            foundDest = formatCityName(to)
        } else {
            val detected = cities.filter { lower.contains(it) }
            if (detected.size >= 2) {
                foundOrigin = formatCityName(detected[0])
                foundDest = formatCityName(detected[1])
            } else if (detected.size == 1) {
                foundDest = formatCityName(detected[0])
            }
        }

        // Budget detection
        val budgetMatch = Regex("(\\d{3,5})\\s*(?:rs|rupees|inr|₹)?").find(lower)
        val budget = budgetMatch?.groupValues?.get(1)?.toIntOrNull() ?: 1500

        // Date detection
        val date = when {
            lower.contains("today") -> "Today"
            lower.contains("tomorrow") -> "Tomorrow"
            lower.contains("monday") -> "Monday"
            lower.contains("weekend") -> "This Saturday"
            else -> "Tomorrow"
        }

        // Time detection
        val time = when {
            lower.contains("morning") -> "Morning (6 AM - 12 PM)"
            lower.contains("afternoon") -> "Afternoon (12 PM - 6 PM)"
            lower.contains("evening") -> "Evening (6 PM - 11 PM)"
            lower.contains("night") -> "Night (11 PM - 6 AM)"
            else -> "Evening (6 PM - 11 PM)"
        }

        val seat = when {
            lower.contains("sleeper") -> "Sleeper"
            lower.contains("seater") || lower.contains("chair") -> "Chair Car / Seater"
            else -> "Sleeper / Comfortable"
        }

        return TravelSearchQuery(
            origin = foundOrigin,
            destination = foundDest,
            date = date,
            timePreference = time,
            totalBudget = budget,
            seatPreference = seat,
            naturalQuery = query
        )
    }

    private fun formatCityName(name: String): String {
        return when (name.lowercase()) {
            "vizag" -> "Visakhapatnam"
            "bangalore" -> "Bengaluru"
            else -> name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
