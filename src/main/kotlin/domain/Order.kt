package domain

import java.math.BigDecimal

data class Order(
    val userId: String,
    val orderId: String,
    val total: BigDecimal,
    val name: String,
    val email: String
)