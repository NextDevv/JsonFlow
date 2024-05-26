package it.unilix.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import it.unilix.json.JsonObject

class YamlString(private val value: String) {
    fun get(): String {
        return value
    }

    fun cast(): YamlObject {
        val yaml = YAMLFactory()
        val parser = yaml.createParser(value)
        val mapper = ObjectMapper(yaml)
        val map = try { mapper.readValue(parser, LinkedHashMap::class.java) ?: return YamlObject(mapOf<String, Any?>()) } catch (e: Exception) { return YamlObject(mapOf<String, Any?>()) }
        return YamlObject(map)
    }
}