package com.hayden.tracing_aspect.observation_aspects

import com.hayden.tracing_aspect.model.Trace

/**
 * Save to the database.
 */
interface MessageCapture {

    fun mapMessage(trace: Trace): Trace

    class DefaultMessageCapture: MessageCapture {
        override fun mapMessage(trace: Trace): Trace {
            return trace
        }

    }

}