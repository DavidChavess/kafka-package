package kafka.serialization

import com.google.gson.*
import kafka.Message
import java.lang.reflect.Type

class MessageAdapter : JsonSerializer<Message<*>>, JsonDeserializer<Message<*>> {

    override fun serialize(message: Message<*>, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val json = JsonObject()
        if (message.payload != null)
            json.addProperty("type", message.payload::class.java.name)
        json.add("payload", context?.serialize(message.payload))
        json.add("correlationId", context?.serialize(message.correlationId))
        return json
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Message<*> {
        val obj = json?.asJsonObject
        if (obj != null && context != null) {
            val className = Class.forName(obj.get("type").asString)
            val correlationId = context.deserialize<String>(obj.get("correlationId"), String::class.java)
            val payload = context.deserialize<Any>(obj.get("payload"), className)
            return Message(correlationId, payload)
        }
        throw Exception("Can´t i deserialize")
    }
}
