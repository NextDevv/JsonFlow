package it.unilix.json

import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap

class JsonString {
    companion object {
        private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
        @JvmStatic
        fun fromString(string: String): JsonObject {
            return try { JsonObject(gson.fromJson(string, LinkedTreeMap::class.java)) } catch (_: Exception) { JsonObject(
                linkedMapOf("arraylist" to gson.fromJson(string, ArrayList::class.java))) }
        }
    }
}