package it.unilix.yaml

class YamlObject(private var value: Any?) {
    fun get(): Any? {
        return value
    }

    operator fun get(key: String): YamlObject {
        if(value !is Map<*, *>) {
            value = mutableMapOf<String, Any?>(key to mapOf<String, Any?>())
            return YamlObject(value)
        }
        var v = this.value
        if(v is YamlObject) v = v.get()
        val map = (v as Map<*, *>).toMutableMap()
        if(!map.containsKey(key)) {
            map[key] = mapOf<String, Any?>()
        }
        this.value = map

        return YamlObject(map[key] ?: mapOf<String, Any?>())
    }

    operator fun get(key: String, default: Any?): YamlObject {
        if(get(key).get() is Map<*, *> && (get(key).get() as Map<*, *>).isEmpty()) {
            set(key, default)
            return get(key)
        }else {
            return get(key)
        }
    }

    operator fun get(index: Int): YamlObject {
        if(value !is List<*>) {
            value = mutableListOf(mutableMapOf<String, Any?>())
            return YamlObject((value as MutableList<*>)[index] ?: mutableMapOf<String, Any?>())
        }
        var v = this.value
        if(v is YamlObject) v = v.get()
        val list = (v as List<*>).toMutableList()
        if(list.size <= index) {
            list.add(mutableMapOf<String, Any?>())
        }
        this.value = list

        return YamlObject(list[index])
    }

    operator fun set(key: String, value: Any?) {
        if(this.value is Map<*, *>) {
            try { (this.value as MutableMap<String, Any?>)[key] = value }
            catch (e: ClassCastException) {
                this.value = mutableMapOf(key to value)
                (this.value as MutableMap<String, Any?>)[key] = value
            }
        }
    }

    operator fun set(index: Int, value: Any?) {
        if(this.value is List<*>) {
            (this.value as MutableList<Any?>)[index] = value
        }
    }

    fun keys(): Set<String> {
        return (value as? Map<*, *>)?.keys?.map { it.toString() }?.toSet() ?: setOf()
    }

    fun size(): Int {
        return (value as? Map<*, *>)?.size ?: ((value as? List<*>)?.size ?: 0)
    }
}