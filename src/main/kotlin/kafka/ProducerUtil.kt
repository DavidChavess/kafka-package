package kafka

import java.io.Closeable
import java.util.Properties
import java.util.UUID
import java.util.concurrent.Future
import kafka.serialization.JsonSerialize
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer

class ProducerUtil<Value>(
    private val overrideProperties: Map<String, String> = mapOf()
) : Closeable {

    private val producer = KafkaProducer<String, Message<Value>>(properties())

    fun sendAsync(topic: String, key: String = UUID.randomUUID().toString(), message: Message<Value>) {
        sendMessage(topic, key, message)
    }

    fun send(topic: String, key: String = UUID.randomUUID().toString(), message: Message<Value>) {
        sendMessage(topic, key, message)?.get()
    }

    private fun sendMessage(topic: String, key: String, message: Message<Value>): Future<RecordMetadata>? {
        val callback = Callback { metadata, exception ->
            if (exception != null) {
                println("Erro ao enviar msg")
                exception.printStackTrace()
            }
            println("Sucesso: Msg enviada = ${metadata.topic()} ::: part: ${metadata.partition()} / off:${metadata.offset()} / tim:${metadata.timestamp()}")
        }
        val record = ProducerRecord(topic, key, message)
        return producer.send(record, callback)
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