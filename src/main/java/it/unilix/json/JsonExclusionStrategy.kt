package it.unilix.json

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

class JsonExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return false
    }

    override fun shouldSkipField(f: FieldAttributes): Boolean {
        f.annotations.print()
        return f.getAnnotation(JsonExclude::class.java) != null
    }
}