package com.hayden.tracing_aspect.observation_aspects

import com.hayden.tracing_aspect.model.*
import com.hayden.tracing_aspect.Logged
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
open class CdcObservabilityAspect(
    private val observation: ObservationBehavior
) {


    @Around("@annotation(logged)")
    @Throws(Throwable::class)
    public open fun doCdc(joinPoint: ProceedingJoinPoint, logged: Logged): Any? {
        return observation.doObservation(
            ObservationBehavior.LoggedObservationArgs(
                ObservationUtility.AdviceJoinPoint.from(joinPoint),
                logged,
                logged.monitoringTypes.toList(),
                joinPoint,
            )
        )
    }

}
