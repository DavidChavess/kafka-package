package kafka

import kafka.serialization.JsonSerialize
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.io.Closeable
import java.util.Properties
import java.util.UUID

class ProducerUtil<Value>(
    private val overrideProperties: Map<String, String> = mapOf()
) : Closeable {

    private val producer = KafkaProducer<String, Message<Value>>(properties())

    fun send(topic: String, message: Message<Value>) {
        val key = UUID.randomUUID().toString()
        send(topic, key, message)
    }

    fun send(topic: String, key: String, message: Message<Value>) {
        val callback = Callback { metadata, exception ->
            if (exception != null) {
                println("Erro ao enviar msg")
                exception.printStackTrace()
            }
            println("Sucesso: Msg enviada = ${metadata.topic()} ::: ${metadata.partition()} / ${metadata.offset()} / ${metadata.timestamp()}")
        }
        val record = ProducerRecord(topic, key, message)
        producer.send(record, callback).get()
    }

    private fun properties(): Properties {
        val props = Properties()
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9094, 127.0.0.1:8098, 127.0.0.1:8099")
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerialize::class.java.name)
        props.setProperty(ProducerConfig.ACKS_CONFIG, "all")
        props.putAll(overrideProperties)
        return props
    }

    override fun close() {
        producer.close()
    }

}