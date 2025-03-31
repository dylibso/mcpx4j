package com.dylibso.mcpx4j.examples.gemini

import com.google.ai.client.generativeai.type.Schema
import org.json.JSONObject

data class ParsedSchema(val parameters: List<Schema<*>>, val requiredParameters: List<String>) {
    companion object {
        fun parse(schema: String): ParsedSchema {
            val parsedJsonSchema = JSONObject(schema)

            return parseObject(parsedJsonSchema)
        }

        fun parseObject(parsedJsonSchema: JSONObject): ParsedSchema {
            // Handle scalar types at the top level
            if (parsedJsonSchema.has("type") && !parsedJsonSchema.has("properties")) {
                val type = parsedJsonSchema.getString("type")
                val description = parsedJsonSchema.optString("description", type)
                val name =
                    parsedJsonSchema.optString("name", "value") // Default name if not specified

                val parameter = createSchemaForType(type, name, description, parsedJsonSchema)
                return ParsedSchema(listOf(parameter), emptyList())
            }

            // Handle object type with properties
            val properties = parsedJsonSchema.optJSONObject("properties")
            val required = try {
                parsedJsonSchema.getJSONArray("required").let { array ->
                    List(array.length()) { array.getString(it) }
                }
            } catch (e: Exception) {
                emptyList()
            }

            if (properties == null) {
                return ParsedSchema(emptyList(), emptyList())
            }

            val parameters = mutableListOf<Schema<*>>()

            for (key in properties.keys()) {
                val property = properties.getJSONObject(key)
                var type = "object"
                if (property.has("type")) {
                    type = property.getString("type")
                }
                val description = property.optString("description", type)

                val schema = createSchemaForType(type, key, description, property)
                parameters.add(schema)
            }

            return ParsedSchema(parameters, required)
        }

        private fun createSchemaForType(
            type: String,
            name: String,
            description: String,
            jsonObject: JSONObject
        ): Schema<*> {
            return when (type) {
                "string" -> {
                    if (jsonObject.has("enum")) {
                        val enumValues = jsonObject.getJSONArray("enum").let { array ->
                            List(array.length()) { array.getString(it) }
                        }
                        Schema.enum(name, description, enumValues)
                    } else {
                        Schema.str(name, description)
                    }
                }

                "integer" -> {
                    val format = jsonObject.optString("format", "")
                    when (format) {
                        "int32" -> Schema.int(name, description)
                        "int64" -> Schema.long(name, description)
                        else -> Schema.int(name, description)
                    }
                }

                "number" -> Schema.double(name, description)
                "boolean" -> Schema.bool(name, description)
                "array" -> {
                    val items = jsonObject.optJSONObject("items")
                    if (items != null) {
                        // Recursive call to handle array items
                        val (itemParameters, _) = parse(items.toString())
                        Schema.arr(
                            name, description,
                            itemParameters.firstOrNull() as Schema<out Any>?
                        )
                    } else {
                        Schema.arr(name, description)
                    }
                }

                "object" -> {
                    val nestedProperties = jsonObject.optJSONObject("properties")
                    if (nestedProperties != null) {
                        // Recursive call to handle nested objects
                        val (nestedParameters, _) = parse(jsonObject.toString())
                        val arr = nestedParameters.toTypedArray() as Array<Schema<Any>>
                        Schema.obj(name, description, *arr)
                    } else {
                        Schema.obj(name, description)
                    }
                }

                else -> Schema.str(name, description) // Default to string for unknown types
            }
        }
    }
}