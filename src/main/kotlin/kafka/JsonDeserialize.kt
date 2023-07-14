package kafka

import com.google.gson.Gson
import org.apache.kafka.common.serialization.Deserializer

class JsonDeserialize<T> : Deserializer<T> {

    private lateinit var type: Class<T>

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        super.configure(configs, isKey)
        val typeName = configs?.get(TYPE_CONFIG).toString()
        this.type = Class.forName(typeName) as Class<T>

    }

    override fun deserialize(topic: String?, data: ByteArray?): T {
        return Gson().fromJson(String(data ?: "".toByteArray()), type)
    }

    companion object {
        const val TYPE_CONFIG: String = "br.com.type"
    }
}