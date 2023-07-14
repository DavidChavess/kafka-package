package kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.io.Closeable
import java.lang.Thread.sleep
import java.time.Duration
import java.util.Properties
import java.util.regex.Pattern

class ConsumerUtil<Value>(
    private val topic: String,
    private val groupId: String,
    private val type: Class<Value>
) : Closeable {

    private val consumer = KafkaConsumer<String, Value>(properties())

    fun execute(callback: (Value) -> Unit) {
        consumer.subscribe(Pattern.compile(topic))

        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            if (!records.isEmpty) {
                records.forEach {
                    println("-----------------------")
                    println("Processing new message")
                    println("Record Key: ${it.key()}")
                    println("Value: ${it.value()}")
                    println("Part: ${it.partition()}")
                    println("OffSet: ${it.offset()}")
                    println("Topic: ${it.topic()}")
                    println("Message processed!")
                    callback(it.value())
                }
            }
            sleep(5000)
        }
    }

    private fun properties(): Properties {
        val props = Properties()
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9094")
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1")
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserialize::class.java.name)
        props.setProperty(JsonDeserialize.TYPE_CONFIG, type.name)
        return props
    }

    override fun close() {
        consumer.close()
    }
}
