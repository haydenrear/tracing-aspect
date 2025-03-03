package com.hayden.tracing_aspect.model

import org.springframework.boot.logging.LogLevel
import java.time.Instant

enum class LogType {
    MESSAGE, LOG
}

data class ExecutionMetadata(val traceId: String, val threadId: String?, val executionId: String?)

data class ServiceIds(val serviceInstanceId: String, val serviceId: String)

data class TraceMetadata(val serviceId: ServiceIds, val executionMetadata: ExecutionMetadata?) {
    constructor(serviceId: ServiceIds) : this(serviceId, null)
}

data class MessageMetadata(val traceId: TraceMetadata, val logType: LogType, val level: LogLevel?) {
    constructor(traceId: TraceMetadata, logType: LogType): this(traceId, logType, null)
}

data class Message(val dictionary: Map<String, *>?, val top: String?)

data class Trace(val time: Instant, val metadata: MessageMetadata, val log: Message)