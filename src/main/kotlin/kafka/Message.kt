package kafka

data class Message<T>(
    val correlationId: String,
    val payload: T
) {
    fun continueWithCorrelationId(id: String): String = "${this.correlationId}, $id"
}