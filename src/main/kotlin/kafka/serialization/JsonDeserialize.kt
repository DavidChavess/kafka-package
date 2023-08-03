package kafka.serialization

import com.google.gson.GsonBuilder
import kafka.Message
import org.apache.kafka.common.serialization.Deserializer

class JsonDeserialize : Deserializer<Message<*>> {

    override fun deserialize(topic: String?, data: ByteArray?): Message<*> {
        return GsonBuilder()
            .registerTypeAdapter(Message::class.java, MessageAdapter())
            .create()
            .fromJson(String(data ?: "".toByteArray()), Message::class.java)
    }
}