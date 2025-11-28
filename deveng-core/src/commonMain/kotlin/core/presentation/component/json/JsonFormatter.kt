package core.presentation.component.json


import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

fun formatJson(
    unFormattedJson: String
): String {
    return try {
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        val jsonElement = Json.parseToJsonElement(unFormattedJson)
        json.encodeToString(JsonElement.serializer(), jsonElement)
    } catch (e: Exception) {
        unFormattedJson
    }
}