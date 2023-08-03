package kafka.serialization

import com.google.gson.GsonBuilder
import kafka.Message
import org.apache.kafka.common.serialization.Serializer

class JsonSerialize : Serializer<Message<*>> {
    override fun serialize(topic: String?, data: Message<*>): ByteArray {
        return GsonBuilder()
            .registerTypeAdapter(Message::class.java, MessageAdapter())
            .create()
            .toJson(data)
            .toByteArray()
    }
}