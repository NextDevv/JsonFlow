package it.unilix.json

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.concurrent.CompletableFuture

class JsonUrl(private val url: String, private val method: HttpMethod) {
    private val client = HttpClient(CIO)
    private var bodyRequest = ""
    private var headers = mutableMapOf<String, String>()

    @OptIn(DelicateCoroutinesApi::class)
    fun open(): Deferred<JsonObject> {
        var jsonObject: JsonObject
        val deferred = GlobalScope.async {
            when(method) {
                HttpMethod.Get -> {
                    val response = client.get(url) {
                        setBody(bodyRequest)
                        headers {
                            this@JsonUrl.headers.forEach { (key, value) ->
                                append(key, value)
                            }
                        }
                    }

                    jsonObject = JsonString.fromString(response.bodyAsText())
                    return@async jsonObject
                }
                HttpMethod.Post -> {
                    val response = client.post(url) {
                        setBody(bodyRequest)
                        headers {
                            this@JsonUrl.headers.forEach { (key, value) ->
                                append(key, value)
                            }
                        }
                    }

                    jsonObject = JsonString.fromString(response.bodyAsText())
                    return@async jsonObject
                }
                HttpMethod.Put -> {
                    val response = client.put(url) {
                        setBody(bodyRequest)
                        headers {
                            this@JsonUrl.headers.forEach { (k, v) ->
                                append(k, v)
                            }
                        }
                    }

                    jsonObject = JsonString.fromString(response.bodyAsText())
                    return@async jsonObject
                }
                HttpMethod.Delete -> {
                    val response = client.delete(url) {
                        setBody(bodyRequest)
                        headers {
                            this@JsonUrl.headers.forEach { (k, v) ->
                                append(k, v)
                            }
                        }
                    }

                    jsonObject = JsonString.fromString(response.bodyAsText())
                    return@async jsonObject
                }
                else -> {
                    throw Exception("Method not supported")
                }
            }
        }
        return deferred
    }

    fun setBody(body: String): JsonUrl {
        this.bodyRequest = body
        return this
    }

    fun setHeader(key: String, value: String): JsonUrl {
        headers[key] = value
        return this
    }
}