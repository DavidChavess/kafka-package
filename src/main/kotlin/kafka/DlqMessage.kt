package kafka

data class DlqMessage<T>(
    val correlationId: String,
    val topic: String,
    val payload: T,
    val timestamp: Long
) {
}