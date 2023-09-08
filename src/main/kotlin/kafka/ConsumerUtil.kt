package kafka

import java.io.Closeable
import java.lang.Thread.sleep
import java.time.Duration
import java.util.Properties
import java.util.UUID
import java.util.regex.Pattern
import kafka.serialization.JsonDeserialize
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

class ConsumerUtil<Value>(
    private val topic: String,
    private val groupId: String,
    private val overrideProperties: Map<String, String> = mapOf()
) : Closeable {

    private val consumer = KafkaConsumer<String, Message<Value>>(properties())
    private val dlqProducer = ProducerUtil<DlqMessage<Value>>()

    fun execute(callback: (Message<Value>) -> Unit) {
        consumer.subscribe(Pattern.compile(topic))
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            if (!records.isEmpty) {
                records.forEach {
                    println("-----------------------")
                    println("Processing new message...")
                    println("Record Key: ${it.key()}")
                    println("Value: ${it.value()}")
                    println("Part: ${it.partition()}")
                    println("OffSet: ${it.offset()}")
                    println("Topic: ${it.topic()}")
                    println("Message processed!")
                    try {
                        callback(it.value())
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        sendToDlq(it)
                    }
                }
            }
            sleep(5000)
        }
    }

    private fun sendToDlq(consumerRecord: ConsumerRecord<String, Message<Value>>) {
        val message = consumerRecord.value()
        val dlqTopic = "${consumerRecord.topic()}_DLQ"
        val correlationId = "DeadLetter(${UUID.randomUUID()}), ${message.correlationId}"

        println("Send message ${message.payload} to DLQ $dlqTopic")

        val dlqMessage = DlqMessage(correlationId, consumerRecord.topic(), message.payload, consumerRecord.timestamp())

        dlqProducer.sendAsync(
            dlqTopic,
            consumerRecord.key(),
            Message(correlationId, dlqMessage)
        )
    }

    private fun properties(): Properties {
        val props = Properties()
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9094, 127.0.0.1:8098, 127.0.0.1:8099")
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        props.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1")
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserialize::class.java.name)
        props.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString())
        props.putAll(overrideProperties)
        return props
    }

    override fun close() {
        consumer.close()
    }
}
