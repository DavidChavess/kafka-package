package service

import kafka.ConsumerUtil
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors.newFixedThreadPool

class RunnerService<T>(
    private val service: ConsumerService<T>,
) {
    fun start(threadCount: Int) {
        val threadPool = newFixedThreadPool(threadCount)
        val dispatcher = threadPool.asCoroutineDispatcher()
        repeat(threadCount) {
            CoroutineScope(dispatcher).launch {
                ConsumerUtil<T>(
                    service.topic(),
                    service.consumerGroup()
                ).execute(service::parse)
            }
        }
    }
}