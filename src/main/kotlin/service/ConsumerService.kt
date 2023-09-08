package service

import kafka.Message

interface ConsumerService<T> {
    fun topic(): String
    fun consumerGroup(): String
    fun parse(message: Message<T>): Message<T>
}