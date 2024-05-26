package it.unilix.json

import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import java.lang.IndexOutOfBoundsException
import java.util.HashMap

class JsonObject(private var value: Any? = null) {
    companion object {
        /**
         * Create a JsonObject from a map or a list
         */
        infix fun cast(value: Any?): JsonObject {
            if(value is Map<*,*>) {
                return JsonObject(value)
            }else if(value is List<*>) {
                return JsonObject(value)
            }

            return JsonString.fromString(value.toString())
        }
    }

    private val gson = GsonBuilder().addSerializationExclusionStrategy(JsonExclusionStrategy()).setPrettyPrinting().create()

    /**
     * Check if the value is a map
     */
    private fun isMap(): Boolean {
        return value is Map<*, *> || (value is JsonObject && (value as JsonObject).value is Map<*, *>)
    }

    private fun isList(): Boolean {
        return value is List<*> || (value is JsonObject && (value as JsonObject).value is List<*>)
    }

    /**
     * Get the value of the key
     * @param key the key
     * @return the value of the key
     */
    operator fun get(key: String): JsonObject {
        if(value is List<*>) {
            val list = value as List<*>
            if(list.isEmpty())
                value = arrayListOf<Any?>()
            val map = linkedMapOf(key to linkedMapOf<String, Any?>())
            (value as ArrayList<Any?>).add(map)
            return JsonObject(map)
        }

        if(!isMap()) {
            val map = linkedMapOf(key to linkedMapOf<String, Any?>())
            this.value = map
            return JsonObject(map)
        }
        var v = this.value
        if(v is JsonObject)
            v = (this.value as JsonObject).get()
        val map = try { (v as HashMap<String, Any?>).toLinkedTreeMap() } catch (_: Exception) { v as LinkedTreeMap<String, Any?> }
        if(!map.containsKey(key)) {
            map[key] = linkedMapOf<String, Any?>().toLinkedTreeMap()
        }
        this.value = map
        return JsonObject(map[key] ?: linkedMapOf<String, Any?>().toLinkedTreeMap())
    }

    /**
     * Get the value of the index
     * @param index the index
     * @return the value of the index
     */
    operator fun get(index: Int): JsonObject {
        if(value is List<*>) {
            if((value as List<*>).isEmpty())
                value = arrayListOf<Any?>()
            if((value as List<*>).size <= index)
                try { (value as ArrayList<Any?>).add(index, linkedMapOf<String, Any?>().toLinkedTreeMap()) }
                catch (_: IndexOutOfBoundsException) { (value as ArrayList<Any?>).addAll(listOf(linkedMapOf<String, Any?>().toLinkedTreeMap())) }
            return JsonObject((value as List<*>)[index])
        }
        return JsonObject(emptyList<Any?>())
    }

    /**
     * Set the value of the key
     * @param key the key
     * @param value the value
     */
    operator fun set(key: String, value: Any?) {
        if(isMap()) {
            var v = this.value
            if(v is JsonObject)
                v = (this.value as JsonObject).get()
            else if(v is HashMap<*, *>)
                v = v.toLinkedTreeMap()
            else if(v is String && isStringAMap(v))
                v = gson.fromJson(v, LinkedTreeMap::class.java as Class<LinkedTreeMap<String, Any>>)

            (v as LinkedTreeMap<String, Any>)[key] = value
            this.value = v
        }else {
            val map = linkedMapOf(key to value)
            this.value = map
        }
    }

    private fun isStringAMap(string: String): Boolean {
        return string.startsWith("{") && string.endsWith("}")
    }

    /**
     * Set the value of the index
     * @param index the index
     * @param value the value
     */
    operator fun set(index: Int, value: Any) {
        if(this.value is List<*>) {
            (this.value as ArrayList<Any>)[index] = value
        }
    }

    /**
     * Get the value of the key
     * @param key the key
     * @param default the default value
     * @return the value of the key
     */
    fun get(key: String, default: Any?): Any? {
        return (value as Map<*, *>).getOrDefault(key, default)
    }

    /**
     * Gets the value of the JsonObject
     * @return the value
     */
    fun get(): Any? {
        return value
    }

    /**
     * Check if the value is null
     * @return true if the value is null, false otherwise
     */
    fun isNull(): Boolean {
        return value == null
    }

    operator fun plus(other: JsonObject): JsonObject {
        if(isMap() && other.isMap()) {
            val map = value as Map<*, *>
            val otherMap = other.value as Map<*, *>
            val newMap = LinkedHashMap(map)
            newMap.putAll(otherMap)
            return JsonObject(newMap)
        }
        return JsonObject(null)
    }

    operator fun minus(other: JsonObject): JsonObject {
        if(isMap() && other.isMap()) {
            val map = value as Map<*, *>
            val otherMap = other.value as Map<*, *>
            val newMap = LinkedHashMap(map)
            newMap.keys.removeAll(otherMap.keys)
            return JsonObject(newMap)
        }
        return JsonObject(null)
    }

    operator fun plus(number: Number): JsonObject {
        if(value is Number) {
            return JsonObject((value as Number).toDouble() + number.toDouble())
        }
        return JsonObject(null)
    }

    operator fun minus(number: Number): JsonObject {
        if(value is Number) {
            return JsonObject((value as Number).toDouble() - number.toDouble())
        }
        return JsonObject(null)
    }

    operator fun times(number: Number): JsonObject {
        if(value is Number) {
            return JsonObject((value as Number).toDouble() * number.toDouble())
        }
        return JsonObject(null)
    }

    operator fun div(number: Number): JsonObject {
        if(value is Number) {
            return JsonObject((value as Number).toDouble() / number.toDouble())
        }
        return JsonObject(null)
    }

    fun toJsonString(): String {
        return gson.toJson(value)
    }

    override fun toString(): String {
        return gson.toJson(value)
    }

    operator fun plus(hashMapOf: Map<String, String>): JsonObject {
        if(isMap()) {
            val map = value as Map<*, *>
            val newMap = LinkedHashMap(map)
            newMap.putAll(hashMapOf)
            return JsonObject(newMap)
        }
        return JsonObject(null)
    }

    /**
     * Convert the value to the specified type
     * @param type the type
     * @return the value converted to the specified type
     */
    fun <T> to(type: Class<T>): T {
        return gson.fromJson(gson.toJson(value), type)
    }

    @JvmName("containsMap")
    operator fun contains(key: String): Boolean {
        return if(isMap()) {
            (value as Map<*, *>).containsKey(key)
        }else {
            false
        }
    }

    @JvmName("containsList")
    operator fun contains(value: Any): Boolean {
        return if(this.value is List<*>) {
            (this.value as List<*>).contains(value)
        }else {
            false
        }
    }

    operator fun plus(listOf: List<Int>): JsonObject {
        if(value is List<*>) {
            val list = value as List<*>
            val newList = ArrayList(list)
            newList.addAll(listOf)
            return JsonObject(newList)
        }
        return JsonObject(null)
    }

    operator fun rem(number: Number): JsonObject {
        if(value is Number) {
            return JsonObject((value as Number).toDouble() % number.toDouble())
        }
        return JsonObject(null)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonObject

        return value == other.value
    }

    fun size(): Int {
        return if(value is List<*>) (value as List<*>).size else if(value is Map<*,*>) (value as Map<*,*>).size else 0
    }

    fun forEach(action: (String, Any?) -> Unit) {
        if(isMap()) {
            val map = value as Map<*, *>
            map.forEach { (key, value) ->
                action(key as String, value)
            }
        }
    }

    fun forEachIndexed(action: (Int, Any?) -> Unit) {
        if(value is List<*>) {
            val list = value as List<*>
            list.forEachIndexed { index, value ->
                action(index, value)
            }
        }
    }

    fun forEach(action: (Any?) -> Unit) {
        if(value is List<*>) {
            val list = value as List<*>
            list.forEach { value ->
                action(value)
            }
        }
    }

    fun filter(predicate: (Any?) -> Boolean): JsonObject {
        if(value is List<*>) {
            val list = value as List<*>
            return JsonObject(list.filter(predicate))
        }else if(value is Map<*,*>) {
            val map = value as Map<*,*>
            return JsonObject(map.filterValues(predicate))
        }

        return JsonObject(null)
    }

    fun filterIndexed(predicate: (Int, Any?) -> Boolean): JsonObject {
        if(value is List<*>) {
            val list = value as List<*>
            return JsonObject(list.filterIndexed(predicate))
        }
        return JsonObject(null)
    }

    fun map(transform: (Any?) -> Any?): JsonObject {
        if(value is List<*>) {
            val list = value as List<*>
            return JsonObject(list.map(transform))
        }else if(value is Map<*,*>) {
            val map = value as Map<*,*>
            return JsonObject(map.mapValues { transform(it.value) })
        }
        return JsonObject(null)
    }

    fun computeIfAbsent(key: String, defaultValue: () -> Any?): JsonObject {
        if(isMap()) {
            val map = value as LinkedTreeMap<String, Any?>
            if(!map.containsKey(key)) {
                map[key] = defaultValue()
            }
            return JsonObject(map)
        }
        return JsonObject(null)
    }

    fun computeIfAbsent(index: Int, defaultValue: () -> Any?): JsonObject {
        if(value is List<*>) {
            val list = value as ArrayList<Any?>
            if(list.size <= index) {
                list.add(index, defaultValue())
            }
            return JsonObject(list)
        }
        return JsonObject(null)
    }

    fun remove(key: String): JsonObject {
        if(isMap()) {
            val map = value as LinkedTreeMap<String, Any?>
            map.remove(key)
            return JsonObject(map)
        }
        return JsonObject(null)
    }

    fun remove(index: Int): JsonObject {
        if(value is List<*>) {
            val list = value as ArrayList<Any?>
            list.removeAt(index)
            return JsonObject(list)
        }
        return JsonObject(null)
    }

    fun clear(): JsonObject {
        if(isMap()) {
            val map = value as LinkedTreeMap<String, Any?>
            map.clear()
            return JsonObject(map)
        }else if(value is List<*>) {
            val list = value as ArrayList<Any?>
            list.clear()
            return JsonObject(list)
        }
        return JsonObject(null)
    }

    fun putAll(map: Map<String, Any?>): JsonObject {
        if(isMap()) {
            val m = value as LinkedTreeMap<String, Any?>
            m.putAll(map)
            return JsonObject(m)
        }
        return JsonObject(null)
    }

    fun putAll(jsonObject: JsonObject): JsonObject {
        if(jsonObject.isMap()) {
            val map = jsonObject.get() as Map<String, Any?>
            return putAll(map)
        }
        return JsonObject(null)
    }

    fun toMap(): Map<String, Any?> {
        if(isMap()) {
            return value as Map<String, Any?>
        }
        return linkedMapOf()
    }

    fun toList(): List<Any?> {
        if(value is List<*>) {
            return value as List<Any?>
        }
        return arrayListOf()
    }

    fun toLinkedTreeMap(): LinkedTreeMap<String, Any?> {
        if(isMap()) {
            return value as LinkedTreeMap<String, Any?>
        }
        return LinkedTreeMap()
    }

    fun toArrayList(): ArrayList<Any?> {
        if(value is List<*>) {
            return value as ArrayList<Any?>
        }
        return arrayListOf()
    }

    fun toJsonObject(): JsonObject {
        return this
    }

    fun computeIfPresent(key: String, action: (String, Any?) -> Any?): JsonObject {
        if(isMap()) {
            val map = value as LinkedTreeMap<String, Any?>
            if(map.containsKey(key)) {
                map[key] = action(key, map[key])
            }
            return JsonObject(map)
        }
        return JsonObject(null)
    }

    fun computeIfPresent(index: Int, action: (Int, Any?) -> Any?): JsonObject {
        if(value is List<*>) {
            val list = value as ArrayList<Any?>
            if(list.size > index) {
                list[index] = action(index, list[index])
            }
            return JsonObject(list)
        }
        return JsonObject(null)
    }

    @JvmName("computeIfPresentString")
    fun merge(other: JsonObject, action: (String, Any?, Any?) -> Any?): JsonObject {
        if(isMap() && other.isMap()) {
            val map = value as LinkedTreeMap<String, Any?>
            val otherMap = other.get() as Map<String, Any?>
            otherMap.forEach { (key, value) ->
                if(map.containsKey(key)) {
                    map[key] = action(key, map[key], value)
                }else {
                    map[key] = value
                }
            }
            return JsonObject(map)
        }
        return JsonObject(null)
    }

    @JvmName("computeIfPresentInt")
    fun merge(other: JsonObject, action: (Int, Any?, Any?) -> Any?): JsonObject {
        if(value is List<*> && other.value is List<*>) {
            val list = value as ArrayList<Any?>
            val otherList = other.value as List<Any?>
            otherList.forEachIndexed { index, value ->
                if(list.size > index) {
                    list[index] = action(index, list[index], value)
                }else {
                    list.add(index, value)
                }
            }
            return JsonObject(list)
        }
        return JsonObject(null)
    }

    fun merge(other: JsonObject): JsonObject {
        if(isMap() && other.isMap()) {
            val map = value as LinkedTreeMap<String, Any?>
            val otherMap = other.get() as Map<String, Any?>
            otherMap.forEach { (key, value) ->
                map[key] = value
            }
            return JsonObject(map)
        }
        return JsonObject(null)
    }

    @JvmName("mergeString")
    fun merge(other: JsonObject, action: (String, Any?) -> Any?): JsonObject {
        if(isMap() && other.isMap()) {
            val map = value as LinkedTreeMap<String, Any?>
            val otherMap = other.get() as Map<String, Any?>
            otherMap.forEach { (key, value) ->
                map[key] = action(key, value)
            }
            return JsonObject(map)
        }
        return JsonObject(null)
    }

    @JvmName("mergeInt")
    fun merge(other: JsonObject, action: (Int, Any?) -> Any?): JsonObject {
        if(value is List<*> && other.value is List<*>) {
            val list = value as ArrayList<Any?>
            val otherList = other.value as List<Any?>
            otherList.forEachIndexed { index, value ->
                list[index] = action(index, value)
            }
            return JsonObject(list)
        }
        return JsonObject(null)
    }

    @JvmName("computeString")
    fun compute(key: String, action: (String, Any?) -> Any?): JsonObject {
        if(isMap()) {
            val map = value as LinkedTreeMap<String, Any?>
            map[key] = action(key, map[key])
            return JsonObject(map)
        }
        return JsonObject(null)
    }

    @JvmName("computeInt")
    fun compute(index: Int, action: (Int, Any?) -> Any?): JsonObject {
        if(value is List<*>) {
            val list = value as ArrayList<Any?>
            list[index] = action(index, list[index])
            return JsonObject(list)
        }
        return JsonObject(null)
    }
}