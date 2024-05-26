package it.unilix.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.File

class JsonTypeAdapter : TypeAdapter<File>() {
    /**
     * Writes one JSON value (an array, object, string, number, boolean or null)
     * for `value`.
     *
     * @param value the Java object to write. Maybe null.
     */
    override fun write(out: JsonWriter?, value: File?) {
        out?.value(value?.path)
    }

    /**
     * Reads one JSON value (an array, object, string, number, boolean or null)
     * and converts it to a Java object. Returns the converted object.
     *
     * @return the converted Java object. Maybe null.
     */
    override fun read(`in`: JsonReader?): File? {
        return `in`?.nextString()?.let { File(it) }
    }
}