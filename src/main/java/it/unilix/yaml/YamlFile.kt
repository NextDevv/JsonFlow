package it.unilix.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import it.unilix.json.JsonFile
import it.unilix.json.JsonObject
import java.io.File

class YamlFile(val path: String) {
    constructor(parent: String, name: String) : this("$parent${File.separator}$name")
    constructor(file: File) : this(file.path)

    val content = linkedMapOf<String, YamlObject>()
    private var header = ""
    private var footer = ""

    fun exists(): Boolean {
        return File(path).exists()
    }

    fun create(): Boolean {
        val file = File(path)
        if(file.parentFile != null && !file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        return File(path).createNewFile()
    }

    fun createIfNotExists(): Boolean {
        return if (!exists()) create() else true
    }

    fun load(): YamlFile {
        if(exists()) {
            val yamlFactory = YAMLFactory()
            val yamlParser = yamlFactory.createParser(File(path))
            val yamlMapper = ObjectMapper(yamlFactory)
            val map = try { yamlMapper.readValue(yamlParser, LinkedHashMap::class.java) ?: return this } catch (e: Exception) { return this }
            map.forEach { (key, value) ->
                this.content[key.toString()] = YamlObject(value)
            }
        }else {
            throw Exception("File not found")
        }

        return this
    }

    fun header(string: String): YamlFile {
        this.header = "# $string".split("\n").joinToString("\n# ", postfix = "\n")
        return this
    }

    fun header(vararg strings: String): YamlFile {
        this.header = strings.joinToString("\n# ", prefix = "# ", postfix = "\n")
        return this
    }

    fun footer(string: String): YamlFile {
        this.footer = "# $string".split("\n").joinToString("\n# ", postfix = "\n")
        return this
    }

    fun footer(vararg strings: String): YamlFile {
        this.footer = strings.joinToString("\n# ", prefix = "# ", postfix = "\n")
        return this
    }

    fun save(): Boolean {
        val yamlFactory = YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        val yamlMapper = ObjectMapper(yamlFactory)
        val file = File(path)
        file.writeText("")
        file.appendText(header)
        file.appendText("\n")

        file.appendText(yamlMapper.writeValueAsString(this.content.mapValues {
            var value = it.value.get()
            if (value is YamlObject) {
                value = value.get()
            }
            value
        }))

        file.appendText("\n")
        file.appendText(footer)

        return true
    }

    fun toJson(): JsonFile {
        val jsonFile = JsonFile(path.replace(".yaml", ".json"))
        jsonFile.createIfNotExists()
        jsonFile.load()
        jsonFile.putAll(content.mapValues {
            var value = it.value.get()
            if (value is YamlObject) {
                value = value.get()
            }
            value
        })
        return jsonFile
    }

    operator fun get(key: String): YamlObject {
        return content[key] ?: run {
            content[key] = YamlObject(mapOf<String, Any?>())
            content[key]!!
        }
    }

    fun setFileObj(obj: Any) {
        val yamlFactory = YAMLFactory()
        val yamlMapper = ObjectMapper(yamlFactory)
        yamlMapper.writeValue(File(path), obj)
        load()
    }

    fun getFileObj(clazz: Class<*>): Any {
        val yamlFactory = YAMLFactory()
        val yamlParser = yamlFactory.createParser(File(path))
        val yamlMapper = ObjectMapper(yamlFactory)
        return yamlMapper.readValue(yamlParser, clazz)
    }

     inline fun <reified T> getFileObj(): T {
        val yamlFactory = YAMLFactory()
        val yamlParser = yamlFactory.createParser(File(this.path))
        val yamlMapper = ObjectMapper(yamlFactory)
        return yamlMapper.readValue(yamlParser, T::class.java)
    }

    inline fun <reified T> getObj(key: String): T {
        val yaml = YAMLFactory()
        val mapper = ObjectMapper(yaml)
        mapper.findAndRegisterModules();
        return mapper.readValue(mapper.writeValueAsString(content[key]), T::class.java)
    }

    fun getObj2(key: String, clazz: Class<*>): Any {
        val yaml = YAMLFactory()
        val mapper = ObjectMapper(yaml)
        mapper.findAndRegisterModules();
        return mapper.readValue(mapper.writeValueAsString(content[key]), clazz)
    }

    operator fun get(index: Int): YamlObject {
        return content[index.toString()] ?: run {
            content[index.toString()] = YamlObject(mapOf<String, Any?>())
            content[index.toString()]!!
        }
    }

    operator fun get(key: String, default: Any?): YamlObject {
        return content[key] ?: run {
            content[key] = YamlObject(default)
            content[key]!!
        }
    }

    operator fun set(key: String, value: Any?) {
        content[key] = YamlObject(value)
    }

    fun keys(): Set<String> {
        return content.keys
    }

    fun size(): Int {
        return content.size
    }

    fun remove(key: String): YamlObject {
        return content.remove(key) ?: YamlObject(mapOf<String, Any?>())
    }

    fun clear() {
        content.clear()
    }

    fun containsKey(key: String): Boolean {
        return content.containsKey(key)
    }

    fun containsValue(value: Any?): Boolean {
        return content.containsValue(YamlObject(value))
    }

    fun isEmpty(): Boolean {
        return content.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return content.isNotEmpty()
    }
}