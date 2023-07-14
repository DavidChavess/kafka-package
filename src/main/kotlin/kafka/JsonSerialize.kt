package kafka

import com.google.gson.Gson
import org.apache.kafka.common.serialization.Serializer

class JsonSerialize<T> : Serializer<T> {
    override fun serialize(topic: String?, data: T): ByteArray {
        return Gson().toJson(data).toByteArray()
    }
}