package com.hayden.tracing_aspect.observation_aspects

import com.fasterxml.jackson.databind.ObjectMapper
import com.hayden.tracing_aspect.Logged
import com.hayden.tracing_aspect.model.*
import com.hayden.tracing_aspect.props.TracingConfigurationProperties
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.function.Supplier

@Component
open class ObservationBehavior(
    val observationRegistry: ObservationRegistry,
    val loggedObservabilityUtility: ObservationUtility<LoggedObservationArgs>,
    val observabilityUtility: ObservationUtility<ObservationUtility.JoinPointObservationArgs>,
    val tracingProps: TracingConfigurationProperties,
    val om: ObjectMapper) {


    data class LoggedObservationArgs(
        override val advice: ObservationUtility.AdviceJoinPoint,
        val logged: Logged,
        override val monitoringTypes: List<MonitoringTypes>,
        val proceedingJoinPoint: ProceedingJoinPoint
    ) : ObservationUtility.JoinPointObservationArgs {
        override fun proceed(): Any? {
            return proceedingJoinPoint.proceed()
        }
    }


    data class DiAnnotationObservationArgs(
        override val advice: ObservationUtility.AdviceJoinPoint,
        override val monitoringTypes: List<MonitoringTypes>,
        val proceedingJoinPoint: ProceedingJoinPoint
    ) : ObservationUtility.JoinPointObservationArgs {
        override fun proceed(): Any? {
            return proceedingJoinPoint.proceed()
        }
    }

    data class AgentObservationArgs(
        override val monitoringTypes: List<MonitoringTypes>,
        override val advice: ObservationUtility.AdviceJoinPoint
    ): ObservationUtility.JoinPointObservationArgs {
        override fun proceed(): Any? {
            return null
        }
    }


    fun doObservation(observationArgs: ObservationUtility.ObservationArgs): Any? {
        return when (observationArgs) {
            is LoggedObservationArgs -> doDelegateObserve(observationArgs, loggedObservabilityUtility)
            is ObservationUtility.JoinPointObservationArgs -> doDelegateObserve(observationArgs, observabilityUtility)
            else -> null
        }
    }

    private fun <T: ObservationUtility.JoinPointObservationArgs> doDelegateObserve(
        observationArgs: T, observationUtility: ObservationUtility<T>
    ): Any? {

        val trace = Trace(
            Instant.now(),
            MessageMetadata(TraceMetadata(ServiceIds(tracingProps.serviceInstanceId ?: "",  tracingProps.serviceId ?: "")), LogType.MESSAGE),
            Message(observationUtility.extractTrace(observationArgs), observationArgs.id)
        )

        observationUtility.consumer(observationArgs, trace)

        // Observation context @tracing_apt.Logged calls DelegatingCdcObservationHandler
        // to save event data in the database.
        val out = Observation.createNotStarted(observationArgs.id, observationRegistry)
            .highCardinalityKeyValue("trace", om.writeValueAsString(trace))
            .highCardinalityKeyValue("data", om.writeValueAsString(observationUtility.extractData(observationArgs)))

        return if (observationArgs.advice.advice.joinPointAction == ObservationUtility.JoinPointAction.Enter) {
            out.observe(Supplier { observationArgs.proceed() })
        } else {
            out.observe(Supplier { observationArgs.proceed() })
        }

    }

}