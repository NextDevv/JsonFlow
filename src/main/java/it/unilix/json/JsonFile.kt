package it.unilix.json

import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import java.io.File
import java.nio.file.Path

class JsonFile(val path: String) {
    private val filePAthSeparator = File.separator
    val content: LinkedHashMap<String, JsonObject> = linkedMapOf()
    private val gson = GsonBuilder().addSerializationExclusionStrategy(JsonExclusionStrategy()).setPrettyPrinting().create()

    constructor(parent: String, name: String) : this("$parent${File.separator}$name")
    constructor(file: File) : this(file.path)
    constructor(path: Path) : this(path.toString())

    /**
     * Check if the file exists
     * @return true if the file exists, false otherwise
     */
    fun exists(): Boolean {
        return File(path).exists()
    }

    /**
     * Create the file
     * @return true if the file was created, false otherwise
     */
    fun create(): Boolean {
        val file = File(path)
        if(file.parentFile != null && !file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        return File(path).createNewFile()
    }

    /**
     * Create the file if it does not exist
     * @return true if the file was created, false otherwise
     */
    fun createIfNotExists(): Boolean {
        return if (!exists()) create() else true
    }

    /**
     * Loads the file content into the content map
     * @return the JsonFile instance
     * @throws Exception if the file does not exist
     */
    fun load(): JsonFile {
        if(exists()) {
            val content = File(path).readText()
            val map = gson.fromJson(content, LinkedHashMap::class.java) ?: return this
            map.forEach { (key, value) ->
                this.content[key.toString()] = JsonObject(value)
            }
        }else {
            throw Exception("File not found")
        }

        return this
    }

    /**
     * Save the content map into the file
     * @return the JsonFile instance
     */
    fun save(): JsonFile {
        val content = gson.toJson(this.content.mapValues {
            var value = it.value.get()
            if(value is JsonObject) {
                value = value.get()
            }
            value
        })
        File(path).writeText(content)
        return this
    }

    private fun isMap(value: Any?): Boolean {
        return value is Map<*, *> || (value is JsonObject && value.get() is Map<*, *>)
    }

    /**
     * Get the value of the key
     * @param key the key
     * @return the value
     */
    operator fun get(key: String): JsonObject {
        return if(content.containsKey(key)) {
            content[key]!!
        }else {
            content[key] = JsonObject(hashMapOf<String, Any?>())
            content[key]!!
        }
    }

    inline fun <reified T> getObj(key: String): T {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(content[key].toString(), T::class.java)
    }

    fun getObj2(key: String, clazz: Class<*>): Any {
        return gson.fromJson(content[key].toString(), clazz)
    }

    fun remove(key: String) {
        content.remove(key)
    }

    fun putAll(map: Map<String, Any?>) {
        map.forEach { (key, value) ->
            val v = if(value is JsonObject) value.get() else value
            content[key] = JsonObject(v)
        }
    }

    fun putAll(jsonObject: JsonObject) {
        if(jsonObject.get() is Map<*,*>)
            putAll(jsonObject.get() as Map<String, Any?>)
    }

    fun clear() {
        content.clear()
    }

    operator fun set(s: String, value: Any?) {
        var v = value
        if(v is JsonObject) v = v.get()
        content[s] = JsonObject(v)
    }

    override fun toString(): String {
        return gson.toJson(content.mapValues {
            var value = it.value.get()
            if(value is JsonObject) {
                value = value.get()
            }
            value
        })
    }

    fun use(block: (JsonFile) -> Unit) {
        block(this)
    }

    fun setFileObj(obj: Any) {
        clear()
        val map = gson.fromJson(gson.toJson(obj), LinkedHashMap::class.java)
        map.forEach { (key, value) ->
            content[key.toString()] = JsonObject(value)
        }
    }

    fun getFileObj(clazz: Class<*>): Any {
        return gson.fromJson(toString(), clazz)
    }

    inline fun <reified T> getFileObj(): T {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(toString(), T::class.java)
    }
}

fun Map<*,*>.toLinkedTreeMap(): LinkedTreeMap<String, Any?> {
    val map = LinkedTreeMap<String, Any?>()
    this.forEach { (key, value) ->
        map[key.toString()] = value
    }
    return map
}

fun Any?.print() {
    println(this)
}